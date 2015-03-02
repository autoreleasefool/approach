package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.StatsAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;

public class StatsActivity extends ActionBarActivity
    implements ChangeableTheme
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "StatsActivity";

    /** Represent names of general stats related to the middle pin */
    private static final String[] STATS_MIDDLE_GENERAL =
            {"Middle Hit", "Strikes", "Spare Conversions"};
    /** Represent names of specific stats related to middle pin*/
    private static final String[] STATS_MIDDLE_DETAILED =
            {"Head Pins", "Head Pins Spared",
            "Lefts", "Lefts Spared", "Rights", "Rights Spared",
            "Aces", "Aces Spared",
            "Chop Offs", "Chop Offs Spared", "Left Chop Offs", "Left Chop Offs Spared", "Right Chop Offs", "Right Chop Offs Spared",
            "Splits", "Splits Spared", "Left Splits", "Left Splits Spared", "Right Splits", "Right Splits Spared"};
    /** Represent names of stats related to fouls */
    private static final String[] STATS_FOULS =
            {"Fouls"};
    /** Represent names of stats related to pins left standing at the end of each frame */
    private static final String[] STATS_PINS_TOTAL =
            {"Pins Left"};
    /** Represent names of stats related to average pins left standing per game */
    private static final String[] STATS_PINS_AVERAGE =
            {"Average Pins Left"};
    /** Represent games of general stats about a bowler, league. or event */
    private static final String[] STATS_GENERAL =
            {"Average", "High Single", "High Series", "Total Pinfall", "# of Games"};

    /** Indicates all the stats related to the specified bowler should be loaded */
    private static final byte LOADING_BOWLER_STATS = 0;
    /** Indicates all the stats related to the specified league should be loaded */
    private static final byte LOADING_LEAGUE_STATS = 1;
    /** Indicates only the stats related to the specified game should be loaded */
    private static final byte LOADING_GAME_STATS = 2;

    /** Displays the stat names and values in a list to the user */
    private RecyclerView mStatsRecycler;
    /** Organizes stat data into a list to be displayed by mStatsRecycler */
    private StatsAdapter mStatsAdapter;

    /** List of names of stats that will be displayed to the user */
    private List<String> mListStatNames;
    /** List of values of stats corresponding to those named in mListStatNames */
    private List<String> mListStatValues;

    /** Name of the bowler whose stats are being displayed */
    private String mBowlerName;
    /** Name of the league whose stats are being displayed */
    private String mLeagueName;
    /** Id of the bowler whose stats will be loaded and displayed */
    private long mBowlerId;
    /** Id of the league whose stats will be loaded and displayed */
    private long mLeagueId;
    /** Id of the game whose stats will be loaded and displayed */
    private long mGameId;
    /** Number of the game in the series being loaded */
    private byte mGameNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //Enables backtracking to activity which created this stats activity, since it's not always the same
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListStatNames = new ArrayList<>();
        mListStatValues = new ArrayList<>();

        mStatsRecycler = (RecyclerView)findViewById(R.id.recyclerView_stats);
        mStatsRecycler.setHasFixedSize(true);
        mStatsRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mStatsRecycler.setLayoutManager(layoutManager);

        mStatsAdapter = new StatsAdapter(this, mListStatNames, mListStatValues);
        mStatsRecycler.setAdapter(mStatsAdapter);
        updateTheme();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mBowlerId = getIntent().getLongExtra(Constants.EXTRA_ID_BOWLER, -1);
        mLeagueId = getIntent().getLongExtra(Constants.EXTRA_ID_LEAGUE, -1);
        mGameId = getIntent().getLongExtra(Constants.EXTRA_ID_GAME, -1);

        mListStatNames.clear();
        mListStatValues.clear();

        byte statsToLoad;
        int titleToSet;
        if (mGameId == -1)
        {

            if (mLeagueId == -1)
            {
                titleToSet = R.string.title_activity_stats_bowler;
                statsToLoad = LOADING_BOWLER_STATS;
            }
            else
            {
                mLeagueName = getIntent().getStringExtra(Constants.EXTRA_NAME_LEAGUE);
                titleToSet = R.string.title_activity_stats_league;
                statsToLoad = LOADING_LEAGUE_STATS;
            }
        }
        else
        {
            mBowlerName = getIntent().getStringExtra(Constants.EXTRA_NAME_BOWLER);
            mLeagueName = getIntent().getStringExtra(Constants.EXTRA_NAME_LEAGUE);
            mGameNumber = getIntent().getByteExtra(Constants.EXTRA_GAME_NUMBER, (byte)-1);
            titleToSet = R.string.title_activity_stats_game;
            statsToLoad = LOADING_GAME_STATS;
        }

        if(Theme.getStatsActivityThemeInvalidated())
        {
            updateTheme();
        }

        setTitle(titleToSet);
        new LoadStatsTask().execute(statsToLoad);
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
        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new settings activity and displays it to the user
     */
    private void showSettingsMenu()
    {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra(Constants.EXTRA_SETTINGS_SOURCE, TAG);
        startActivity(settingsIntent);
    }

    /**
     * Checks which situation has occurred by the state of the pins in ball
     *
     * @param ball result of the pins after a ball was thrown
     * @param statValues stat values to update
     * @param offset indicates a spare was thrown and the spare count should be increased for a stat
     */
    private void increaseFirstBallStat(int ball, int[] statValues, int offset)
    {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch(ball)
        {
            case Constants.BALL_VALUE_STRIKE:
                if (offset == 0)
                {
                    statValues[Constants.STAT_STRIKES]++;
                }
                break;
            case Constants.BALL_VALUE_LEFT:statValues[Constants.STAT_LEFTS + offset]++; break;
            case Constants.BALL_VALUE_RIGHT:statValues[Constants.STAT_RIGHTS + offset]++; break;
            case Constants.BALL_VALUE_LEFT_CHOP:
                statValues[Constants.STAT_LEFT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_CHOP:
                statValues[Constants.STAT_RIGHT_CHOP_OFFS + offset]++;
                statValues[Constants.STAT_CHOP_OFFS + offset]++;
                break;
            case Constants.BALL_VALUE_ACE:statValues[Constants.STAT_ACES + offset]++; break;
            case Constants.BALL_VALUE_LEFT_SPLIT:
                statValues[Constants.STAT_LEFT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_SPLIT:
                statValues[Constants.STAT_RIGHT_SPLITS + offset]++;
                statValues[Constants.STAT_SPLITS + offset]++;
                break;
            case Constants.BALL_VALUE_HEAD_PIN:statValues[Constants.STAT_HEAD_PINS + offset]++;
        }
    }

    /**
     * Counts the total value of pins which were left at the end of a frame on the third ball
     *
     * @param thirdBall state of the pins after the third ball
     * @return total value of pins left standing
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
     * Returns the indicated state of the pins after a ball was thrown
     *
     * @param firstBall the ball thrown
     * @return the state of the pins after a ball was thrown
     */
    private int getFirstBallValue(boolean[] firstBall)
    {
        if (!firstBall[2])
        {
            return -1;
        }

        int numberOfPinsKnockedDown = 0;
        for (boolean knockedDown: firstBall)
        {
            if (knockedDown)
                numberOfPinsKnockedDown++;
        }

        if (numberOfPinsKnockedDown == 5)
            return Constants.BALL_VALUE_STRIKE;
        else if (numberOfPinsKnockedDown == 4)
        {
            if (!firstBall[0])
                return Constants.BALL_VALUE_LEFT;
            else if (!firstBall[4])
                return Constants.BALL_VALUE_RIGHT;
        }
        else if (numberOfPinsKnockedDown == 3)
        {
            if (!firstBall[3] && !firstBall[4])
                return Constants.BALL_VALUE_LEFT_CHOP;
            else if (!firstBall[0] && !firstBall[1])
                return Constants.BALL_VALUE_RIGHT_CHOP;
            else if (!firstBall[0] && !firstBall[4])
                return Constants.BALL_VALUE_ACE;
        }
        else if (numberOfPinsKnockedDown == 2)
        {
            if (firstBall[1])
                return Constants.BALL_VALUE_LEFT_SPLIT;
            else if (firstBall[3])
                return Constants.BALL_VALUE_RIGHT_SPLIT;
        }
        else
            return Constants.BALL_VALUE_HEAD_PIN;

        return -2;
    }

    /**
     * Sets the strings in the list mListStatValues
     *
     * @param statValues raw value of stat
     * @param totalShotsAtMiddle total "first ball" opportunities for a game, league or bowler
     * @param spareChances total chances a bowler had to spare a ball
     * @param statOffset position in mListStatValues to start altering
     */
    private void setGeneralAndDetailedStatValues(final int[] statValues, final int totalShotsAtMiddle, final int spareChances, final int statOffset)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[Constants.STAT_MIDDLE_HIT] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_MIDDLE_HIT] / (double)totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_MIDDLE_HIT] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_STRIKES] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_STRIKES] / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_STRIKES] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            mListStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_SPARE_CONVERSIONS] / (double) spareChances * 100)
                            + "% [" + statValues[Constants.STAT_SPARE_CONVERSIONS] + "/" + spareChances + "]");
        }
        currentStatPosition++;

        for (int i = Constants.STAT_HEAD_PINS; i < Constants.STAT_RIGHT_SPLITS_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[i] > 0)
            {
                mListStatValues.set(currentStatPosition,
                        decimalFormat.format(statValues[i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[i] + "/" + totalShotsAtMiddle + "]");
            }
            if (statValues[i + 1] > 0)
            {
                mListStatValues.set(currentStatPosition + 1,
                        decimalFormat.format(statValues[i + 1] / (double)statValues[i] * 100)
                                + "% [" + statValues[i + 1] + "/" + statValues[i] + "]");
            }
        }

        final int statValuesListSize = mListStatValues.size();
        for (int i = Constants.STAT_FOULS; i <= Constants.STAT_NUMBER_OF_GAMES && statValuesListSize > currentStatPosition; i++, currentStatPosition++)
        {
            mListStatValues.set(currentStatPosition, String.valueOf(statValues[i]));
        }
    }

    /**
     * Adds header stat names and placeholder values to certain positions
     * in mListStatNames and mListStatValues
     *
     * @param bowlerLeagueOrGame indicates whether a bowler, league or game's stats are being loaded
     * @param NUMBER_OF_GENERAL_DETAILS number of general details at the start of the lists
     */
    private void setStatHeaders(byte bowlerLeagueOrGame, final byte NUMBER_OF_GENERAL_DETAILS)
    {
        int nextHeaderPosition = 0;
        mListStatNames.add(nextHeaderPosition, "-General");
        mListStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += NUMBER_OF_GENERAL_DETAILS + STATS_MIDDLE_GENERAL.length + 1;
        mListStatNames.add(nextHeaderPosition, "-First Ball");
        mListStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += STATS_MIDDLE_DETAILED.length + 1;
        mListStatNames.add(nextHeaderPosition, "-Fouls");
        mListStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += STATS_FOULS.length + 1;
        mListStatNames.add(nextHeaderPosition, "-Pins Left on Deck");
        mListStatValues.add(nextHeaderPosition, "-");

        if (bowlerLeagueOrGame < LOADING_GAME_STATS)
        {
            nextHeaderPosition += STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length + 1;
            mListStatNames.add(nextHeaderPosition, "-Overall");
            mListStatValues.add(nextHeaderPosition, "-");
        }
    }

    /**
     * Returns a cursor from database to load either bowler or league stats
     *
     * @param shouldGetLeagueStats if true, league stats will be loaded. Bowler stats will be loaded otherwise
     * @return a cursor with rows relevant to mBowlerId or mLeagueId
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
                ? String.valueOf(mLeagueId)
                : String.valueOf(mBowlerId)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a cursor from the database to load game stats
     *
     * @return a cursor with rows relevant to mGameId
     */
    private Cursor getGameCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getReadableDatabase();
        String rawStatsQuery = "SELECT "
                + "game." + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                + FrameEntry.COLUMN_NAME_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_NAME_FRAME_ACCESSED + ", "
                + FrameEntry.COLUMN_NAME_FOULS + ", "
                + FrameEntry.COLUMN_NAME_BALL[0] + ", "
                + FrameEntry.COLUMN_NAME_BALL[1] + ", "
                + FrameEntry.COLUMN_NAME_BALL[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " JOIN " + FrameEntry.TABLE_NAME
                + " ON game." + GameEntry._ID + "=" + FrameEntry.COLUMN_NAME_GAME_ID
                + " WHERE " + FrameEntry.COLUMN_NAME_GAME_ID + "=?"
                + " ORDER BY " + FrameEntry.COLUMN_NAME_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(mGameId)};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Loads the data on a game, league or bowler and adds it to mStatsAdapter
     */
    private class LoadStatsTask extends AsyncTask<Byte, Void, Void>
    {
        @Override
        protected Void doInBackground(Byte... bowlerLeagueOrGameParam)
        {
            final byte bowlerLeagueOrGame = bowlerLeagueOrGameParam[0];
            final byte NUMBER_OF_GENERAL_DETAILS;
            Cursor cursor;
            int[] statValues;
            mListStatNames.add("Bowler");
            mListStatValues.add(mBowlerName);

            //Adds only names to list which are relevant to the data being loaded
            mListStatNames.addAll(Arrays.asList(STATS_MIDDLE_GENERAL));
            mListStatNames.addAll(Arrays.asList(STATS_MIDDLE_DETAILED));
            mListStatNames.addAll(Arrays.asList(STATS_FOULS));
            mListStatNames.addAll(Arrays.asList(STATS_PINS_TOTAL));
            switch(bowlerLeagueOrGame)
            {
                case LOADING_BOWLER_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 1;
                    mListStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    mListStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(false);
                    break;
                case LOADING_LEAGUE_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 2;
                    mListStatNames.add(1, "League");
                    mListStatValues.add(1, mLeagueName);
                    mListStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    mListStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(true);
                    break;
                case LOADING_GAME_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 3;
                    mListStatNames.add(1, "League");
                    mListStatValues.add(1, mLeagueName);
                    mListStatNames.add(2, "Game #");
                    mListStatValues.add(2, String.valueOf(mGameNumber));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length];
                    cursor = getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("bowlerLeagueOrGame must be between 0 and 2 (inclusive");
            }

            int i = mListStatValues.size();
            while (i < mListStatNames.size())
            {
                mListStatValues.add("--");
                i++;
            }

            /*
             * Passes through rows in the database and updates stats which are affected as each
             * frame is analyzed
             */
            int totalShotsAtMiddle = 0;
            int spareChances = 0;
            int seriesTotal = 0;
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_ACCESSED)) == 1);
                    if (bowlerLeagueOrGame == LOADING_GAME_STATS && !frameAccessed)
                        break;

                    byte frameNumber = (byte)cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FRAME_NUMBER));
                    String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_FOULS));
                    String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[0])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[1])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_NAME_BALL[2]))};
                    boolean[][] pinState = new boolean[3][5];

                    for (i = 0; i < 5; i++)
                    {
                        pinState[0][i] = ballStrings[0].charAt(i) == '1';
                        pinState[1][i] = ballStrings[1].charAt(i) == '1';
                        pinState[2][i] = ballStrings[2].charAt(i) == '1';
                    }
                    for (i = 1; i <= 3; i++)
                    {
                        if (frameFouls.contains(String.valueOf(i)))
                            statValues[Constants.STAT_FOULS]++;
                    }

                    if (bowlerLeagueOrGame != LOADING_GAME_STATS || frameAccessed)
                    {
                        if (frameNumber == Constants.NUMBER_OF_FRAMES)
                        {
                            totalShotsAtMiddle++;
                            int ballValue = getFirstBallValue(pinState[0]);
                            if (ballValue != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(ballValue, statValues, 0);
                            if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                                spareChances++;

                            if (ballValue != 0)
                            {
                                if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(ballValue, statValues, 1);

                                    if (ballValue >= 5)
                                        spareChances++;
                                }
                                else
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                            else
                            {
                                totalShotsAtMiddle++;
                                ballValue = getFirstBallValue(pinState[1]);
                                if (ballValue != -1)
                                    statValues[Constants.STAT_MIDDLE_HIT]++;
                                increaseFirstBallStat(ballValue, statValues, 0);

                                if (ballValue != 0)
                                {
                                    if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    {
                                        statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                        increaseFirstBallStat(ballValue, statValues, 1);

                                        if (ballValue >= 5)
                                            spareChances++;
                                    }
                                    else
                                    {
                                        statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                    }
                                }
                                else
                                {
                                    totalShotsAtMiddle++;
                                    ballValue = getFirstBallValue(pinState[2]);
                                    if (ballValue != -1)
                                        statValues[Constants.STAT_MIDDLE_HIT]++;
                                    increaseFirstBallStat(ballValue, statValues, 0);

                                    if (ballValue != 0)
                                    {
                                        statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                    }
                                }
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            int ballValue = getFirstBallValue(pinState[0]);
                            if (ballValue != -1)
                                statValues[Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(ballValue, statValues, 0);

                            if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                                spareChances++;

                            if (ballValue != 0)
                            {
                                if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(ballValue, statValues, 1);

                                    if (ballValue >= 5)
                                        spareChances++;
                                }
                                else
                                {
                                    statValues[Constants.STAT_PINS_LEFT_ON_DECK] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                        }
                    }

                    if (bowlerLeagueOrGame != LOADING_GAME_STATS && frameNumber == 1)
                    {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));
                        byte gameNumber = (byte)cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_NUMBER));
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

            if (bowlerLeagueOrGame != LOADING_GAME_STATS)
            {
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
            }
            setGeneralAndDetailedStatValues(statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS);
            setStatHeaders(bowlerLeagueOrGame, NUMBER_OF_GENERAL_DETAILS);
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mStatsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        mStatsAdapter.updateTheme();
        Theme.validateStatsActivityTheme();
    }
}
