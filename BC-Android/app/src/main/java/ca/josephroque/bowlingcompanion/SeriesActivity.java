package ca.josephroque.bowlingcompanion;

import android.content.ContentValues;
import android.content.Intent;
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

    private long bowlerID = -1;
    private long leagueID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        final ListView seriesListView = (ListView)findViewById(R.id.list_series);

        bowlerID = getIntent().getLongExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);
        leagueID = getIntent().getLongExtra(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, -1);

        /*TODO: delete following if statement, just to check if program works to this point*/
        if (bowlerID == -1 || leagueID == -1)
        {
            Log.w("SeriesActivity", "ERROR: could not find bowlerID(" + bowlerID + ") or leagueID(" + leagueID + ") in extras");
        }

        String rawSeriesQuery = "SELECT "
                + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid, "
                + SeriesEntry.COLUMN_NAME_DATE_CREATED + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER
                + " FROM " + SeriesEntry.TABLE_NAME
                + " LEFT JOIN " + GameEntry.TABLE_NAME
                + " ON sid" /*+ SeriesEntry._ID*/ + "=" + GameEntry.COLUMN_NAME_SERIES_ID
                + " WHERE " + SeriesEntry.COLUMN_NAME_BOWLER_ID + "=?"
                + " ORDER BY " + SeriesEntry.COLUMN_NAME_DATE_CREATED + " DESC, "
                + GameEntry.COLUMN_NAME_GAME_NUMBER;
        String[] rawQueryArgs = {String.valueOf(bowlerID)};

        Cursor cursor = database.rawQuery(rawSeriesQuery, rawQueryArgs);

        List<Long> seriesIDList = new ArrayList<Long>();
        List<String> seriesDateList = new ArrayList<String>();
        List<List<Integer>> seriesGamesList = new ArrayList<List<Integer>>();

        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                long seriesID = cursor.getLong(cursor.getColumnIndex(SeriesEntry._ID));
                String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_NAME_DATE_CREATED));
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

                    Intent gameIntent = new Intent(SeriesActivity.this, GameActivity.class);
                    gameIntent.putExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, bowlerID);
                    gameIntent.putExtra(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, leagueID);
                    gameIntent.putExtra(SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID, seriesIDSelected);
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLeagueStats()
    {
        //TODO: showLeagueStats()
    }

    private void addNewSeries()
    {
        long seriesID = -1;
        long[] gameID = new long[3], frameID = new long[30];
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

            for (int i = 0; i < 3; i++)
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

        gameIntent.putExtra(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, bowlerID);
        gameIntent.putExtra(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, leagueID);
        gameIntent.putExtra(SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID, seriesID);
        gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, gameID);
        gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, frameID);
        startActivity(gameIntent);
    }
}
