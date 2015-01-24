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
        String[] stats = concatenateArrays(STATS_MIDDLE_GENERAL, STATS_MIDDLE_DETAILED,
                STATS_FOULS, STATS_PINS_TOTAL, STATS_PINS_AVERAGE, STATS_GENERAL);

        int highSingle = 0;
        int highSeries = 0;
        int totalPinfall = 0;
        int numberOfGames = 0;
        int seriesTotal = 0;
        int totalShotsAtMiddle = 0;
        int[] statValues = new int[11];
        int[] statSpared = new int[6];
        int numberOfGamesInSeries = 0;

        Cursor cursor = getLeagueCursor();
        int count = 0;
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                int gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_NUMBER));
                int numberOfFouls = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                String[] ballString = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);
                for (int i = 0; i < 3; i++)
                {
                    if (numberOfFouls / Math.pow(2, i) > 0)
                    {
                        statValues[9]++;
                        numberOfFouls %= (int)(Math.pow(2, i));
                    }
                }

                if (frameAccessed)
                {
                    if (frameNumber < 10)
                    {
                        totalShotsAtMiddle++;
                    }

                    if (frameNumber == 10)
                    {
                        for (int b = 0; b < 3; b++)
                        {
                            totalShotsAtMiddle++;

                            if (ballString[b].equals("11111"))
                            {
                                statValues[0]++;
                                statValues[1]++;
                            }
                            else
                            {
                                int firstBall = getStatIndexForBall(ballString[b]);
                                if (firstBall >= 0)
                                    statValues[firstBall]++;
                                if (firstBall != -1)
                                    statValues[0]++;

                                if (b < 2 && ballString[b + 1].equals("11111"))
                                {
                                    statValues[2]++;
                                    if (firstBall >= 3)
                                        statSpared[firstBall - 3]++;
                                }
                                else
                                {
                                    for (int i = 0; i < 5; i++)
                                    {
                                        if (ballString[2].charAt(i) == '0')
                                        {
                                            switch(i)
                                            {
                                                case 0:case 4:statValues[10] += 2; break;
                                                case 1:case 3:statValues[10] += 3; break;
                                                case 2:statValues[10] += 5; break;
                                            }
                                        }
                                    }
                                    //break;
                                }
                            }
                        }
                    }
                    else
                    {
                        if (ballString[0].equals("11111"))
                        {
                            statValues[1]++;
                            statValues[0]++;
                        }
                        else
                        {
                            int firstBall = getStatIndexForBall(ballString[0]);

                            if (firstBall >= 0)
                            {
                                statValues[firstBall]++;
                            }
                            if (firstBall != -1)
                            {
                                statValues[0]++;
                            }

                            if (ballString[1].equals("11111"))
                            {
                                statValues[2]++;
                                if (firstBall >= 3)
                                    statSpared[firstBall - 3]++;
                            }
                            else
                            {
                                for (int i = 0; i < 5; i++)
                                {
                                    if (ballString[2].charAt(i) == '0')
                                    {
                                        switch(i)
                                        {
                                            case 0:case 4:statValues[10] += 2; break;
                                            case 1:case 3:statValues[10] += 3; break;
                                            case 2:statValues[10] += 5; break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (frameNumber == 1)
                {
                    int gameScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                    if (highSingle < gameScore)
                        highSingle = gameScore;
                    totalPinfall += gameScore;
                    seriesTotal += gameScore;
                    numberOfGames++;

                    if (gameNumber == numberOfGamesInSeries)
                    {
                        if (highSeries < seriesTotal)
                            highSeries = seriesTotal;
                        seriesTotal = 0;
                    }
                }

                cursor.moveToNext();
            }
        }
        int averageScore = (numberOfGames > 0)
                ? totalPinfall / numberOfGames
                : 0;
        int averagePinsLeftOnDeck = (numberOfGames > 0)
                ? statValues[10] / numberOfGames
                : 0;

        DecimalFormat decimalFormat = new DecimalFormat("##0.##");

        for (int i = 0; i < STATS_MIDDLE_GENERAL.length; i++)
        {
            stats[i] = stats[i] + ((totalShotsAtMiddle > 0)
                    ? decimalFormat.format(statValues[i] / (double)totalShotsAtMiddle * 100) + "% [" + statValues[i] + "]"
                    : "0% [0]");
        }

        final int startingStatIndex = STATS_MIDDLE_GENERAL.length;
        for (int i = 0, currentStatIndex = 0; i < STATS_MIDDLE_DETAILED.length; i++, currentStatIndex += 2)
        {
            if (totalShotsAtMiddle > 0)
            {
                stats[startingStatIndex + currentStatIndex] = stats[startingStatIndex + currentStatIndex] + decimalFormat.format(statValues[startingStatIndex + i] / (double) totalShotsAtMiddle * 100) + "% [" + statValues[startingStatIndex + i] + "]";
                stats[startingStatIndex + currentStatIndex + 1] = stats[startingStatIndex + currentStatIndex + 1] + decimalFormat.format(statSpared[i] / (double) (statValues[startingStatIndex + i] > 0 ? statValues[startingStatIndex + i] : 1) * 100) + "% [" + statSpared[i] + "]";
            }
            else
            {
                stats[startingStatIndex + currentStatIndex] = stats[startingStatIndex + currentStatIndex] + "0% [0]";
                stats[startingStatIndex + currentStatIndex + 1] = stats[startingStatIndex + currentStatIndex + 1] + "0% [0]";
            }
        }

        //TODO: fix magic numbers, 9 is totalFouls index, 10 is totalPinsLeftOnDeck index
        stats[15] = stats[15] + statValues[9];
        stats[16] = stats[16] + statValues[10];
        stats[17] = stats[17] + averagePinsLeftOnDeck;
        stats[18] = stats[18] + averageScore;
        stats[19] = stats[19] + highSingle;
        stats[20] = stats[20] + highSeries + " (" + numberOfGamesInSeries + " game" + ((numberOfGamesInSeries > 1)
                ? "s)"
                : ")");
        stats[21] = stats[21] + totalPinfall;
        stats[22] = stats[22] + numberOfGames;

        List<String> statsList = new ArrayList<String>();
        statsList.add("Bowler: " + bowlerName);
        statsList.add("League: " + leagueName);
        statsList.addAll(Arrays.asList(stats));
        ArrayAdapter<String> statsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statsList);
        ListView listView = (ListView)findViewById(R.id.list_stats);
        listView.setAdapter(statsAdapter);
    }

    /**
     * Loads the stats relevant to the currently selected game
     */
    private void loadGameStats()
    {
        String[] stats = concatenateArrays(STATS_MIDDLE_GENERAL, STATS_MIDDLE_DETAILED, STATS_FOULS, STATS_PINS_TOTAL);

        int totalShotsAtMiddle = 1;
        int[] statValues = new int[11];
        int[] statSpared = new int[6];

        Cursor cursor = getGameCursor();
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                String[] ballString = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                int numberOfFouls = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                for (int i = 0; i < 3; i++)
                {
                    if (numberOfFouls / Math.pow(2, i) > 0)
                    {
                        statValues[9]++;
                        numberOfFouls %= (int)(Math.pow(2, i));
                    }
                }
                boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);

                if (!frameAccessed)
                {
                    break;
                }

                if (frameNumber > 1 && frameNumber < 10)
                {
                    totalShotsAtMiddle++;
                }

                if (frameNumber == 10)
                {
                    for (int b = 0; b < 3; b++)
                    {
                        totalShotsAtMiddle++;

                        if (ballString[b].equals("11111"))
                        {
                            statValues[0]++;
                            statValues[1]++;
                        }
                        else
                        {
                            int firstBall = getStatIndexForBall(ballString[b]);
                            if (firstBall >= 0)
                                statValues[firstBall]++;
                            if (firstBall != -1)
                                statValues[0]++;

                            if (b < 2 && ballString[b + 1].equals("11111"))
                            {
                                statValues[2]++;
                                if (firstBall >= 3)
                                    statSpared[firstBall - 3]++;
                            }
                            else
                            {
                                for (int i = 0; i < 5; i++)
                                {
                                    if (ballString[2].charAt(i) == '0')
                                    {
                                        switch(i)
                                        {
                                            case 0:case 4:statValues[10] += 2; break;
                                            case 1:case 3:statValues[10] += 3; break;
                                            case 2:statValues[10] += 5; break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                else
                {
                    if (ballString[0].equals("11111"))
                    {
                        statValues[1]++;
                        statValues[0]++;
                    }
                    else
                    {
                        int firstBall = getStatIndexForBall(ballString[0]);

                        if (firstBall >= 0)
                        {
                            statValues[firstBall]++;
                        }
                        if (firstBall != -1)
                        {
                            statValues[0]++;
                        }

                        if (ballString[1].equals("11111"))
                        {
                            statValues[2]++;
                            if (firstBall >= 3)
                                statSpared[firstBall - 3]++;
                        }
                        else
                        {
                            for (int i = 0; i < 5; i++)
                            {
                                if (ballString[2].charAt(i) == '0')
                                {
                                    switch(i)
                                    {
                                        case 0:case 4:statValues[10] += 2; break;
                                        case 1:case 3:statValues[10] += 3; break;
                                        case 2:statValues[10] += 5; break;
                                    }
                                }
                            }
                        }
                    }
                }
                cursor.moveToNext();
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("##0.##");

        for (int i = 0; i < STATS_MIDDLE_GENERAL.length; i++)
        {
            stats[i] = stats[i] + decimalFormat.format(statValues[i] / (double)totalShotsAtMiddle * 100) + "% [" + statValues[i] + "]";
        }

        final int startingStatIndex = STATS_MIDDLE_GENERAL.length;
        for (int i = 0, currentStatIndex = 0; i < STATS_MIDDLE_DETAILED.length / 2; i++, currentStatIndex += 2)
        {
            stats[startingStatIndex + currentStatIndex] = stats[startingStatIndex + currentStatIndex] + decimalFormat.format(statValues[startingStatIndex + i] / (double)totalShotsAtMiddle * 100) + "% [" + statValues[startingStatIndex + i] + "]";
            stats[startingStatIndex + currentStatIndex + 1] = stats[startingStatIndex + currentStatIndex + 1] + decimalFormat.format(statSpared[i] / (double)(statValues[startingStatIndex + i] > 0 ? statValues[startingStatIndex + i]:1) * 100) + "% [" + statSpared[i] + "]";
        }

        //TODO: fix magic numbers, 9 is totalFouls index, 10 is totalPinsLeftOnDeck index
        stats[15] = stats[15] + statValues[9];
        stats[16] = stats[16] + statValues[10];

        List<String> statsList = new ArrayList<String>();
        statsList.add("Bowler: " + bowlerName);
        statsList.add("League: " + leagueName);
        statsList.add("Game number: " + gameNumber);
        statsList.addAll(Arrays.asList(stats));
        ArrayAdapter<String> statsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statsList);
        ListView listView = (ListView)findViewById(R.id.list_stats);
        listView.setAdapter(statsAdapter);
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
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + GameEntry.COLUMN_NAME_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
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

    private static int getStatIndexForBall(String ball)
    {
        if (ball.charAt(2) == '0')
            return -1;

        else if (ball.equals("00100"))
            return 3;
        else if (ball.equals("01111"))
            return 4;
        else if (ball.equals("11110"))
            return 5;
        else if (ball.equals("01110"))
            return 6;
        else if (ball.equals("00111") || ball.equals("11100"))
            return 7;
        else if (ball.equals("01100") || ball.equals("00110"))
            return 8;
        else
            return -2;
    }

    private static <T> T[] concatenateArrays(T[] firstArray, T[]... rest)
    {
        int totalArrayLength = firstArray.length;
        for (T[] array : rest)
        {
            totalArrayLength += array.length;
        }

        T[] result = Arrays.copyOf(firstArray, totalArrayLength);
        int offset = firstArray.length;
        for (T[] array : rest)
        {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
