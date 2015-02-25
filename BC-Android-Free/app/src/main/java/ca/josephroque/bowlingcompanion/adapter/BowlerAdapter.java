package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.LeagueEventActivity;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class BowlerAdapter extends RecyclerView.Adapter<BowlerAdapter.BowlerViewHolder>
{
    private static final String TAG = "BowlerAdapter";

    private Activity mActivity;
    private List<String> mBowlerNames;
    private List<Short> mBowlerAverages;
    private List<Long> mBowlerIDs;

    public static class BowlerViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageViewBowlerOrTeam;
        private TextView mTextViewBowlerName;
        private TextView mTextViewBowlerAverage;

        private BowlerViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mImageViewBowlerOrTeam = (ImageView)itemLayoutView.findViewById(R.id.imageView_bowler_team);
            mTextViewBowlerName = (TextView)itemLayoutView.findViewById(R.id.textView_bowler_name);
            mTextViewBowlerAverage = (TextView)itemLayoutView.findViewById(R.id.textView_bowler_average);
        }
    }

    public BowlerAdapter(Activity context, List<Long> bowlerIDs, List<String> bowlerNames, List<Short> bowlerAverages)
    {
        mActivity = context;
        this.mBowlerIDs = bowlerIDs;
        this.mBowlerNames = bowlerNames;
        this.mBowlerAverages = bowlerAverages;
    }

    @Override
    public BowlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_bowlers, parent, false);

        return new BowlerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BowlerViewHolder holder, final int position)
    {
        holder.mImageViewBowlerOrTeam.setImageResource(R.drawable.ic_person);
        holder.mTextViewBowlerName.setText(mBowlerNames.get(position));
        holder.mTextViewBowlerAverage.setText("Avg: "
                + String.valueOf(mBowlerAverages.get(position)));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new OpenBowlerLeaguesTask().execute(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showDeleteBowlerDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mBowlerNames.size();
    }

    private void showDeleteBowlerDialog(final int position)
    {
        final String bowlerName = mBowlerNames.get(position);
        final long bowlerID = mBowlerIDs.get(position);

        DatabaseHelper.deleteData(mActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteBowler(bowlerID);
                    }
                },
                bowlerName);
    }

    private void deleteBowler(final long selectedBowlerID)
    {
        final int indexOfId = mBowlerIDs.indexOf(selectedBowlerID);
        final String bowlerName = mBowlerNames.remove(indexOfId);
        mBowlerIDs.remove(indexOfId);
        notifyItemRemoved(indexOfId);

        SharedPreferences preferences = mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE);
        long recentBowlerId = preferences.getLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1);
        long quickBowlerId = preferences.getLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1);

        if (recentBowlerId == selectedBowlerID)
        {
            preferences.edit()
                    .putLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1)
                    .putLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, -1)
                    .apply();
            recentBowlerId = -1;
        }
        if (quickBowlerId == selectedBowlerID)
        {
            preferences.edit()
                    .putLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1)
                    .putLong(Constants.PREFERENCE_ID_QUICK_LEAGUE, -1)
                    .apply();
            quickBowlerId = -1;
        }
        MainActivity.sQuickSeriesButtonEnabled = !(recentBowlerId == -1) || !(quickBowlerId == -1);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
                String[] whereArgs = {String.valueOf(selectedBowlerID)};
                database.beginTransaction();
                try
                {
                    database.delete(FrameEntry.TABLE_NAME,
                            FrameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(BowlerEntry.TABLE_NAME,
                            BowlerEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting bowler: " + bowlerName);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    private class OpenBowlerLeaguesTask extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... position)
        {
            Long selectedBowlerID = mBowlerIDs.get(position[0]);

            SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));

            database.beginTransaction();
            try
            {
                database.update(BowlerEntry.TABLE_NAME,
                        values,
                        BowlerEntry._ID + "=?",
                        new String[]{String.valueOf(selectedBowlerID)});
                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error updating bowler: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE)
                    .edit()
                    .putString(Constants.PREFERENCE_NAME_BOWLER, mBowlerNames.get(position[0]))
                    .putLong(Constants.PREFERENCE_ID_BOWLER, selectedBowlerID)
                    .apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Intent leagueEventIntent = new Intent(mActivity, LeagueEventActivity.class);
            mActivity.startActivity(leagueEventIntent);
        }
    }
}
