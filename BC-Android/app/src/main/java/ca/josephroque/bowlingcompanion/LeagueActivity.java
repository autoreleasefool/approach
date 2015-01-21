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
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.adapter.LeagueAverageListAdapter;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.AddLeagueDialog;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class LeagueActivity extends ActionBarActivity
    implements AddLeagueDialog.AddLeagueDialogListener
{

    /** TAG identifier for output to log */
    private static final String TAG = "LeagueActivity";

    /** Adapter for the ListView of leagues */
    private LeagueAverageListAdapter leagueAdapter = null;

    /** ID of the selected bowler */
    private long bowlerID = -1;
    /** List of the names of the leagues belonging to the selected bowler */
    private List<String> leagueNamesList = null;
    /** List of the averages of the leagues, relative to order of leagueNamesList */
    private List<Integer> leagueAverageList = null;
    /** List of the number of games in the leagues, relative to order of leagueNamesList */
    private List<Integer> leagueNumberOfGamesList = null;
    /** List of the IDs of the leagues, relative to the order of leagueNamesList */
    private List<Long> leagueIDList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);

        SQLiteDatabase database = DatabaseHelper.getInstance(LeagueActivity.this).getReadableDatabase();
        final ListView leagueListView = (ListView)findViewById(R.id.list_league_name);

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        bowlerID = preferences.getLong(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);

        String rawLeagueQuery = "SELECT "
                + LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID + " AS lid, "
                + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE
                + " FROM " + LeagueEntry.TABLE_NAME
                + " LEFT JOIN " + GameEntry.TABLE_NAME
                + " ON " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=" + GameEntry.COLUMN_NAME_BOWLER_ID
                + " WHERE " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=?"
                + " ORDER BY " + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " DESC";
        String[] rawLeagueArgs ={String.valueOf(bowlerID)};

        Cursor cursor = database.rawQuery(rawLeagueQuery, rawLeagueArgs);

        //Loads data from the above query into lists
        leagueNamesList = new ArrayList<String>();
        leagueAverageList = new ArrayList<Integer>();
        leagueIDList = new ArrayList<Long>();
        leagueNumberOfGamesList = new ArrayList<Integer>();
        List<Integer> leagueTotalNumberOfGamesList = new ArrayList<Integer>();

        if (cursor.moveToFirst())
        {
            int leagueTotalPinfall = 0;
            int totalNumberOfLeagueGames = 0;
            while(!cursor.isAfterLast())
            {
                String leagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                long leagueID = cursor.getLong(cursor.getColumnIndex("lid"));
                int numberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                int finalScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                if (leagueIDList.size() == 0)
                {
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                    leagueNumberOfGamesList.add(numberOfGames);
                }
                else if (!leagueIDList.contains(leagueID))
                {
                    if (leagueIDList.size() > 0)
                    {
                        leagueTotalNumberOfGamesList.add(totalNumberOfLeagueGames);
                        leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
                    }

                    leagueTotalPinfall = 0;
                    totalNumberOfLeagueGames = 0;
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                    leagueNumberOfGamesList.add(numberOfGames);
                }

                totalNumberOfLeagueGames++;
                leagueTotalPinfall += finalScore;

                cursor.moveToNext();
            }

            if (leagueIDList.size() > 0)
            {
                leagueTotalNumberOfGamesList.add(totalNumberOfLeagueGames);
                leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
            }
        }

        leagueAdapter = new LeagueAverageListAdapter(LeagueActivity.this, leagueIDList, leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueListView.setAdapter(leagueAdapter);
        leagueListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    long leagueIDSelected = (Long)leagueListView.getItemAtPosition(position);

                    //Updates the date modified in the database of the selected league
                    SQLiteDatabase database = DatabaseHelper.getInstance(LeagueActivity.this).getWritableDatabase();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ContentValues values = new ContentValues();
                    values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));

                    database.beginTransaction();
                    try
                    {
                        database.update(LeagueEntry.TABLE_NAME,
                                values,
                                LeagueEntry._ID + "=?",
                                new String[]{String.valueOf(leagueIDSelected)});
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

                    getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                            .edit()
                            .putString(Constants.PREFERENCES_NAME_LEAGUE, leagueNamesList.get(leagueIDList.indexOf(leagueIDSelected)))
                            .putLong(Constants.PREFERENCES_ID_LEAGUE, leagueIDSelected)
                            .putInt(Constants.PREFERENCES_NUMBER_OF_GAMES, leagueNumberOfGamesList.get(leagueIDList.indexOf(leagueIDSelected)))
                            .apply();

                    Intent seriesIntent = new Intent(LeagueActivity.this, SeriesActivity.class);
                    startActivity(seriesIntent);
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_bowler_stats:
                showBowlerStats();
                return true;
            case R.id.action_new_league:
                showAddLeagueDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a StatsActivity to show the complete stats
     * of the selected bowler
     */
    private void showBowlerStats()
    {
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_LEAGUE, -1)
                .putLong(Constants.PREFERENCES_ID_GAME, -1)
                .putLong(Constants.PREFERENCES_ID_SERIES, -1);

        Intent statsIntent = new Intent(LeagueActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

    /**
     * Creates an instance of AddLeagueDialogFragment to create a new league
     * for the selected bowler
     */
    private void showAddLeagueDialog()
    {
        DialogFragment dialog = new AddLeagueDialog();
        dialog.show(getSupportFragmentManager(), "AddLeagueDialogFragment");
    }

    @Override
    public void onAddNewLeague(String leagueName, int numberOfGames)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (numberOfGames < 1 || numberOfGames > 5)
        {
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and 5 (inclusive).";
        }
        else if (leagueNamesList.contains(leagueName))
        {
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }

        //Displays an alert if input is invalid and does not create the new league
        if (!validInput)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(LeagueActivity.this);
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

        long newID = -1;
        SQLiteDatabase database = DatabaseHelper.getInstance(LeagueActivity.this).getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, leagueName);
        values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));
        values.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
        values.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames);

        database.beginTransaction();
        try
        {
            newID = database.insert(LeagueEntry.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Error adding new league: " + ex.getMessage());
        }
        finally
        {
           database.endTransaction();
        }

        //Adds the league to the top of the list (it is the most recent)
        leagueNamesList.add(0, leagueName);
        leagueAverageList.add(0, 0);
        leagueIDList.add(0, newID);
        leagueNumberOfGamesList.add(0, numberOfGames);
        leagueAdapter.update(leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelNewLeague()
    {
        //do nothing
    }
}
