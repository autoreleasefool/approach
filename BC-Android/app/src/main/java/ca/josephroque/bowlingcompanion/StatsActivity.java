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
                showBowlerStats();
            }
            else
            {
                setTitle(R.string.title_activity_stats_league);
                showLeagueStats();
            }
        }
        else
        {
            setTitle(R.string.title_activity_stats_game);
            showGameStats();
        }
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
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBowlerStats()
    {
        List<String> bowlerStats = new ArrayList<String>();
        bowlerStats.add("Bowler: " + bowlerName);

        for (String name:STATS_UNIVERSAL_NAMES)
        {
            bowlerStats.add(name + ": 0");
        }

        for (String name: STATS_BOWLER_LEAGUE_NAMES)
        {
            bowlerStats.add(name + ": 0");
        }

        ArrayAdapter<String> bowlerAdapter = new ArrayAdapter<String>(StatsActivity.this, android.R.layout.simple_list_item_1, bowlerStats);
        listStats.setAdapter(bowlerAdapter);
        bowlerAdapter.notifyDataSetChanged();
    }

    private void showLeagueStats()
    {
        List<String> leagueStats = new ArrayList<String>();
        leagueStats.add("Bowler: " + bowlerName);
        leagueStats.add("League: " + leagueName);

        for (String name:STATS_UNIVERSAL_NAMES)
        {
            leagueStats.add(name + ": 0");
        }

        for (String name:STATS_BOWLER_LEAGUE_NAMES)
        {
            leagueStats.add(name + ": 0");
        }

        ArrayAdapter<String> leagueAdapter = new ArrayAdapter<String>(StatsActivity.this, android.R.layout.simple_list_item_1, leagueStats);
        listStats.setAdapter(leagueAdapter);
        leagueAdapter.notifyDataSetChanged();
    }

    private void showGameStats()
    {
        List<String> gameStats = new ArrayList<String>();
        gameStats.add("Bowler: " + bowlerName);
        gameStats.add("League: " + leagueName);

        for (String name:STATS_UNIVERSAL_NAMES)
        {
            gameStats.add(name + ": 0");
        }

        ArrayAdapter<String> gameAdapter = new ArrayAdapter<String>(StatsActivity.this, android.R.layout.simple_list_item_1, gameStats);
        listStats.setAdapter(gameAdapter);
        gameAdapter.notifyDataSetChanged();
    }

    /*private Cursor getCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES
                + ;
        String[] rawStatsArgs = {};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }*/
}
