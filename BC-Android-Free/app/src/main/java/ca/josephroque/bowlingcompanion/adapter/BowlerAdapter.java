package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
    private List<Integer> mBowlerAverages;
    private List<Long> mBowlerIDs;

    public static class BowlerViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageViewBowlerOrTeam;
        public TextView textViewBowlerName;
        public TextView textViewBowlerAverage;

        public BowlerViewHolder(View mItemLayoutView)
        {
            super(mItemLayoutView);
            imageViewBowlerOrTeam = (ImageView)mItemLayoutView.findViewById(R.id.imageView_bowler_team);
            textViewBowlerName = (TextView)mItemLayoutView.findViewById(R.id.textView_bowler_name);
            textViewBowlerAverage = (TextView)mItemLayoutView.findViewById(R.id.textView_bowler_average);
        }
    }

    public BowlerAdapter(Activity context, List<Long> mBowlerIDs, List<String> mBowlerNames, List<Integer> mBowlerAverages)
    {
        mActivity = context;
        this.mBowlerIDs = mBowlerIDs;
        this.mBowlerNames = mBowlerNames;
        this.mBowlerAverages = mBowlerAverages;
    }

    @Override
    public BowlerViewHolder onCreateViewHolder(ViewGroup mParent, int mViewType)
    {
        View mItemView = LayoutInflater.from(mParent.getContext())
                .inflate(R.layout.list_bowlers, mParent, false);

        return new BowlerViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(BowlerViewHolder mHolder, final int mPosition)
    {
        mHolder.imageViewBowlerOrTeam.setImageResource(R.drawable.ic_person);
        mHolder.textViewBowlerName.setText(mBowlerNames.get(mPosition));
        mHolder.textViewBowlerAverage.setText(String.valueOf(mBowlerAverages.get(mPosition)));

        mHolder.itemView.setBackgroundColor(
                Color.parseColor(Constants.COLOR_BACKGROUND_SECONDARY));

        mHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new OpenBowlerLeaguesTask().execute(mPosition);
            }
        });

        mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showDeleteBowlerDialog(mPosition);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mBowlerNames.size();
    }

    private void showDeleteBowlerDialog(final int mPosition)
    {
        final String mBowlerName = mBowlerNames.get(mPosition);
        final long mBowlerID = mBowlerIDs.get(mPosition);

        DatabaseHelper.deleteData(mActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteBowler(mBowlerID);
                    }
                },
                mBowlerName);
    }

    private void deleteBowler(final long mSelectedBowlerID)
    {
        final int mIndexOfId = mBowlerIDs.indexOf(mSelectedBowlerID);
        final String mBowlerName = mBowlerNames.remove(mIndexOfId);
        mBowlerIDs.remove(mIndexOfId);
        notifyItemRemoved(mIndexOfId);

        SharedPreferences mPreferences = mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE);
        long mRecentBowlerId = mPreferences.getLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1);
        long mQuickBowlerId = mPreferences.getLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1);

        if (mRecentBowlerId == mSelectedBowlerID)
        {
            mPreferences.edit()
                    .putLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1)
                    .putLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, -1)
                    .apply();
            mRecentBowlerId = -1;
        }
        if (mQuickBowlerId == mSelectedBowlerID)
        {
            mPreferences.edit()
                    .putLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1)
                    .putLong(Constants.PREFERENCE_ID_QUICK_LEAGUE, -1)
                    .apply();
            mQuickBowlerId = -1;
        }
        MainActivity.sQuickSeriesButtonEnabled = !(mRecentBowlerId == -1) || !(mQuickBowlerId == -1);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase mDatabase = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
                String[] mWhereArgs = {String.valueOf(mSelectedBowlerID)};
                mDatabase.beginTransaction();
                try
                {
                    mDatabase.delete(FrameEntry.TABLE_NAME,
                            FrameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            mWhereArgs);
                    mDatabase.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            mWhereArgs);
                    mDatabase.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            mWhereArgs);
                    mDatabase.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            mWhereArgs);
                    mDatabase.delete(BowlerEntry.TABLE_NAME,
                            BowlerEntry._ID + "=?",
                            mWhereArgs);
                    mDatabase.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting bowler: " + mBowlerName);
                }
                finally
                {
                    mDatabase.endTransaction();
                }
            }
        }).start();
    }

    private class OpenBowlerLeaguesTask extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... mPosition)
        {
            Long mSelectedBowlerID = mBowlerIDs.get(mPosition[0]);

            SQLiteDatabase mDatabase = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues mValues = new ContentValues();
            mValues.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, mDateFormat.format(new Date()));

            mDatabase.beginTransaction();
            try
            {
                mDatabase.update(BowlerEntry.TABLE_NAME,
                        mValues,
                        BowlerEntry._ID + "=?",
                        new String[]{String.valueOf(mSelectedBowlerID)});
                mDatabase.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error updating bowler: " + ex.getMessage());
            }
            finally
            {
                mDatabase.endTransaction();
            }

            mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE)
                    .edit()
                    .putString(Constants.PREFERENCE_NAME_BOWLER, mBowlerNames.get(mPosition[0]))
                    .putLong(Constants.PREFERENCE_ID_BOWLER, mSelectedBowlerID)
                    .apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            //TODO: uncomment when LeagueActivity is created
            //Intent leagueIntent = new Intent(mActivity, LeagueActivity.class);
            //mActivity.startActivity(leagueIntent);
        }
    }
}
