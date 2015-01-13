package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import ca.josephroque.bowlingcompanion.dialog.AddBowlerDialog;
import ca.josephroque.bowlingcompanion.dialog.AddLeagueDialog;


public class LeagueActivity extends ActionBarActivity
    implements AddLeagueDialog.AddLeagueDialogListener
{

    private LeagueAverageListAdapter leagueAdapter = null;

    private long bowlerID = -1;
    private List<String> leagueNamesList = null;
    private List<Integer> leagueAverageList = null;
    private List<Long> leagueIDList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);

        SQLiteDatabase database = DatabaseHelper.getInstance(LeagueActivity.this).getReadableDatabase();
        final ListView leagueListView = (ListView)findViewById(R.id.list_league_name);

        bowlerID = getIntent().getLongExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);

        //TODO: get rid of this in final, just to check for error for now
        if (bowlerID == -1)
        {
            Log.w("LeagueActivity", "ERROR: Could not find bowler ID in extras");
        }

        String rawLeagueQuery = "SELECT "
                + LeagueEntry._ID + ", "
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

        leagueNamesList = new ArrayList<String>();
        leagueAverageList = new ArrayList<Integer>();
        leagueIDList = new ArrayList<Long>();
        List<Integer> leagueNumberOfGamesList = new ArrayList<Integer>();

        if (cursor.moveToFirst())
        {
            int leagueTotalPinfall = 0;
            int totalNumberOfLeagueGames = 0;
            while(!cursor.isAfterLast())
            {
                String leagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                long leagueID = cursor.getLong(cursor.getColumnIndex(LeagueEntry._ID));
                int numberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                int finalScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                if (leagueIDList.size() == 0)
                {
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                }
                else if (!leagueIDList.contains(leagueID))
                {
                    if (leagueIDList.size() > 0)
                    {
                        leagueNumberOfGamesList.add(totalNumberOfLeagueGames);
                        leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
                    }

                    leagueTotalPinfall = 0;
                    totalNumberOfLeagueGames = 0;
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                }

                totalNumberOfLeagueGames += numberOfGames;
                leagueTotalPinfall += finalScore;

                cursor.moveToNext();
            }

            if (leagueIDList.size() > 0)
            {
                leagueNumberOfGamesList.add(totalNumberOfLeagueGames);
                leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
            }
        }

        //TODO finish this thing http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
        leagueAdapter = new LeagueAverageListAdapter(LeagueActivity.this, leagueIDList, leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueListView.setAdapter(leagueAdapter);
        leagueListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    long leagueIDSelected = (Long)leagueListView.getItemAtPosition(position);

                    SQLiteDatabase database = DatabaseHelper.getInstance(LeagueActivity.this).getWritableDatabase();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();

                    ContentValues values = new ContentValues();
                    values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));

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
                        Log.w("LeagueActivity", "Error updating league: " + ex.getMessage());
                    }
                    finally
                    {
                        database.endTransaction();
                    }

                    Intent seriesIntent = new Intent(LeagueActivity.this, SeriesActivity.class);
                    seriesIntent.putExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, bowlerID);
                    seriesIntent.putExtra(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, leagueIDSelected);
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBowlerStats()
    {
        //TODO: showBowlerStats()
    }

    private void showAddLeagueDialog()
    {
        DialogFragment dialog = new AddBowlerDialog();
        dialog.show(getSupportFragmentManager(), "AddLeagueDialogFragment");
    }

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
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, leagueName);
        values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));
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
            Log.w("LeagueActivity", "Error adding new league: " + ex.getMessage());
        }
        finally
        {
           database.endTransaction();
        }

        leagueNamesList.add(0, leagueName);
        leagueAverageList.add(0, 0);
        leagueIDList.add(0, newID);
        leagueAdapter.notifyDataSetChanged();
    }

    public void onCancelNewLeague()
    {
        //do nothing
    }
}
