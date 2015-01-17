package ca.josephroque.bowlingcompanion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;


public class StatsActivity extends ActionBarActivity
{

    private static final String[] STATS_UNIVERSAL_NAMES = {"Middle Hit %", "Strike %", "Spare %",
            "Left %", "Right %", "Ace %", "Chop Off %", "Split %", "Head Pin %"};

    private static final String[] STATS_BOWLER_LEAGUE_NAMES = {"High Single", "High Series",
            "Total Pinfall", "# of Games", "Average" + "Pins Left Standing", "Average Pins Left"};

    private static final int STATS_BOWLER = 0;
    private static final int STATS_LEAGUE = 1;
    private static final int STATS_GAME = 2;

    private String bowlerName = null;
    private String leagueName = null;
    private long bowlerID = -1;
    private long leagueID = -1;
    private long seriesID = -1;
    private long gameID = -1;

    private ListView listStats = null;
    private String bowlerOrLeagueName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        SharedPreferences preferences = getSharedPreferences(Preferences.MY_PREFS, MODE_PRIVATE);
        bowlerName = preferences.getString(Preferences.NAME_BOWLER, "");
        leagueName = preferences.getString(Preferences.NAME_LEAGUE, "");
        bowlerID = preferences.getLong(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);
        leagueID = preferences.getLong(LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID, -1);
        seriesID = preferences.getLong(SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID, -1);
        gameID = preferences.getLong(GameEntry.TABLE_NAME + "." + GameEntry._ID, -1);
        bowlerOrLeagueName = getIntent().getStringExtra("BowlerLeagueName");

        listStats = (ListView)findViewById(R.id.list_stats);

        if (gameID == -1)
        {
            if (leagueID == -1)
            {
                setTitle(R.string.title_activity_stats_bowler);
                loadStats(STATS_BOWLER);
            }
            else
            {
                setTitle(R.string.title_activity_stats_league);
                loadStats(STATS_LEAGUE);
            }
        }
        else
        {
            setTitle(R.string.title_activity_stats_game);
            loadStats(STATS_GAME);
        }
    }

    private void loadStats(int bowlerLeagueOrGame)
    {
        String[] generalStats = new String[STATS_UNIVERSAL_NAMES.length];
        for (int i = 0; i < generalStats.length; i++)
        {
            generalStats[i] = STATS_UNIVERSAL_NAMES[i] + ": 0";
        }

        List<String> statsList = new ArrayList<String>();

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

    private Cursor getCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2] + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER
                + " FROM " + LeagueEntry.TABLE_NAME + " league"
                + " LEFT JOIN " + GameEntry.TABLE_NAME + " game"
                + " ON " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=" + GameEntry.COLUMN_NAME_BOWLER_ID
                + " LEFT JOIN " + FrameEntry.TABLE_NAME + " frame"
                + " ON " + GameEntry.COLUMN_NAME_BOWLER_ID + "=" + FrameEntry.COLUMN_NAME_BOWLER_ID
                + " WHERE " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=?"
                + " ORDER BY league." + LeagueEntry._ID + ", game." + GameEntry._ID + ", frame." + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(bowlerID)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }
}
