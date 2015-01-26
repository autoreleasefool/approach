package ca.josephroque.bowlingcompanion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class StatsActivity extends ActionBarActivity
{

    /** TAG identifier for output to log */
    private static final String TAG = "StatsActivity";

    /** Stats relating to hitting the middle pin */
    private static final String[] STATS_MIDDLE_GENERAL =
            {"Middle Hit", "Strikes", "Spares"};
    /** Detailed stats relating to hitting the middle pin */
    private static final String[] STATS_MIDDLE_DETAILED =
            {"Head Pins", "Head Pins Spared", "Lefts", "Lefts Spared", "Rights", "Rights Spared", "Aces", "Aces Spared", "Chop Offs", "Chop Offs Spared", "Splits", "Splits Spared"};
    /** Stats about fouls */
    private static final String[] STATS_FOULS =
            {"Fouls"};
    /** Stats about the total pins left standing */
    private static final String[] STATS_PINS_TOTAL =
            {"Total Pins Left on Deck"};
    /** Stats about the average pins left standing */
    private static final String[] STATS_PINS_AVERAGE =
            {"Average Pins Left on Deck"};
    /** General stats about the bowler or league */
    private static final String[] STATS_GENERAL =
            {"Average", "High Single", "High Series", "Total Pinfall", "# of Games"};

    /** Name of the currently selected bowler */
    private String bowlerName = null;
    /** Name of the currently selected league */
    private String leagueName = null;
    /** ID of the currently selected bowler */
    private long bowlerID = -1;
    /** ID of the currently selected league */
    private long leagueID = -1;
    /** ID of the currently selected series */
    private long seriesID = -1;
    /** ID of the currently selected game */
    private long gameID = -1;
    /** Game number in the current series */
    private int gameNumber = -1;
    /** The number of games in a series, if viewing stats for a series */
    private int numberOfGamesInSeries = -1;

    /** List containing all of the stats to be displayed */
    private ListView listStats = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        bowlerName = preferences.getString(Constants.PREFERENCES_NAME_BOWLER, "");
        leagueName = preferences.getString(Constants.PREFERENCES_NAME_LEAGUE, "");
        bowlerID = preferences.getLong(Constants.PREFERENCES_ID_BOWLER, -1);
        leagueID = preferences.getLong(Constants.PREFERENCES_ID_LEAGUE, -1);
        seriesID = preferences.getLong(Constants.PREFERENCES_ID_SERIES, -1);
        gameID = preferences.getLong(Constants.PREFERENCES_ID_GAME, -1);
        gameNumber = preferences.getInt(Constants.PREFERENCES_GAME_NUMBER, -1);
        numberOfGamesInSeries = getIntent().getIntExtra(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, -1);

        listStats = (ListView)findViewById(R.id.list_stats);

        if (gameID == -1)
        {
            if (leagueID == -1)
            {
                setTitle(R.string.title_activity_stats_bowler);
                loadBowlerStats();
            }
            else
            {
                setTitle(R.string.title_activity_stats_league);
                loadLeagueStats();
            }
        }
        else
        {
            setTitle(R.string.title_activity_stats_game);
            loadGameStats();
        }
    }

    /**
     * Loads the stats relevant to the currently selected bowler
     */
    private void loadBowlerStats()
    {
        
    }

    /**
     * Loads the stats relevant to the currently selected league
     */
    private void loadLeagueStats()
    {

    }

    /**
     * Loads the stats relevant to the currently selected game
     */
    private void loadGameStats()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Returns a query from the database with data necessary to determine statistics
     * of a bowler
     *
     * @return a cursor with the result of the query
     */
    private Cursor getBowlerCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " LEFT JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + " WHERE game." + GameEntry.COLUMN_NAME_BOWLER_ID + "=?"
                + " ORDER BY game." + GameEntry._ID + ", frame." + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(bowlerID)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a query from the database with data necessary to determine statistics
     * of a league
     *
     * @return a cursor with the result of the query
     */
    private Cursor getLeagueCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " LEFT JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + " WHERE game." + GameEntry.COLUMN_NAME_LEAGUE_ID + "=?"
                + " ORDER BY game." + GameEntry._ID + ", frame." + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(leagueID)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a query from the database with data necessary to determine statistics
     * of a game
     *
     * @return a cursor with the result of the query
     */
    private Cursor getGameCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        String rawStatsQuery = "SELECT "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + FrameEntry.TABLE_NAME
                + " WHERE " + FrameEntry.COLUMN_NAME_GAME_ID + "=?"
                + " ORDER BY " + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(gameID)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }
}
