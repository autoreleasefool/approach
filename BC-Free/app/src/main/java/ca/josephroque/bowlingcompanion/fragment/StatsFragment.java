package ca.josephroque.bowlingcompanion.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.DividerItemDecoration;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.StatsAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-24.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class StatsFragment extends Fragment
    implements Theme.ChangeableTheme
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "StatsFragment";

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
    /** Indicates all the stats related to the specified series should be loaded */
    private static final byte LOADING_SERIES_STATS = 2;
    /** Indicates only the stats related to the specified game should be loaded */
    private static final byte LOADING_GAME_STATS = 3;

    /** Adapter to manage data displayed in fragment */
    private StatsAdapter mAdapterStats;

    /** List of names of stats to be displayed */
    private List<String> mListStatNames;
    /** List of values corresponding to mListStatNames */
    private List<String> mListStatValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        mListStatNames = new ArrayList<>();
        mListStatValues = new ArrayList<>();

        RecyclerView mRecyclerViewStats = (RecyclerView)rootView.findViewById(R.id.rv_stats);
        mRecyclerViewStats.setHasFixedSize(true);
        mRecyclerViewStats.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewStats.setLayoutManager(layoutManager);
        mAdapterStats = new StatsAdapter(mListStatNames, mListStatValues);
        mRecyclerViewStats.setAdapter(mAdapterStats);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MainActivity mainActivity = (MainActivity)getActivity();

        mListStatNames.clear();
        mListStatValues.clear();
        mAdapterStats.notifyDataSetChanged();

        //Checks what type of stats should be displayed, depending
        //on what data is available in the parent activity at the time
        byte statsToLoad;
        int titleToSet;
        if (mainActivity.getGameId() == -1)
        {
            if (mainActivity.getSeriesId() == -1)
            {
                if (mainActivity.getLeagueId() == -1)
                {
                    titleToSet = R.string.title_stats_bowler;
                    statsToLoad = LOADING_BOWLER_STATS;
                }
                else
                {
                    titleToSet = R.string.title_stats_league;
                    statsToLoad = LOADING_LEAGUE_STATS;
                }
            }
            else
            {
                titleToSet = R.string.title_stats_series;
                statsToLoad = LOADING_SERIES_STATS;
            }
        }
        else
        {
            titleToSet = R.string.title_stats_game;
            statsToLoad = LOADING_GAME_STATS;
        }

        updateTheme();

        mainActivity.setActionBarTitle(titleToSet);
        new LoadStatsTask().execute(statsToLoad);
    }

    @Override
    public void updateTheme()
    {
        mAdapterStats.updateTheme();
    }

    private class LoadStatsTask extends AsyncTask<Byte, Void, List<?>[]>
    {
        @Override
        protected List<?>[] doInBackground(Byte... bowlerLeagueOrGameParam)
        {
            MainActivity.waitForSaveThreads((MainActivity)getActivity(), TAG);

            MainActivity mainActivity = (MainActivity)getActivity();
            final byte bowlerLeagueOrGame = bowlerLeagueOrGameParam[0];
            final byte NUMBER_OF_GENERAL_DETAILS;
            Cursor cursor;
            int[] statValues;
            List<String> listStatNames = new ArrayList<>();
            List<String> listStatValues = new ArrayList<>();

            listStatNames.add("Bowler");
            listStatValues.add(mainActivity.getBowlerName());

            //Adds only names to list which are relevant to the data being loaded
            listStatNames.addAll(Arrays.asList(STATS_MIDDLE_GENERAL));
            listStatNames.addAll(Arrays.asList(STATS_MIDDLE_DETAILED));
            listStatNames.addAll(Arrays.asList(STATS_FOULS));
            listStatNames.addAll(Arrays.asList(STATS_PINS_TOTAL));
            switch(bowlerLeagueOrGame)
            {
                case LOADING_BOWLER_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 1;
                    listStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    listStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(false);
                    break;
                case LOADING_LEAGUE_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 2;
                    listStatNames.add(1, "League/Event");
                    listStatValues.add(1, mainActivity.getLeagueName());
                    listStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    listStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getBowlerOrLeagueCursor(true);
                    break;
                case LOADING_SERIES_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 3;
                    listStatNames.add(1, "League/Event");
                    listStatValues.add(1, mainActivity.getLeagueName());
                    listStatNames.add(2, "Date");
                    listStatValues.add(2, mainActivity.getSeriesDate());
                    listStatNames.addAll(Arrays.asList(STATS_PINS_AVERAGE));
                    listStatNames.addAll(Arrays.asList(STATS_GENERAL));
                    listStatNames.set(NUMBER_OF_GENERAL_DETAILS + Constants.STAT_HIGH_SERIES, "Series Total");
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length
                            + STATS_GENERAL.length];
                    cursor = getSeriesCursor();
                    break;
                case LOADING_GAME_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 4;
                    listStatNames.add(1, "League/Event");
                    listStatValues.add(1, mainActivity.getLeagueName());
                    listStatNames.add(2, "Date");
                    listStatValues.add(2, mainActivity.getSeriesDate());
                    listStatNames.add(3, "Game #");
                    listStatValues.add(3, String.valueOf(mainActivity.getGameNumber()));
                    statValues = new int[STATS_MIDDLE_GENERAL.length + STATS_MIDDLE_DETAILED.length
                            + STATS_FOULS.length + STATS_PINS_TOTAL.length];
                    cursor = getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("bowlerLeagueOrGame must be between 0 and 3 (inclusive");
            }

            int i = listStatValues.size();
            while (i < listStatNames.size())
            {
                listStatValues.add("--");
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
                    boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED)) == 1);
                    if (bowlerLeagueOrGame == LOADING_GAME_STATS && !frameAccessed)
                        break;

                    byte frameNumber = (byte)cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER));
                    String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_FOULS));
                    String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[0])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[1])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[2]))};
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
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                        byte gameNumber = (byte)cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER));
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
            setGeneralAndDetailedStatValues(listStatValues, statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS);
            setStatHeaders(listStatNames, listStatValues, bowlerLeagueOrGame, NUMBER_OF_GENERAL_DETAILS);
            cursor.close();

            return new List<?>[]{listStatNames, listStatValues};
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(List<?>[] lists)
        {
            mListStatNames.addAll((List<String>)lists[0]);
            mListStatValues.addAll((List<String>)lists[1]);
            mAdapterStats.notifyDataSetChanged();
        }
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
    private void setGeneralAndDetailedStatValues(List<String> listStatValues, final int[] statValues, final int totalShotsAtMiddle, final int spareChances, final int statOffset)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[Constants.STAT_MIDDLE_HIT] > 0)
        {
            listStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_MIDDLE_HIT] / (double)totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_MIDDLE_HIT] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_STRIKES] > 0)
        {
            listStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_STRIKES] / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[Constants.STAT_STRIKES] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            listStatValues.set(currentStatPosition,
                    decimalFormat.format(statValues[Constants.STAT_SPARE_CONVERSIONS] / (double) spareChances * 100)
                            + "% [" + statValues[Constants.STAT_SPARE_CONVERSIONS] + "/" + spareChances + "]");
        }
        currentStatPosition++;

        for (int i = Constants.STAT_HEAD_PINS; i < Constants.STAT_RIGHT_SPLITS_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[i] > 0)
            {
                listStatValues.set(currentStatPosition,
                        decimalFormat.format(statValues[i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[i] + "/" + totalShotsAtMiddle + "]");
            }
            if (statValues[i + 1] > 0)
            {
                listStatValues.set(currentStatPosition + 1,
                        decimalFormat.format(statValues[i + 1] / (double)statValues[i] * 100)
                                + "% [" + statValues[i + 1] + "/" + statValues[i] + "]");
            }
        }

        final int statValuesListSize = listStatValues.size();
        for (int i = Constants.STAT_FOULS; i <= Constants.STAT_NUMBER_OF_GAMES && statValuesListSize > currentStatPosition; i++, currentStatPosition++)
        {
            listStatValues.set(currentStatPosition, String.valueOf(statValues[i]));
        }
    }

    /**
     * Adds header stat names and placeholder values to certain positions
     * in mListStatNames and mListStatValues
     *
     * @param bowlerLeagueOrGame indicates whether a bowler, league or game's stats are being loaded
     * @param NUMBER_OF_GENERAL_DETAILS number of general details at the start of the lists
     */
    private void setStatHeaders(List<String> listStatNames, List<String> listStatValues, byte bowlerLeagueOrGame, final byte NUMBER_OF_GENERAL_DETAILS)
    {
        int nextHeaderPosition = 0;
        listStatNames.add(nextHeaderPosition, "-General");
        listStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += NUMBER_OF_GENERAL_DETAILS + STATS_MIDDLE_GENERAL.length + 1;
        listStatNames.add(nextHeaderPosition, "-First Ball");
        listStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += STATS_MIDDLE_DETAILED.length + 1;
        listStatNames.add(nextHeaderPosition, "-Fouls");
        listStatValues.add(nextHeaderPosition, "-");
        nextHeaderPosition += STATS_FOULS.length + 1;
        listStatNames.add(nextHeaderPosition, "-Pins Left on Deck");
        listStatValues.add(nextHeaderPosition, "-");

        if (bowlerLeagueOrGame < LOADING_GAME_STATS)
        {
            nextHeaderPosition += STATS_PINS_TOTAL.length + STATS_PINS_AVERAGE.length + 1;
            listStatNames.add(nextHeaderPosition, "-Overall");
            listStatValues.add(nextHeaderPosition, "-");
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isEventIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_EVENTS, true);
        boolean isOpenIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_OPEN, true);
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_SCORE + ", "
                + GameEntry.COLUMN_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                + " LEFT JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry._ID
                + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                + " LEFT JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=frame." + FrameEntry.COLUMN_GAME_ID
                + ((shouldGetLeagueStats)
                        ? " WHERE league." + LeagueEntry._ID + "=?"
                        : " WHERE league." + LeagueEntry.COLUMN_BOWLER_ID + "=?")
                + ((isEventIncluded)
                        ? " AND 0=?"
                        : " AND league." + LeagueEntry.COLUMN_IS_EVENT + "=?")
                + ((isOpenIncluded)
                        ? " AND 'Open'=?"
                        : " AND league." + LeagueEntry.COLUMN_LEAGUE_NAME + "!=?")
                + " ORDER BY league." + LeagueEntry._ID
                        + ", series." + SeriesEntry._ID
                        + ", game." + GameEntry.COLUMN_GAME_NUMBER
                        + ", frame." + FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {
                ((shouldGetLeagueStats)
                    ? String.valueOf(((MainActivity)getActivity()).getLeagueId())
                    : String.valueOf(((MainActivity)getActivity()).getBowlerId())),
                String.valueOf(0),
                Constants.NAME_OPEN_LEAGUE};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a cursor from database to load series stats
     *
     * @return a cursor with rows relevant to mSeriesId
     */
    private Cursor getSeriesCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_SCORE + ", "
                + GameEntry.COLUMN_GAME_NUMBER + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " INNER JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=frame." + FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + GameEntry.COLUMN_SERIES_ID + "=?"
                + " ORDER BY game." + GameEntry.COLUMN_GAME_NUMBER + ", frame." + FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(((MainActivity)getActivity()).getSeriesId())};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a cursor from the database to load game stats
     *
     * @return a cursor with rows relevant to mGameId
     */
    private Cursor getGameCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
        String rawStatsQuery = "SELECT "
                + GameEntry.COLUMN_SCORE + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + GameEntry.TABLE_NAME + " AS game"
                + " INNER JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=frame." + FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + GameEntry._ID + "=?"
                + " ORDER BY " + FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(((MainActivity)getActivity()).getGameId())};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Creates a new instance of StatsFragment and returns it
     * @return new instance of StatsFragment
     */
    public static StatsFragment newInstance()
    {
        return new StatsFragment();
    }
}
