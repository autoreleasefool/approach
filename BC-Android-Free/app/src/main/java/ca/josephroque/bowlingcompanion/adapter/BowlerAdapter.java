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
    /** Tag to identify class when outputting to console */
    private static final String TAG = "BowlerAdapter";

    /** Instance of activity which created instance of this object */
    private Activity mActivity;
    /** List of bowler names which will be displayed by RecyclerView */
    private List<String> mBowlerNames;
    /** List of bowler averages which will be displayed by RecyclerView */
    private List<Short> mBowlerAverages;
    /** List of bowler Ids from "bowler" table in database to uniquely identify bowlers */
    private List<Long> mBowlerIds;

    /**
     * Subclass of RecyclerView.ViewHolder to manage views which will display an
     * image and text to user.
     */
    public static class BowlerViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays an image representing type of bowler*/
        private ImageView mImageViewBowlerOrTeam;
        /** Displays name of bowler */
        private TextView mTextViewBowlerName;
        /** Displays average of bowler */
        private TextView mTextViewBowlerAverage;

        /**
         * Calls super constructor with itemLayoutView as parameter and retrieves
         * references to ImageView and TextView for member variables
         *
         * @param itemLayoutView
         */
        private BowlerViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mImageViewBowlerOrTeam = (ImageView)itemLayoutView.findViewById(R.id.imageView_bowler_team);
            mTextViewBowlerName = (TextView)itemLayoutView.findViewById(R.id.textView_bowler_name);
            mTextViewBowlerAverage = (TextView)itemLayoutView.findViewById(R.id.textView_bowler_average);
        }
    }

    /**
     * Stores references to parameters in member variables
     *
     * @param context activity which created this object
     * @param bowlerIds list of unique bowler ids from "bowler" table in database
     * @param bowlerNames list of bowler names which correspond to order of ids from bowlerIds
     * @param bowlerAverages list of bowler averages which correspond to order of ids from bowlerIds
     */
    public BowlerAdapter(Activity context, List<Long> bowlerIds, List<String> bowlerNames, List<Short> bowlerAverages)
    {
        mActivity = context;
        this.mBowlerIds = bowlerIds;
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
        /*
         * Sets image and text to represent individual bowler in list
         * to display to user.
         */
        holder.mImageViewBowlerOrTeam.setImageResource(R.drawable.ic_person);
        holder.mTextViewBowlerName.setText(mBowlerNames.get(position));
        holder.mTextViewBowlerAverage.setText("Avg: "
                + String.valueOf(mBowlerAverages.get(position)));

        /*
         * Below methods are executed when an item in the RecyclerView is
         * clicked or long clicked.
         */
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

    /**
     * Prompts user with a dialog to delete all data regarding a certain
     * bowler in the database
     *
     * @param position position of bowler id in mBowlerIds
     */
    private void showDeleteBowlerDialog(final int position)
    {
        final String bowlerName = mBowlerNames.get(position);
        final long bowlerID = mBowlerIds.get(position);

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

    /**
     * Deletes all data regarding a certain bowlerId in the database
     *
     * @param selectedBowlerID id of bowler whose data will be deleted
     */
    private void deleteBowler(final long selectedBowlerID)
    {
        //Removes bowler from RecyclerView immediately so UI does not hang
        final int indexOfId = mBowlerIds.indexOf(selectedBowlerID);
        final String bowlerName = mBowlerNames.remove(indexOfId);
        mBowlerIds.remove(indexOfId);
        notifyItemRemoved(indexOfId);

        /*
         * Gets recentBowlerId and quickBowlerId to check if the deleted bowler was either.
         * If so, the value is cleared.
         */
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

        //Deletes data on a new thread so UI does not hang
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Deletes data from all tables corresponding to selectedBowlerId
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

    /**
     * Creates a LeagueEventActivity to displays the leagues and events corresponding
     * to a bowler id selected by the user from the list displayed
     */
    private class OpenBowlerLeaguesTask extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... position)
        {
            //Gets id of bowler which was selected from position of selected item in list
            Long selectedBowlerID = mBowlerIds.get(position[0]);

            SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));

            //Updates bowler row in database to be most recently edited
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
