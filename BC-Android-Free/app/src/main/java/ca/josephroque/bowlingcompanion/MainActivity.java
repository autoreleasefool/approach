package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.BowlerAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewBowlerDialog;
import ca.josephroque.bowlingcompanion.external.AppRater;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;

public class MainActivity extends ActionBarActivity
    implements NewBowlerDialog.NewBowlerDialogListener, ChangeableTheme
{

    /** Tag to identify class when outputting to console */
    private static final String TAG = "MainActivity";

    /** Indicates whether the option to create a quick series is enabled or not */
    public static boolean sQuickSeriesButtonEnabled = false;

    /** List to store Ids from bowler table in database */
    private List<Long> mListBowlerIds;
    /** List to store names of bowlers from bowler table, in an order relative to mListBowlerIds*/
    private List<String> mListBowlerNames;
    /** List to store averages of bowlers, in an order relative to mListBowlerIds */
    private List<Short> mListBowlerAverages;

    /** View to display bowler names and averages to user */
    private RecyclerView mBowlerRecycler;
    /** Adapter to manage data displayed in mBowlerRecycler */
    private BowlerAdapter mBowlerAdapter;
    /** TextView to display instructions to the user */
    private TextView mBowlerInstructionsTextView;

    /** Id from 'bowler' database which represents the most recently edited bowler */
    private long mRecentBowlerId = -1;
    /** Id from  'league' database which represents the most recently edited bowler */
    private long mRecentLeagueId = -1;
    /** Number of games in the most recently selected league */
    private byte mRecentNumberOfGames = -1;

    /** Id from 'bowler' database which represents the preferred bowler for a quick series */
    private long mQuickBowlerId = -1;
    /** Id from 'league' database which represents the preferred league for a quick series */
    private long mQuickLeagueId = -1;
    /** Number of games in the preferred league */
    private byte mQuickNumberOfGames = -1;

    /** Name of most recently edited bowler */
    private String mRecentBowlerName;
    /** Name of most recently edited league */
    private String mRecentLeagueName;
    /** Name of the preferred bowler for a quick series */
    private String mQuickBowlerName;
    /** Name of the preferred league for a quick series */
    private String mQuickLeagueName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Theme.loadTheme(this);

        mListBowlerIds = new ArrayList<>();
        mListBowlerNames = new ArrayList<>();
        mListBowlerAverages = new ArrayList<>();

        mBowlerRecycler = (RecyclerView) findViewById(R.id.recyclerView_bowlers);
        mBowlerRecycler.setHasFixedSize(true);
        mBowlerRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        RecyclerView.LayoutManager bowlerLayoutManager = new LinearLayoutManager(this);
        mBowlerRecycler.setLayoutManager(bowlerLayoutManager);

        mBowlerAdapter = new BowlerAdapter(this, mListBowlerIds, mListBowlerNames, mListBowlerAverages);
        mBowlerRecycler.setAdapter(mBowlerAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_bowler);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showNewBowlerDialog();
            }
        });

        mBowlerInstructionsTextView = (TextView)findViewById(R.id.textView_new_bowler_instructions);

        AppRater.appLaunched(this);

        updateTheme();
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

        mListBowlerIds.clear();
        mListBowlerNames.clear();
        mListBowlerAverages.clear();

        if (Theme.getMainActivityThemeInvalidated())
        {
            updateTheme();
        }

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.action_settings:
                showSettingsMenu();
                return true;
            case R.id.action_quick_series:
                //Action bar button pressed, quick series
                showQuickSeriesDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new settings activity and displays it to the user
     */
    private void showSettingsMenu()
    {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra(Constants.EXTRA_SETTINGS_SOURCE, TAG);
        startActivity(settingsIntent);
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (bowlerName == null || bowlerName.length() == 0)
        {
            //No input for the name
            validInput = false;
            invalidInputMessage = "You must enter a name.";
        }
        else if (mListBowlerNames.contains(bowlerName))
        {
            //Bowler name already exists in the list
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }

        /*
         * If the input was invalid for any reason, a dialog is shown
         * to the user and the method does not continue
         */
        if (!validInput)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setMessage(invalidInputMessage)
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
        DialogFragment dialogFragment = new NewBowlerDialog();
        dialogFragment.show(getFragmentManager(), "NewBowlerDialog");
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
            AlertDialog.Builder quickSeriesBuilder = new AlertDialog.Builder(this);
            if (mQuickBowlerId == -1 || mQuickLeagueId == -1)
            {
                quickSeriesBuilder.setMessage("Create a new series with these settings:"
                    + "\nBowler: " + mRecentBowlerName
                    + "\nLeague: " + mRecentLeagueName);
                quickOrRecent = false;
            }
            else
            {
                quickSeriesBuilder.setMessage("Create a new series with these settings:"
                        + "\nBowler: " + mQuickBowlerName
                        + "\nLeague: " + mQuickLeagueName);
                quickOrRecent = true;
            }

            quickSeriesBuilder.setTitle("Quick Series")
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (quickOrRecent)
                            {
                                SeriesActivity.addNewEventSeries(MainActivity.this, mQuickBowlerId, mQuickLeagueId, mQuickNumberOfGames, mQuickBowlerName, mQuickLeagueName);
                            }
                            else
                            {
                                SeriesActivity.addNewEventSeries(MainActivity.this, mRecentBowlerId, mRecentLeagueId, mRecentNumberOfGames, mQuickBowlerName, mQuickLeagueName);
                            }
                            dialog.dismiss();
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
            AlertDialog.Builder quickSeriesDisabledBuilder = new AlertDialog.Builder(this);
            quickSeriesDisabledBuilder.setTitle("Quick Series")
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
            long newId = -1;
            SQLiteDatabase database
                    = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();

            ContentValues bowlerValues = new ContentValues();
            bowlerValues.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName[0]);
            bowlerValues.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(currentDate));

            database.beginTransaction();
            try
            {
                newId = database.insert(BowlerEntry.TABLE_NAME, null, bowlerValues);


                /*
                 * Creates an entry in the 'league' table for a default league
                 * for the new bowler being added
                 */
                ContentValues leagueValues = new ContentValues();
                leagueValues.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, Constants.NAME_LEAGUE_OPEN);
                leagueValues.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(currentDate));
                leagueValues.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, newId);
                leagueValues.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, 1);
                database.insert(LeagueEntry.TABLE_NAME, null, leagueValues);

                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new bowler: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            return String.valueOf(newId) + ":" + bowlerName[0];
        }

        @Override
        protected void onPostExecute(String bowlerIdAndName)
        {
            long bowlerId = Long.parseLong(bowlerIdAndName.substring(0, bowlerIdAndName.indexOf(":")));
            String bowlerName = bowlerIdAndName.substring(bowlerIdAndName.indexOf(":") + 1);

            /*
             * Adds the new bowler information to the corresponding lists
             * and displays them in the recycler view
             */
            if (bowlerId != -1)
            {
                mListBowlerIds.add(0, bowlerId);
                mListBowlerNames.add(0, bowlerName);
                mListBowlerAverages.add(0, (short)0);
                mBowlerAdapter.notifyItemInserted(0);
                mBowlerRecycler.scrollToPosition(0);
                hideNewBowlerInstruction();
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
            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            //Gets name of all bowlers from database and their IDs
            String rawBowlerQuery = "SELECT "
                    + "bowler." + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                    + "bowler." + BowlerEntry._ID + ", "
                    + "AVG(game." + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ") AS avg"
                    + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                    + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                    + " ON bowler." + BowlerEntry._ID + "=game." + GameEntry.COLUMN_NAME_BOWLER_ID
                    + " GROUP BY bowler." + BowlerEntry._ID;

            Cursor cursor = database.rawQuery(rawBowlerQuery, new String[]{});

            /*
             * Clears data from lists and adds Ids, names
             * and averages loaded in the cursor
             */
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    mListBowlerIds.add(cursor.getLong(cursor.getColumnIndex(BowlerEntry._ID)));
                    mListBowlerNames.add(cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME)));
                    mListBowlerAverages.add(cursor.getShort(cursor.getColumnIndex("avg")));
                    cursor.moveToNext();
                }
            }

            /*
             * If there is a bowler and league which have been previously accessed
             * or a set bowler or league, then their corresponding values
             * are loaded to be used to create a quick series
             */
            if (mRecentBowlerId > -1 && mRecentLeagueId > -1)
            {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{String.valueOf(mRecentBowlerId), String.valueOf(mRecentLeagueId)};

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                if (cursor.moveToFirst())
                {
                    mRecentBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME));
                    mRecentLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    mRecentNumberOfGames = (byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                }
            }
            if (mQuickBowlerId > -1 && mQuickLeagueId > -1)
            {
                String rawQuickQuery = "SELECT "
                        + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] rawQuickArgs = new String[]{String.valueOf(mQuickBowlerId), String.valueOf(mQuickLeagueId)};

                cursor = database.rawQuery(rawQuickQuery, rawQuickArgs);
                if (cursor.moveToFirst())
                {
                    mQuickBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME));
                    mQuickLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    mQuickNumberOfGames = (byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mBowlerAdapter.notifyDataSetChanged();
            sQuickSeriesButtonEnabled = !(mRecentBowlerId == -1 || mRecentLeagueId == -1) || !(mQuickBowlerId == -1 || mQuickLeagueId == -1);
            if (mListBowlerIds.size() > 0)
            {
                hideNewBowlerInstruction();
            }
        }
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_bowler);
        floatingActionButton.setColorNormal(Theme.getActionButtonThemeColor());
        floatingActionButton.setColorPressed(Theme.getActionButtonThemeColor());
        floatingActionButton.setColorRipple(Theme.getActionButtonRippleThemeColor());
        Theme.validateMainActivityTheme();
    }

    public void showNewBowlerInstructions()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mBowlerInstructionsTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideNewBowlerInstruction()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mBowlerInstructionsTextView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
