package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

        listBowlerNames.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String bowlerNameSelected = (String)listBowlerNames.getItemAtPosition(position);

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

                    Intent leagueIntent = new Intent(MainActivity.this, LeagueActivity.class);
                    startActivity(leagueIntent);
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
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        recentBowlerID = preferences.getLong(Constants.PREFERENCES_ID_BOWLER_RECENT, -1);
        recentLeagueID = preferences.getLong(Constants.PREFERENCES_ID_LEAGUE_RECENT, -1);
        boolean hasShownTutorial = preferences.getBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL, false);

        //Clearing all preferences so app does not store unnecessary data
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .clear()
                .putLong(Constants.PREFERENCES_ID_BOWLER_RECENT, recentBowlerID)
                .putLong(Constants.PREFERENCES_ID_LEAGUE_RECENT, recentLeagueID)
                .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL, hasShownTutorial)
                .apply();

        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
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
        bowlerAdapter.notifyDataSetChanged();

        Button quickGameButton = (Button)findViewById(R.id.button_quick_game);
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
            quickGameButton.setText("Start a " + recentNumberOfGames + " game series with these settings:\n"
                    + "Bowler: " + recentBowlerName + "\n"
                    + "League: " + recentLeagueName);
        }
        else
        {
            quickGameButton.setText(R.string.text_quick_game_button);
            quickGameButton.setEnabled(false);
        }
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
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        /*
         * Creates a new database entry for the bowler whose name was
         * received by input via the dialog
         */
        long newID = -1;
        SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues bowlerValues = new ContentValues();
        bowlerValues.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName);
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

        bowlerNamesList.add(0, bowlerName);
        bowlerIDsList.add(0, newID);
        bowlerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelNewBowler()
    {
        //does nothing
    }
}
