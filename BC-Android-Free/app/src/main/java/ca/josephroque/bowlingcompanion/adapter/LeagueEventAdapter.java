package ca.josephroque.bowlingcompanion.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.LeagueEventActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-02-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 *
 * Be aware, any documentation or code which simply refers to either a "League" or
 * "Event" should be assumed to imply it references the other kind as well
 */
public class LeagueEventAdapter extends RecyclerView.Adapter<LeagueEventAdapter.LeagueEventViewHolder>
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "LeagueEventAdapter";

    /** Instance of activity which created instance of this object */
    private LeagueEventActivity mLeagueEventActivity;

    /** List of league ids from "league" table in database to uniquely identify leagues */
    private List<Long> mListLeagueEventIds;
    /** List of league names which will be displayed by RecyclerView */
    private List<String> mListLeagueEventNames;
    /** List of league average which will be displayed by RecyclerView */
    private List<Short> mListLeagueEventAverages;
    /** List of number of games in leagues so series with proper number of games can be created */
    private List<Byte> mListLeagueEventNumberOfGames;

    /** Indicates whether this object is being used to display events or not */
    private boolean mEventMode;

    /**
     * Subclass of RecyclerView.ViewHolder to manage views which will display
     * text to the user.
     */
    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays name of league or event */
        private TextView mTextViewLeagueEventName;
        /** Displays average score of league or event */
        private TextView mTextViewLeagueEventAverage;
        private ValueAnimator mValueAnimator = null;

        /**
         * Calls super constructor with itemLayoutView as parameter and retrieves
         * references to TextView for member variables
         *
         * @param itemLayoutView
         */
        private LeagueEventViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewLeagueEventName = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueEventAverage = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_average);
        }
    }

    /**
     * Stores references to parameters in member variables
     *
     * @param activity activity which created this object
     * @param listLeagueEventIds list of unique league ids from "league" table in database
     * @param listLeagueEventNames list of league names which correspond to order of listLeagueEventIds
     * @param listLeagueEventAverages list of league averages which correspond to order of listLeagueEventIds
     * @param listLeagueEventNumberOfGames list of number of games in league which correspond to order of listLeagueEventIds
     * @param eventMode indicates whether this object represents a list of events or not
     */
    public LeagueEventAdapter(
            LeagueEventActivity activity,
            List<Long> listLeagueEventIds,
            List<String> listLeagueEventNames,
            List<Short> listLeagueEventAverages,
            List<Byte> listLeagueEventNumberOfGames,
            boolean eventMode)
    {
        this.mLeagueEventActivity = activity;
        this.mListLeagueEventIds = listLeagueEventIds;
        this.mListLeagueEventNames = listLeagueEventNames;
        this.mListLeagueEventAverages = listLeagueEventAverages;
        this.mListLeagueEventNumberOfGames = listLeagueEventNumberOfGames;
        this.mEventMode = eventMode;
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leagues_events, parent, false);
        return new LeagueEventViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final LeagueEventViewHolder holder, final int position)
    {
        /*
         * Sets text of TextView objects to display league names and averages to user
         */
        holder.mTextViewLeagueEventName.setText(mListLeagueEventNames.get(position));
        holder.mTextViewLeagueEventAverage.setText("Avg: "
                + String.valueOf(mListLeagueEventAverages.get(position)));

        /*
         * Below methods are executed when an item in the RecyclerView is
         * clicked or long clicked.
         */
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new OpenLeagueEventSeriesTask().execute(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showDeleteLeagueOrEventDialog(position);
                return true;
            }
        });
        holder.itemView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    holder.mValueAnimator =
                            ValueAnimator.ofObject(new ArgbEvaluator(),
                                    Theme.getListItemBackground(),
                                    Theme.getLongPressThemeColor());
                    holder.mValueAnimator.setDuration(Theme.getMediumAnimationDuration());
                    holder.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            v.setBackgroundColor((Integer)animation.getAnimatedValue());
                        }
                    });
                    holder.mValueAnimator.start();
                }
                else if (event.getActionMasked() == MotionEvent.ACTION_UP && holder.mValueAnimator != null)
                {
                    holder.mValueAnimator.cancel();
                    holder.mValueAnimator = null;
                    v.setBackgroundColor(Theme.getListItemBackground());
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mListLeagueEventIds.size();
    }

    /**
     * Prompts user with a dialog to delete all data regarding a certain
     * league or event in the database
     *
     * @param position position of league id in mListLeagueEventIds
     */
    private void showDeleteLeagueOrEventDialog(final int position)
    {
        final String leagueName = mListLeagueEventNames.get(position);
        final long leagueID = mListLeagueEventIds.get(position);

        /*
         * There is a default league "Open" which is created along with a new bowler
         * and cannot be deleted - this conditional prevents its removal
         */
        if (leagueName.equals(Constants.NAME_LEAGUE_OPEN))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mLeagueEventActivity);
            builder.setMessage("The league \"" + leagueName + "\" cannot be deleted.")
                    .setCancelable(false)
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        DatabaseHelper.deleteData(mLeagueEventActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteLeague(leagueID);
                    }
                },
                leagueName);
    }

    /**
     * Deletes all data regarding a certain league id in the database
     *
     * @param selectedLeagueID id of league whose data will be deleted
     */
    private void deleteLeague(final long selectedLeagueID)
    {
        //Removes league from RecyclerView immediately  UI doesn't hang
        final int indexOfId = mListLeagueEventIds.indexOf(selectedLeagueID);
        final String leagueName = mListLeagueEventNames.remove(indexOfId);
        mListLeagueEventAverages.remove(indexOfId);
        mListLeagueEventNumberOfGames.remove(indexOfId);
        mListLeagueEventIds.remove(indexOfId);
        notifyItemRemoved(indexOfId);

        if (mListLeagueEventIds.size() == 0)
        {
            mLeagueEventActivity.showNewLeagueEventInstructions(mEventMode);
        }

        //Deletion occurs on separate thread so UI does not hang
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Deletes data from all tables corresponding to selectedLeagueId
                SQLiteDatabase database = DatabaseHelper.getInstance(mLeagueEventActivity).getWritableDatabase();
                String[] whereArgs = {String.valueOf(selectedLeagueID)};
                database.beginTransaction();
                try
                {
                    database.delete(FrameEntry.TABLE_NAME,
                            FrameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting league: " + leagueName);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Creates a SeriesActivity to display the series of a league or event corresponding
     * to a league id selected by the user from the list displayed
     */
    private class OpenLeagueEventSeriesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            long selectedLeagueId = mListLeagueEventIds.get(position[0]);
            String selectedLeagueName = mListLeagueEventNames.get(position[0]);

            SQLiteDatabase database = DatabaseHelper.getInstance(mLeagueEventActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            if (!selectedLeagueName.equals(Constants.NAME_LEAGUE_OPEN))
            {
                //Updates the date modified in the database of the selected league
                ContentValues values = new ContentValues();
                values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, currentDate);

                database.beginTransaction();
                try
                {
                    database.update(LeagueEntry.TABLE_NAME,
                            values,
                            LeagueEntry._ID + "=?",
                            new String[]{String.valueOf(selectedLeagueId)});
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Error updating league: " + ex.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }
            }

            /*
             * If an event was selected by the user the corresponding series of the event is
             * loaded from the database so an instance of GameActivity can be created, since
             * creating a SeriesActivity would be redundant for a single series.
             */
            if (mEventMode)
            {
                String rawSeriesQuery = "SELECT "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                        + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid, "
                        + SeriesEntry.COLUMN_NAME_DATE_CREATED
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                        + " LEFT JOIN " + SeriesEntry.TABLE_NAME
                        + " ON league." + LeagueEntry._ID + "=" + SeriesEntry.COLUMN_NAME_LEAGUE_ID
                        + " WHERE league." + LeagueEntry._ID + "=?";
                String[] rawSeriesArgs = {String.valueOf(selectedLeagueId)};

                Cursor cursor = null;
                byte numberOfGames = -1;
                long seriesId = -1;
                String seriesDate = null;
                try
                {
                    cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
                    cursor.moveToFirst();
                    numberOfGames = (byte) cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                    seriesId = cursor.getLong(cursor.getColumnIndex("sid"));
                    seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_NAME_DATE_CREATED));
                }
                finally
                {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                return new Object[]{seriesId, numberOfGames, selectedLeagueId, selectedLeagueName, seriesDate};
            }
            else
            {
                return new Object[]{mListLeagueEventNumberOfGames.get(position[0]), selectedLeagueId, selectedLeagueName};
            }
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            if (mEventMode)
            {
                /*
                 * If an event was selected, creates an instance of GameActivity
                 * displaying the event's corresponding series
                 */
                long seriesId = (Long)params[0];
                byte numberOfGames = (Byte)params[1];
                long leagueId = (Long)params[2];
                String leagueName = params[3].toString();
                String seriesDate = params[4].toString();

                SeriesActivity.openEventSeries(
                        mLeagueEventActivity,
                        seriesId,
                        numberOfGames,
                        mLeagueEventActivity.getBowlerId(),
                        mLeagueEventActivity.getBowlerName(),
                        leagueId,
                        leagueName,
                        seriesDate);
            }
            else
            {
                /*
                 * If a league was selected, creates an instance of SeriesActivity
                 * to display all available series in the league
                 */
                byte numberOfGames = (Byte)params[0];
                long leagueId = (Long)params[1];
                String leagueName = params[2].toString();

                if (!leagueName.equals(Constants.NAME_LEAGUE_OPEN))
                {
                    mLeagueEventActivity.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                            .edit()
                            .putLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, leagueId)
                            .putLong(Constants.PREFERENCE_ID_RECENT_BOWLER, mLeagueEventActivity.getBowlerId())
                            .apply();
                }

                Intent seriesIntent = new Intent(mLeagueEventActivity, SeriesActivity.class);
                seriesIntent.putExtra(Constants.EXTRA_NUMBER_OF_GAMES, numberOfGames);
                seriesIntent.putExtra(Constants.EXTRA_ID_BOWLER, mLeagueEventActivity.getBowlerId());
                seriesIntent.putExtra(Constants.EXTRA_ID_LEAGUE, leagueId);
                seriesIntent.putExtra(Constants.EXTRA_NAME_BOWLER, mLeagueEventActivity.getBowlerName());
                seriesIntent.putExtra(Constants.EXTRA_NAME_LEAGUE, leagueName);
                mLeagueEventActivity.startActivity(seriesIntent);
            }
        }
    }
}
