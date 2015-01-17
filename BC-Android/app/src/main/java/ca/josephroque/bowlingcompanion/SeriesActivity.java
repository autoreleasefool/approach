package ca.josephroque.bowlingcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import ca.josephroque.bowlingcompanion.adapter.SeriesListAdapter;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

public class SeriesActivity extends ActionBarActivity
{

    private String bowlerName = null;
    private String leagueName = null;
    private long bowlerID = -1;
    private long leagueID = -1;
    private int numberOfGames = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        final ListView seriesListView = (ListView)findViewById(R.id.list_series);

        SharedPreferences preferences = getSharedPreferences(Preferences.MY_PREFS, MODE_PRIVATE);
        bowlerName = preferences.getString(Preferences.NAME_BOWLER, "");
        leagueName = preferences.getString(Preferences.NAME_LEAGUE, "");
        bowlerID = preferences.getLong(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);
        leagueID = preferences.getLong(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, -1);
        numberOfGames = preferences.getInt(LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, -1);

        String rawSeriesQuery = "SELECT "
                + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid, "
                + SeriesEntry.COLUMN_NAME_DATE_CREATED + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER
                + " FROM " + SeriesEntry.TABLE_NAME
                + " LEFT JOIN " + GameEntry.TABLE_NAME
                + " ON sid=" /*+ SeriesEntry._ID*/ + GameEntry.COLUMN_NAME_SERIES_ID
                + " WHERE " + SeriesEntry.COLUMN_NAME_LEAGUE_ID + "=?"
                + " ORDER BY " + SeriesEntry.COLUMN_NAME_DATE_CREATED + " DESC, "
                + GameEntry.COLUMN_NAME_GAME_NUMBER;
        String[] rawQueryArgs = {String.valueOf(leagueID)};

        Cursor cursor = database.rawQuery(rawSeriesQuery, rawQueryArgs);

        List<Long> seriesIDList = new ArrayList<Long>();
        List<String> seriesDateList = new ArrayList<String>();
        List<List<Integer>> seriesGamesList = new ArrayList<List<Integer>>();

        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                long seriesID = cursor.getLong(cursor.getColumnIndex("sid"));
                String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_NAME_DATE_CREATED)).substring(0,10);
                int finalGameScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                if (!seriesIDList.contains(seriesID))
                {
                    seriesIDList.add(seriesID);
                    seriesDateList.add(seriesDate);
                    seriesGamesList.add(new ArrayList<Integer>());
                }

                seriesGamesList.get(seriesGamesList.size() - 1).add(finalGameScore);
                cursor.moveToNext();
            }
        }

        SeriesListAdapter seriesAdapter = new SeriesListAdapter(SeriesActivity.this, seriesIDList, seriesDateList, seriesGamesList);
        seriesListView.setAdapter(seriesAdapter);
        seriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    long seriesIDSelected = (Long)seriesListView.getItemAtPosition(position);

                    Preferences.setPreferences(SeriesActivity.this, bowlerName, leagueName, bowlerID, leagueID, seriesIDSelected, -1, numberOfGames);

                    long[] gameID = new long[numberOfGames];
                    long[] frameID = new long[30];
                    SQLiteDatabase database = DatabaseHelper.getInstance(SeriesActivity.this).getReadableDatabase();

                    String rawSeriesQuery = "SELECT "
                            + GameEntry.TABLE_NAME + "." + GameEntry._ID + " AS gid, "
                            + FrameEntry.TABLE_NAME + "." + FrameEntry._ID + " AS fid"
                            + " FROM " + GameEntry.TABLE_NAME
                            + " LEFT JOIN " + FrameEntry.TABLE_NAME
                            + " ON gid=" + FrameEntry.COLUMN_NAME_GAME_ID
                            + " WHERE " + GameEntry.COLUMN_NAME_SERIES_ID + "=?"
                            + " ORDER BY gid, fid";
                    String[] rawSeriesArgs = {String.valueOf(seriesIDSelected)};

                    int currentGame = -1;
                    long currentGameID = -1;
                    int currentFrame = 0;
                    Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
                    if (cursor.moveToFirst())
                    {
                        while (!cursor.isAfterLast())
                        {
                            if (cursor.getLong(cursor.getColumnIndex("gid")) == currentGameID)
                            {
                                frameID[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                            }
                            else
                            {
                                currentGameID = cursor.getLong(cursor.getColumnIndex("gid"));
                                frameID[currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                                gameID[++currentGame] = currentGameID;
                            }
                            cursor.moveToNext();
                        }
                    }

                    Intent gameIntent = new Intent(SeriesActivity.this, GameActivity.class);
                    gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, gameID);
                    gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, frameID);
                    startActivity(gameIntent);
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_series, menu);
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
            case R.id.action_league_stats:
                showLeagueStats();
                return true;
            case R.id.action_add_series:
                addNewSeries();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLeagueStats()
    {
        Preferences.setPreferences(this, bowlerName, leagueName, bowlerID, leagueID, -1, -1, numberOfGames);

        Intent statsIntent = new Intent(SeriesActivity.this, StatsActivity.class);
        startActivity(statsIntent);
        //TODO: showLeagueStats()
    }

    private void addNewSeries()
    {
        long seriesID = -1;
        long[] gameID = new long[numberOfGames], frameID = new long[30];
        SQLiteDatabase database = DatabaseHelper.getInstance(SeriesActivity.this).getWritableDatabase();
        Intent gameIntent = new Intent(SeriesActivity.this, GameActivity.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        database.beginTransaction();

        try
        {
            ContentValues values = new ContentValues();
            values.put(SeriesEntry.COLUMN_NAME_DATE_CREATED, dateFormat.format(date));
            values.put(SeriesEntry.COLUMN_NAME_LEAGUE_ID, leagueID);
            values.put(SeriesEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
            seriesID = database.insert(SeriesEntry.TABLE_NAME, null, values);

            for (int i = 0; i < numberOfGames; i++)
            {
                values = new ContentValues();
                values.put(GameEntry.COLUMN_NAME_GAME_NUMBER, i + 1);
                values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, 0);
                values.put(GameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                values.put(GameEntry.COLUMN_NAME_SERIES_ID, seriesID);
                gameID[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                for (int j = 0; j < 10; j++)
                {
                    values = new ContentValues();
                    values.put(FrameEntry.COLUMN_NAME_FRAME_NUMBER, j + 1);
                    values.put(FrameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                    values.put(FrameEntry.COLUMN_NAME_LEAGUE_ID, leagueID);
                    values.put(FrameEntry.COLUMN_NAME_SERIES_ID, seriesID);
                    values.put(FrameEntry.COLUMN_NAME_GAME_ID, gameID[i]);
                    frameID[j + 10 * i] = database.insert(FrameEntry.TABLE_NAME, null, values);
                }
            }
            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w("SeriesActivity", "Error adding new series: " + ex.getMessage());
        }
        finally
        {
            database.endTransaction();
        }

        Preferences.setPreferences(this, bowlerName, leagueName, bowlerID, leagueID, seriesID, -1, numberOfGames);
        gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, gameID);
        gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, frameID);
        startActivity(gameIntent);
    }
}
