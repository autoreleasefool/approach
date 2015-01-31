package ca.josephroque.bowlingcompanion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.StatsListAdapter;
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
            {"Middle Hit", "Strikes", "Spare Conversions"};
    /** Detailed stats relating to hitting the middle pin */
    private static final String[] STATS_MIDDLE_DETAILED =
            {"Head Pins", "Head Pins Spared", "Lefts", "Lefts Spared", "Rights", "Rights Spared", "Aces", "Aces Spared", "Chop Offs", "Chop Offs Spared", "Left Chop Offs", "Left Chop Offs Spared", "Right Chop Offs", "Right Chop Offs Spared", "Splits", "Splits Spared", "Left Splits", "Left Splits Spared", "Right Splits", "Right Splits Spared"};
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
                loadBowlerOrLeagueStats(false);
            }
            else
            {
                setTitle(R.string.title_activity_stats_league);
                loadBowlerOrLeagueStats(true);
            }
        }
        else
        {
            setTitle(R.string.title_activity_stats_game);
            loadGameStats();
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads the stats relevant to the currently selected bowler or league
     *
     * @param shouldGetLeagueStats indicates whether stats relevant to a league
     *                             or a bowler should be loaded
     */
    private void loadBowlerOrLeagueStats(boolean shouldGetLeagueStats)
    {
        final int NUMBER_OF_GENERAL_DETAILS = (shouldGetLeagueStats) ? 2 : 1;
        List<String> statNamesList = new ArrayList<String>();
        statNamesList.add("Bowler");
        if (shouldGetLeagueStats)
            statNamesList.add("League");
        statNamesList.addAll(Arrays.asList(STATS_MIDDLE_GENERAL));
        statNamesList.addAll(Arrays.asList(STATS_MIDDLE_DETAILED));
        statNamesList.addAll(Arrays.asList(STATS_FOULS));
        statNamesList.addAll(Arrays.asList(STATS_PINS_TOTAL));
        statNamesList.addAll(Arrays.asList(STATS_PINS_AVERAGE));
        statNamesList.addAll(Arrays.asList(STATS_GENERAL));

        List<String> statValuesList = new ArrayList<String>();
        statValuesList.add(bowlerName);
        if (shouldGetLeagueStats)
            statValuesList.add(leagueName);
        int i = statValuesList.size();
        while (i < statNamesList.size())
        {
            statValuesList.add("--");
            i++;
        }

        int[] statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                + STATS_GENERAL.length];

        int totalShotsAtMiddle = 0;
        int spareChances = 0;
        int seriesTotal = 0;
        Cursor cursor = getBowlerOrLeagueCursor(shouldGetLeagueStats);
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);
                int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                boolean[][] balls = new boolean[3][5];
                for (i = 0; i < 5; i++)
                {
                    balls[0][i] = ballStrings[0].charAt(i) == '1';
                    balls[1][i] = ballStrings[1].charAt(i) == '1';
                    balls[2][i] = ballStrings[2].charAt(i) == '1';
                }

                for (i = 1; i <= 3; i++)
                {
                    if (frameFouls.contains(String.valueOf(i)))
                        statValues[Constants.STAT_FOULS]++;
                }

                if (frameAccessed)
                {
                    if (frameNumber == 10)
                    {
                        totalShotsAtMiddle++;
                        int firstBall = getFirstBallValue(balls[0]);
                        if (firstBall != -1)
                            statValues[Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(firstBall, statValues, 0);

                        if (firstBall < 5)
                            spareChances++;

                        if (firstBall != 0)
                        {
                            if (Arrays.equals(balls[1], Constants.FRAME_CLEAR))
                            {
                                statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(firstBall, statValues, 1);
                            }
                            else
                            {
                                statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            int secondBall = getFirstBallValue(balls[1]);
                            if (secondBall != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(secondBall, statValues, 0);

                            if (secondBall != 0)
                            {
                                if (Arrays.equals(balls[2], Constants.FRAME_CLEAR))
                                {
                                    statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(secondBall, statValues, 1);
                                }
                                else
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                                }
                            }
                            else
                            {
                                totalShotsAtMiddle++;
                                int thirdBall = getFirstBallValue(balls[2]);
                                if (thirdBall != -1)
                                    statValues[Constants.STAT_MIDDLE_HIT]++;
                                increaseFirstBallStat(thirdBall, statValues, 0);

                                if (thirdBall != 0)
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                                }
                            }
                        }
                    }
                    else
                    {
                        totalShotsAtMiddle++;
                        int firstBall = getFirstBallValue(balls[0]);
                        if (firstBall != -1)
                            statValues[Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(firstBall, statValues, 0);

                        if (firstBall < 5)
                            spareChances++;

                        if (firstBall != 0)
                        {
                            if (Arrays.equals(balls[1], Constants.FRAME_CLEAR))
                            {
                                statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(firstBall, statValues, 1);
                            } else
                            {
                                statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                            }
                        }
                    }
                }

                if (frameNumber == 1)
                {
                    int gameScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                    int gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_NUMBER));
                    if (statValues[Constants.STAT_HIGH_SINGLE] < gameScore)
                        statValues[Constants.STAT_HIGH_SINGLE] = gameScore;
                    statValues[Constants.STAT_TOTAL_PINFALL] += gameScore;
                    statValues[Constants.STAT_NUMBER_OF_GAMES]++;

                    if (gameNumber == 1)
                    {
                        if (statValues[Constants.STAT_HIGH_SERIES] < seriesTotal)
                            statValues[Constants.STAT_HIGH_SERIES] = seriesTotal;
                        seriesTotal = gameScore;
                    }
                    else
                    {
                        seriesTotal += gameScore;
                    }
                }
                cursor.moveToNext();
            }
        }
        if (statValues[Constants.STAT_HIGH_SERIES] < seriesTotal)
        {
            statValues[Constants.STAT_HIGH_SERIES] = seriesTotal;
        }

        if (statValues[Constants.STAT_NUMBER_OF_GAMES] > 0)
        {
            statValues[Constants.STAT_AVERAGE] =
                    statValues[Constants.STAT_TOTAL_PINFALL] / statValues[Constants.STAT_NUMBER_OF_GAMES];
            statValues[Constants.STAT_AVERAGE_PINS_LEFT_ON_DECK] =
                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] / statValues[Constants.STAT_NUMBER_OF_GAMES];
        }

        setGeneralAndDetailedStatValues(statValuesList, statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS, statNamesList);
        StatsListAdapter statsListAdapter = new StatsListAdapter(this, statNamesList, statValuesList);
        listStats.setAdapter(statsListAdapter);
    }

    /**
     * Loads the stats relevant to the currently selected game
     */
    private void loadGameStats()
    {
        final int NUMBER_OF_GENERAL_DETAILS = 3;
        List<String> statNamesList = new ArrayList<String>();
        statNamesList.add("Bowler");
        statNamesList.add("League");
        statNamesList.add("Game #");
        statNamesList.addAll(Arrays.asList(STATS_MIDDLE_GENERAL));
        statNamesList.addAll(Arrays.asList(STATS_MIDDLE_DETAILED));
        statNamesList.addAll(Arrays.asList(STATS_FOULS));
        statNamesList.addAll(Arrays.asList(STATS_PINS_TOTAL));

        List<String> statValuesList = new ArrayList<String>();
        statValuesList.add(bowlerName);
        statValuesList.add(leagueName);
        statValuesList.add(String.valueOf(gameNumber));
        int i = statValuesList.size();
        while (i < statNamesList.size())
        {
            statValuesList.add("--");
            i++;
        }

        int[] statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                + STATS_FOULS.length + STATS_PINS_TOTAL.length];

        int totalShotsAtMiddle = 0;
        int spareChances = 0;
        Cursor cursor = getGameCursor();
        if (cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);
                if (!frameAccessed)
                    break;

                int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                        cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                boolean[][] balls = new boolean[3][5];
                for (i = 0; i < 5; i++)
                {
                    balls[0][i] = ballStrings[0].charAt(i) == '1';
                    balls[1][i] = ballStrings[1].charAt(i) == '1';
                    balls[2][i] = ballStrings[2].charAt(i) == '1';
                }

                for (i = 1; i <= 3; i++)
                {
                    if (frameFouls.contains(String.valueOf(i)))
                        statValues[Constants.STAT_FOULS]++;
                }

                if (frameNumber == 10)
                {
                    totalShotsAtMiddle++;
                    int firstBall = getFirstBallValue(balls[0]);
                    if (firstBall != -1)
                        statValues[Constants.STAT_MIDDLE_HIT]++;
                    increaseFirstBallStat(firstBall, statValues, 0);

                    if (firstBall < 5)
                        spareChances++;

                    if (firstBall != 0)
                    {
                        if (Arrays.equals(balls[1], Constants.FRAME_CLEAR))
                        {
                            statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                            increaseFirstBallStat(firstBall, statValues, 1);
                        }
                        else
                        {
                            statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                        }
                    }
                    else
                    {
                        totalShotsAtMiddle++;
                        int secondBall = getFirstBallValue(balls[1]);
                        if (secondBall != -1)
                            statValues[Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(secondBall, statValues, 0);

                        if (secondBall != 0)
                        {
                            if (Arrays.equals(balls[2], Constants.FRAME_CLEAR))
                            {
                                statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(secondBall, statValues, 1);
                            }
                            else
                            {
                                statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            int thirdBall = getFirstBallValue(balls[2]);
                            if (thirdBall != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(thirdBall, statValues, 0);

                            if (thirdBall != 0)
                            {
                                statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                            }
                        }
                    }
                }
                else
                {
                    totalShotsAtMiddle++;
                    int firstBall = getFirstBallValue(balls[0]);
                    if (firstBall != -1)
                        statValues[Constants.STAT_MIDDLE_HIT]++;
                    increaseFirstBallStat(firstBall, statValues, 0);

                    if (firstBall < 5)
                        spareChances++;

                    if (firstBall != 0)
                    {
                        if (Arrays.equals(balls[1], Constants.FRAME_CLEAR))
                        {
                            statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                            increaseFirstBallStat(firstBall, statValues, 1);
                        }
                        else
                        {
                            statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(balls[2]);
                        }
                    }
                }
                cursor.moveToNext();
            }
        }

        setGeneralAndDetailedStatValues(statValuesList, statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS, statNamesList);
        StatsListAdapter statsListAdapter = new StatsListAdapter(this, statNamesList, statValuesList);
        listStats.setAdapter(statsListAdapter);
    }

    /**
     * Increases certain values in the array depending on other parameters
     *
     * @param firstBall value which corresponds to the ball which was thrown
     * @param statValues array with values to be increased
     * @param offset offsets value to increase by up to 1
     */
    private void increaseFirstBallStat(int firstBall, int[] statValues, int offset)
    {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch(firstBall)
        {
            case 0:
                if (offset == 0)
                {
                    statValues[Constants.STAT_STRIKES]++;
                }
                break;
            case 1:statValues[Constants.STAT_LEFTS + offset]++; break;
            case 2:statValues[Constants.STAT_RIGHTS + offset]++; break;
            case 3:
                statValues[Constants.STAT_LEFT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case 4:
                statValues[Constants.STAT_RIGHT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case 5:statValues[Constants.STAT_ACES + offset]++; break;
            case 6:
                statValues[Constants.STAT_LEFT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case 7:
                statValues[Constants.STAT_RIGHT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case 8:statValues[Constants.STAT_HEAD_PINS + offset]++;
        }
    }

    /**
     * Counts the value of pins which have not been knocked down after the third ball
     * @param thirdBall state of pins after third ball
     * @return combined value of pins which are still standing
     */
    private int countPinsLeftStanding(boolean[] thirdBall)
    {
        int pinsLeftStanding = 0;
        for (int i = 0; i < thirdBall.length; i++)
        {
            if (!thirdBall[i])
            {
                switch(i)
                {
                    case 0:case 4:pinsLeftStanding += 2; break;
                    case 1:case 3:pinsLeftStanding += 3; break;
                    case 2:pinsLeftStanding += 5; break;
                }
            }
        }
        return pinsLeftStanding;
    }

    /**
     * Returns a value which indicates which pins were knocked down on the first ball
     *
     * @param firstBall state of the pins after the first ball
     * @return numeric value from -2 to 7
     */
    private int getFirstBallValue(boolean[] firstBall)
    {
        if (!firstBall[2])
        {
            return -1;
        }

        int numberOfPinsKnockedDown = 0;
        for (int i = 0; i < firstBall.length; i++)
        {
            if (firstBall[i])
                numberOfPinsKnockedDown++;
        }

        if (numberOfPinsKnockedDown == 5)
            return 0;       //STRIKE
        else if (numberOfPinsKnockedDown == 4)
        {
            if (!firstBall[0])
                return 1;   //LEFT
            else if (!firstBall[4])
                return 2;   //RIGHT
        }
        else if (numberOfPinsKnockedDown == 3)
        {
            if (!firstBall[3] && !firstBall[4])
                return 3;   //LEFT CHOP
            else if (!firstBall[0] && !firstBall[1])
                return 4;   //RIGHT CHOP
            else if (!firstBall[0] && !firstBall[4])
                return 5;   //ACE
        }
        else if (numberOfPinsKnockedDown == 2)
        {
            if (firstBall[1])
                return 6;   //LEFT SPLIT
            else if (firstBall[3])
                return 7;   //RIGHT SPLIT
        }
        else
            return 8;       //HEAD PIN

        return -2;
    }

    /**
     * Sets values in list from other stat parameters
     *
     * @param statValuesList list of strings which will display stat values
     * @param statValues calculated values of status
     * @param totalShotsAtMiddle number of shots the bowler has had at the middle
     * @param spareChances number of chances the bowler has had to spare
     * @param statOffset number of stats at start of list which do not need to be set
     */
    private void setGeneralAndDetailedStatValues(final List<String> statValuesList, final int[] statValues, final int totalShotsAtMiddle, final int spareChances, final int statOffset, List<String> statNamesList)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[Constants.STAT_MIDDLE_HIT] > 0)
        {
            statValuesList.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_MIDDLE_HIT] / (double)totalShotsAtMiddle * 100)
                    + "% [" + statValues[Constants.STAT_MIDDLE_HIT] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_STRIKES] > 0)
        {
            statValuesList.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_STRIKES] / (double) totalShotsAtMiddle * 100)
                    + "% [" + statValues[Constants.STAT_STRIKES] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            statValuesList.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_SPARE_CONVERSIONS] / (double) spareChances * 100)
                            + "% [" + statValues[Constants.STAT_SPARE_CONVERSIONS] + "/" + spareChances + "]");
        }
        currentStatPosition++;

        for (int i = Constants.STAT_HEAD_PINS; i < Constants.STAT_RIGHT_SPLITS_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[i] > 0)
            {
                statValuesList.set(currentStatPosition,
                        decimalFormat.format(statValues[i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[i] + "/" + totalShotsAtMiddle + "]");
            }
            if (statValues[i + 1] > 0)
            {
                statValuesList.set(currentStatPosition + 1,
                        decimalFormat.format(statValues[i + 1] / (double)statValues[i] * 100)
                        + "% [" + statValues[i + 1] + "/" + statValues[i] + "]");
            }
        }

        final int statValuesListSize = statValuesList.size();
        for (int i = Constants.STAT_FOULS; i <= Constants.STAT_NUMBER_OF_GAMES && statValuesListSize > currentStatPosition; i++, currentStatPosition++)
        {
            statValuesList.set(currentStatPosition, String.valueOf(statValues[i]));
        }
    }

    /**
     * Returns a query from the database with data necessary to determine statistics
     * of a bowler or a league
     *
     * @param shouldGetLeagueStats if true, then a cursor relevant to league stats
     *                             will be returned. Otherwise, a cursor for bowler
     *                             stats will be returned.
     * @return a cursor with the result of the query
     */
    private Cursor getBowlerOrLeagueCursor(boolean shouldGetLeagueStats)
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
                + ((shouldGetLeagueStats)
                    ? " WHERE game." + GameEntry.COLUMN_NAME_LEAGUE_ID + "=?"
                    : " WHERE game." + GameEntry.COLUMN_NAME_BOWLER_ID + "=?")
                + " ORDER BY game." + GameEntry._ID + ", frame." + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {(shouldGetLeagueStats)
                ? String.valueOf(leagueID)
                : String.valueOf(bowlerID)};

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
