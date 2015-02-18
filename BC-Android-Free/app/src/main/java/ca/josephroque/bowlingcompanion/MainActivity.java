package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.BowlerAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewBowlerDialog;

public class MainActivity extends ActionBarActivity
    implements NewBowlerDialog.NewBowlerDialogListener
{

    /** Tag to identify class when outputting to console */
    private static final String TAG = "MainActivity";

    /** List to store Ids from bowler table in database */
    private List<Long> mListBowlerIds;
    /** List to store names of bowlers from bowler table, in an order relative to mListBowlerIds*/
    private List<String> mListBowlerNames;
    /** List to store averages of bowlers, in an order relative to mListBowlerIds */
    private List<Integer> mListBowlerAverages;

    /** View to display bowler names and averages to user */
    private RecyclerView mBowlerRecycler;
    /** Adapter to manage data displayed in mBowlerRecycler */
    private RecyclerView.Adapter mBowlerAdapter;

    /** Id from 'bowler' database which represents the most recently edited bowler */
    private long mRecentBowlerId = -1;
    /** Id from  'league' database which represents the most recently edited bowler */
    private long mRecentLeagueId = -1;
    /** Number of games in the most recently selected league */
    private int mRecentNumberOfGames = -1;

    /** Id from 'bowler' database which represents the preferred bowler for a quick series */
    private long mQuickBowlerId = -1;
    /** Id from 'league' database which represents the preferred league for a quick series */
    private long mQuickLeagueId = -1;
    /** Number of games in the preferred league */
    private int mQuickNumberOfGames = -1;

    /** Name of most recently edited bowler */
    private String mRecentBowlerName;
    /** Name of most recently edited league */
    private String mRecentLeagueName;
    /** Name of the preferred bowler for a quick series */
    private String mQuickBowlerName;
    /** Name of the preferred league for a quick series */
    private String mQuickLeagueName;

    /** Indicates whether the option to create a quick series is enabled or not */
    public static boolean sQuickSeriesButtonEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(Color.parseColor(Constants.COLOR_BACKGROUND_PRIMARY));

        mListBowlerIds = new ArrayList<>();
        mListBowlerNames = new ArrayList<>();
        mListBowlerAverages = new ArrayList<>();

        mBowlerRecycler = (RecyclerView) findViewById(R.id.recyclerView_bowlers);
        mBowlerRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager mBowlerLayoutManager = new LinearLayoutManager(this);
        mBowlerRecycler.setLayoutManager(mBowlerLayoutManager);

        mBowlerAdapter = new BowlerAdapter(this, mListBowlerIds, mListBowlerNames, mListBowlerAverages);
        mBowlerRecycler.setAdapter(mBowlerAdapter);

        FloatingActionButton mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_bowler);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showNewBowlerDialog();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mRecentBowlerId = preferences.getLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1);
        mRecentLeagueId = preferences.getLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, -1);
        mQuickBowlerId = preferences.getLong(Constants.PREFERENCE_ID_QUICK_BOWLER, -1);
        mQuickLeagueId = preferences.getLong(Constants.PREFERENCE_ID_QUICK_LEAGUE, -1);

        //Clearing preferences which may indicate incorrect state of app
        preferences.edit()
                .remove(Constants.PREFERENCE_ID_BOWLER)
                .remove(Constants.PREFERENCE_ID_LEAGUE)
                .remove(Constants.PREFERENCE_ID_SERIES)
                .remove(Constants.PREFERENCE_ID_GAME)
                .apply();

        new LoadBowlerAndRecentTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_settings:
                //Action bar button pressed, open settings
                return true;
            case R.id.action_quick_series:
                //Action bar button pressed, quick series
                showQuickSeriesDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean mValidInput = true;
        String mInvalidInputMessage = null;

        if (bowlerName == null || bowlerName.length() == 0)
        {
            //No input for the name
            mValidInput = false;
            mInvalidInputMessage = "You must enter a name.";
        }
        else if (mListBowlerNames.contains(bowlerName))
        {
            //Bowler name already exists in the list
            mValidInput = false;
            mInvalidInputMessage = "That name has already been used. You must choose another.";
        }

        /*
         * If the input was invalid for any reason, a dialog is shown
         * to the user and the method does not continue
         */
        if (!mValidInput)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setMessage(mInvalidInputMessage)
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

        /*
         * Creates a new database entry for the bowler whose name
         * was received by input via the dialog
         */
        new NewBowlerTask().execute(bowlerName);
    }

    /**
     * Opens an instance of NewBowlerDialog and displays
     * it to the user
     */
    private void showNewBowlerDialog()
    {
        DialogFragment mDialogFragment = new NewBowlerDialog();
        mDialogFragment.show(getFragmentManager(), "NewBowlerDialog");
    }

    /**
     * Prompts user to create a new series with mRecentBowlerId
     * and mRecentLeagueId
     */
    private void showQuickSeriesDialog()
    {
        if (sQuickSeriesButtonEnabled)
        {
            final boolean quickOrRecent;
            AlertDialog.Builder mQuickSeriesBuilder = new AlertDialog.Builder(this);
            if (mQuickBowlerId == -1 || mQuickLeagueId == -1)
            {
                mQuickSeriesBuilder.setMessage("Create a new series with these settings:"
                    + "\nBowler: " + mRecentBowlerName
                    + "\nLeague: " + mRecentLeagueName);
                quickOrRecent = false;
            }
            else
            {
                mQuickSeriesBuilder.setMessage("Create a new series with these settings:"
                        + "\nBowler: " + mQuickBowlerName
                        + "\nLeague: " + mQuickLeagueName);
                quickOrRecent = true;
            }

            mQuickSeriesBuilder.setTitle("Quick Series")
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //TODO: uncomment line when SeriesActivity is complete
                            /*
                            if (quickOrRecent)
                            {
                                SeriesActivity.addNewSeries(MainActivity.this, mQuickBowlerId, mQuickLeagueId, mQuickNumberOfGames);
                            }
                            else
                            {
                                SeriesActivity.addNewSeries(MainActivity.this, mRecentBowlerId, mRecentLeagueId, mRecentNumberOfGames);
                            }
                             */
                        }
                    })
                    .setNegativeButton(Constants.DIALOG_CANCEL, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        else
        {
            AlertDialog.Builder mQuickSeriesDisabledBuilder = new AlertDialog.Builder(this);
            mQuickSeriesDisabledBuilder.setTitle("Quick Series")
                    .setMessage("With this button, you can quickly create a new series with"
                            + " your most recently used bowler/league, or set a specific"
                            + " bowler/league in the settings.")
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * AsyncTask to add a new bowler name to the database
     * and adds the name to be displayed in mBowlerRecycler
     * to the user
     */
    private class NewBowlerTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... bowlerName)
        {
            long mNewId = -1;
            SQLiteDatabase mDatabase
                    = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date mCurrentDate = new Date();

            ContentValues mBowlerValues = new ContentValues();
            mBowlerValues.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName[0]);
            mBowlerValues.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, mDateFormat.format(mCurrentDate));

            mDatabase.beginTransaction();
            try
            {
                mNewId = mDatabase.insert(BowlerEntry.TABLE_NAME, null, mBowlerValues);


                /*
                 * Creates an entry in the 'league' table for a default league
                 * for the new bowler being added
                 */
                ContentValues leagueValues = new ContentValues();
                leagueValues.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, Constants.NAME_LEAGUE_OPEN);
                leagueValues.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, mDateFormat.format(mCurrentDate));
                leagueValues.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, mNewId);
                leagueValues.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, 1);
                mDatabase.insert(LeagueEntry.TABLE_NAME, null, leagueValues);

                mDatabase.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new bowler: " + ex.getMessage());
            }
            finally
            {
                mDatabase.endTransaction();
            }

            return String.valueOf(mNewId) + ":" + bowlerName[0];
        }

        @Override
        protected void onPostExecute(String mBowlerIdAndName)
        {
            long mBowlerId = Long.parseLong(mBowlerIdAndName.substring(0, mBowlerIdAndName.indexOf(":")));
            String mBowlerName = mBowlerIdAndName.substring(mBowlerIdAndName.indexOf(":") + 1);

            /*
             * Adds the new bowler information to the corresponding lists
             * and displays them in the recycler view
             */
            if (mBowlerId != -1)
            {
                mListBowlerIds.add(0, mBowlerId);
                mListBowlerNames.add(0, mBowlerName);
                mListBowlerAverages.add(0, 0);
                mBowlerAdapter.notifyItemInserted(0);
                mBowlerRecycler.scrollToPosition(0);
            }
        }
    }

    /**
     * Loads the names of bowlers and other relevant data from the
     * database and displays it in mBowlerRecycler to the user
     */
    private class LoadBowlerAndRecentTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... parameters)
        {
            SQLiteDatabase mDatabase = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            //Gets name of all bowlers from database and their IDs
            String mRawBowlerQuery = "SELECT "
                    + "bowler." + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                    + "bowler." + BowlerEntry._ID + ", "
                    + "AVG(game." + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ") AS avg"
                    + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                    + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                    + " ON bowler." + BowlerEntry._ID + "=game." + GameEntry.COLUMN_NAME_BOWLER_ID
                    + " GROUP BY " + BowlerEntry.COLUMN_NAME_BOWLER_NAME;

            Cursor mCursor = mDatabase.rawQuery(mRawBowlerQuery, new String[]{});

            /*
             * Clears data from lists and adds Ids, names
             * and averages loaded in the cursor
             */
            mListBowlerIds.clear();
            mListBowlerNames.clear();
            mListBowlerAverages.clear();
            if (mCursor.moveToFirst())
            {
                while(!mCursor.isAfterLast())
                {
                    mListBowlerIds.add(mCursor.getLong(mCursor.getColumnIndex(BowlerEntry._ID)));
                    mListBowlerNames.add(mCursor.getString(mCursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME)));
                    mListBowlerAverages.add(mCursor.getInt(mCursor.getColumnIndex("avg")));
                    mCursor.moveToNext();
                }
            }

            /*
             * If there is a bowler and league which have been previously accessed
             * or a set bowler or league, then their corresponding values
             * are loaded to be used to create a quick series
             */
            if (mRecentBowlerId > -1 && mRecentLeagueId > -1)
            {
                String mRawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] mRawRecentArgs = new String[]{String.valueOf(mRecentBowlerId), String.valueOf(mRecentLeagueId)};

                mCursor = mDatabase.rawQuery(mRawRecentQuery, mRawRecentArgs);
                if (mCursor.moveToFirst())
                {
                    mRecentBowlerName = mCursor.getString(mCursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME));
                    mRecentLeagueName = mCursor.getString(mCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    mRecentNumberOfGames = mCursor.getInt(mCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                }
            }
            if (mQuickBowlerId > -1 && mQuickLeagueId > -1)
            {
                String mRawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] mRawRecentArgs = new String[]{String.valueOf(mQuickBowlerId), String.valueOf(mQuickLeagueId)};

                mCursor = mDatabase.rawQuery(mRawRecentQuery, mRawRecentArgs);
                if (mCursor.moveToFirst())
                {
                    mQuickBowlerName = mCursor.getString(mCursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME));
                    mQuickLeagueName = mCursor.getString(mCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    mQuickNumberOfGames = mCursor.getInt(mCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mBowlerAdapter.notifyDataSetChanged();
            sQuickSeriesButtonEnabled = !(mRecentBowlerId == -1 || mRecentLeagueId == -1) || !(mQuickBowlerId == -1 || mQuickLeagueId == -1);
        }
    }
}
