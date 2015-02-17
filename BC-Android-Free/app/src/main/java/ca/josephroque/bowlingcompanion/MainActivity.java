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
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

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
    /** Name of most recently edited bowler */
    private String mRecentBowlerName;
    /** Name of most recently edited league */
    private String mRecentLeagueName;
    /** Indicates whether the option to create a quick series is enabled or not */
    public static boolean sQuickSeriesButtonEnabled = false;

    /** Layout which shows the tutorial for the first time */
    private RelativeLayout mTutorialLayout = null;

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

        mTutorialLayout = (RelativeLayout)findViewById(R.id.main_tutorial_layout);
        if (hasShownTutorial())
        {
            mTutorialLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mRecentBowlerId = preferences.getLong(Constants.PREFERENCE_ID_RECENT_BOWLER, -1);
        mRecentLeagueId = preferences.getLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, -1);

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

        //Disables the quick series button if there is no recent bowler or league
        menu.findItem(R.id.action_quick_series).setEnabled(sQuickSeriesButtonEnabled);

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
            case R.id.action_new_bowler:
                //Action bar button pressed, new bowler
                showNewBowlerDialog();
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
     * Displays a tutorial overlay if one hasn't been shown to
     * the user yet
     *
     * @return true if the tutorial has already been shown, false otherwise
     */
    private boolean hasShownTutorial()
    {
        SharedPreferences mPreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        boolean mHasShownTutorial = mPreferences.getBoolean(Constants.PREFERENCE_TUTORIAL_MAIN, false);

        if (!mHasShownTutorial)
        {
            mPreferences.edit()
                    .putBoolean(Constants.PREFERENCE_TUTORIAL_MAIN, true)
                    .apply();
            mTutorialLayout.setVisibility(View.VISIBLE);
            mTutorialLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    mTutorialLayout.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
        }

        return mHasShownTutorial;
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
        AlertDialog.Builder mQuickSeriesBuilder = new AlertDialog.Builder(this);
        mQuickSeriesBuilder.setTitle("Quick Series")
                .setMessage("Create a new series with these settings:"
                        + "\nBowler: " + mRecentBowlerName
                        + "\nLeague: " + mRecentLeagueName)
                .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //TODO: uncomment line when SeriesActivity is complete
                        //SeriesActivity.addNewSeries(this, recentBowlerId, recentLeagueId, recentNumberOfGames);
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
                mBowlerAdapter.notifyDataSetChanged();
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
             * If there was a recently added
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
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mBowlerAdapter.notifyDataSetChanged();
            sQuickSeriesButtonEnabled = !(mRecentBowlerId == -1 || mRecentLeagueId == -1);
            invalidateOptionsMenu();
        }
    }
}
