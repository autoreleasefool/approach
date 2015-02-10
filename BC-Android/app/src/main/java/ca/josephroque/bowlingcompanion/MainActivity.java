package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.AddBowlerDialog;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class MainActivity extends ActionBarActivity
    implements AddBowlerDialog.AddBowlerDialogListener
{

    /** TAG identifier for output to log */
    private static final String TAG = "MainActivity";

    /** List of names of bowlers' stats being tracked */
    private List<String> bowlerNamesList = null;
    /** List of IDs of bowlers' stats being tracked*/
    private List<Long> bowlerIDsList = null;
    /** Adapter for ListView of bowlers */
    private ArrayAdapter<String> bowlerAdapter = null;

    /** ID of the most recently selected bowler */
    private long recentBowlerID = -1;
    /** ID of the most recently selected league */
    private long recentLeagueID = -1;
    /** Number of games in most recently selected league */
    private int recentNumberOfGames = -1;

    /** Layout which shows the tutorial first time */
    private RelativeLayout topLevelLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listBowlerNames = (ListView) findViewById(R.id.list_bowler_name);

        bowlerNamesList = new ArrayList<String>();
        bowlerIDsList = new ArrayList<Long>();
        bowlerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, bowlerNamesList);
        listBowlerNames.setAdapter(bowlerAdapter);
        listBowlerNames.setLongClickable(true);

        listBowlerNames.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    new OpenBowlerLeaguesTask().execute(position);
                }
            });
        listBowlerNames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    showDeleteBowlerDialog(position);
                    return true;
                }
            });

        findViewById(R.id.button_quick_game).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                        .edit()
                        .putInt(Constants.PREFERENCES_NUMBER_OF_GAMES, recentNumberOfGames)
                        .apply();
                SeriesActivity.addNewSeries(MainActivity.this, recentBowlerID, recentLeagueID, recentNumberOfGames);
            }
        });

        topLevelLayout = (RelativeLayout)findViewById(R.id.main_top_layout);
        if (hasShownTutorial())
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        recentBowlerID = preferences.getLong(Constants.PREFERENCES_ID_BOWLER_RECENT, -1);
        recentLeagueID = preferences.getLong(Constants.PREFERENCES_ID_LEAGUE_RECENT, -1);

        //Clearing all preferences so app does not store unnecessary data
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .remove(Constants.PREFERENCES_NAME_BOWLER)
                .remove(Constants.PREFERENCES_NAME_LEAGUE)
                .remove(Constants.PREFERENCES_ID_BOWLER)
                .remove(Constants.PREFERENCES_ID_LEAGUE)
                .remove(Constants.PREFERENCES_ID_SERIES)
                .remove(Constants.PREFERENCES_ID_GAME)
                .remove(Constants.PREFERENCES_NUMBER_OF_GAMES)
                .remove(Constants.PREFERENCES_TOURNAMENT_MODE)
                .remove(Constants.PREFERENCES_GAME_NUMBER)
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
        if (topLevelLayout.getVisibility() == View.VISIBLE)
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_add_bowler:
                showAddBowlerDialog();
                return true;
            case R.id.action_settings:
                showSettings();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        DatabaseHelper.closeInstance();

        /*
        TODO: commented out code, tutorial test
        Used to test tutorial, so the tutorial is displayed as often
        as possible. When tutorial testing is complete, remove

        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_MAIN, false)
                .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_LEAGUE, false)
                .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_SERIES, false)
                .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_GAME, false)
                .commit();*/
    }

    /**
     * Creates and displays an instance of SettingsActivity
     */
    private void showSettings()
    {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    /**
     * Displays an AddBowlerBialogFragment to get the name of a new
     * bowler to track
     */
    private void showAddBowlerDialog()
    {
        DialogFragment dialog = new AddBowlerDialog();
        dialog.show(getSupportFragmentManager(), "AddBowlerDialogFragment");
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
        else if (bowlerNamesList.contains(bowlerName))
        {
            //Bowler name already exists in the list
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }

        if (!validInput)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton(Constants.BUTTON_POSITIVE, new DialogInterface.OnClickListener()
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
         * Creates a new database entry for the bowler whose name was
         * received by input via the dialog
         */
        new AddBowlerTask().execute(bowlerName);
    }

    @Override
    public void onCancelNewBowler()
    {
        //does nothing
    }

    /**
     * Shows a dialog to delete data relevant to a bowler
     *
     * @param position position of selected item to delete
     */
    private void showDeleteBowlerDialog(final int position)
    {
        final String bowlerName = bowlerNamesList.get(position);
        final long bowlerID = bowlerIDsList.get(position);

        DatabaseHelper.deleteData(this,
                new DatabaseHelper.DataDeleter()
                    {
                        @Override
                        public void execute()
                        {
                            deleteBowler(bowlerID);
                        }
                    },
                false,
                bowlerName);
    }

    /**
     * Deletes all data in database corresponding to a single bowler ID
     *
     * @param selectedBowlerID bowler ID to delete data of
     */
    private void deleteBowler(final long selectedBowlerID)
    {
        final int index = bowlerIDsList.indexOf(selectedBowlerID);
        final String bowlerName = bowlerNamesList.remove(index);
        bowlerIDsList.remove(index);
        bowlerAdapter.notifyDataSetChanged();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
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
     * Displays a tutorial overlay if one hasn't been shown to
     * the user yet
     *
     * @return true if the tutorial has already been shown, false otherwise
     */
    private boolean hasShownTutorial()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        boolean hasShownTutorial = preferences.getBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_MAIN, false);

        if (!hasShownTutorial)
        {
            preferences.edit()
                    .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_MAIN, true)
                    .apply();
            topLevelLayout.setVisibility(View.VISIBLE);
            topLevelLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    topLevelLayout.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
        }
        return hasShownTutorial;
    }

    private class OpenBowlerLeaguesTask extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... position)
        {
            final ListView listBowlerNames = (ListView)findViewById(R.id.list_bowler_name);
            String bowlerNameSelected = (String)listBowlerNames.getItemAtPosition(position[0]);

            long selectedBowlerID;
            selectedBowlerID = bowlerIDsList.get(bowlerNamesList.indexOf(bowlerNameSelected));

                    /*
                     * Updates database to make the selected bowler the most recently
                     * edited, and therefore the top of the list next time it is
                     * loaded
                     */
            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
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

            getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                    .edit()
                    .putString(Constants.PREFERENCES_NAME_BOWLER, bowlerNamesList.get(bowlerIDsList.indexOf(selectedBowlerID)))
                    .putLong(Constants.PREFERENCES_ID_BOWLER, selectedBowlerID)
                    .apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Intent leagueIntent = new Intent(MainActivity.this, LeagueActivity.class);
            startActivity(leagueIntent);
        }
    }

    private class LoadBowlerAndRecentTask extends AsyncTask<Void, Void, String[]>
    {
        @Override
        protected String[] doInBackground(Void... parameters)
        {
            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            //Gets name of all bowlers from database and their IDs
            Cursor cursor = database.query(BowlerEntry.TABLE_NAME,
                    new String[]{BowlerEntry.COLUMN_NAME_BOWLER_NAME, BowlerEntry._ID},
                    null,   //All rows
                    null,   //No args
                    null,   //No group
                    null,   //No having
                    BowlerEntry.COLUMN_NAME_DATE_MODIFIED + " DESC");  //No order

            //Adds bowler names and IDs to list
            bowlerNamesList.clear();
            bowlerIDsList.clear();
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    bowlerNamesList.add(cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME)));
                    bowlerIDsList.add(cursor.getLong(cursor.getColumnIndex(BowlerEntry._ID)));
                    cursor.moveToNext();
                }
            }


            if (recentBowlerID > -1 && recentLeagueID > -1)
            {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_NAME_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_NAME_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{String.valueOf(recentBowlerID), String.valueOf(recentLeagueID)};

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                cursor.moveToFirst();
                String recentBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_NAME_BOWLER_NAME));
                String recentLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                recentNumberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                return new String[]{recentBowlerName, recentLeagueName};
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] recentBowlerAndLeague)
        {
            bowlerAdapter.notifyDataSetChanged();
            Button quickGameButton = (Button)findViewById(R.id.button_quick_game);

            if (recentBowlerID > -1 && recentLeagueID > -1)
            {
                quickGameButton.setText("Start a " + recentNumberOfGames + " game series with these settings:\n"
                        + "Bowler: " + recentBowlerAndLeague[0] + "\n"
                        + "League: " + recentBowlerAndLeague[1]);
            }
            else
            {
                quickGameButton.setText(R.string.text_quick_game_button);
                quickGameButton.setEnabled(false);
            }
        }
    }

    private class AddBowlerTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... bowlerName)
        {
            long newID = -1;
            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            ContentValues bowlerValues = new ContentValues();
            bowlerValues.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName[0]);
            bowlerValues.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));

            database.beginTransaction();
            try
            {
                newID = database.insert(BowlerEntry.TABLE_NAME, null, bowlerValues);

                ContentValues leagueValues = new ContentValues();
                leagueValues.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, "Open");
                leagueValues.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));
                leagueValues.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, newID);
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

            return String.valueOf(newID) + ":" + bowlerName[0];
        }

        @Override
        protected void onPostExecute(String bowlerNameAndID)
        {
            String bowlerName = bowlerNameAndID.substring(bowlerNameAndID.indexOf(":") + 1);
            long newID = Long.parseLong(bowlerNameAndID.substring(0, bowlerNameAndID.indexOf(":")));

            bowlerNamesList.add(0, bowlerName);
            bowlerIDsList.add(0, newID);
            bowlerAdapter.notifyDataSetChanged();
        }
    }
}
