package ca.josephroque.bowlingcompanion.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.StatsExpandableAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-04-03.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class StatsFragment extends Fragment
    implements Theme.ChangeableTheme
{
    /** Indicates all the stats related to the specified bowler should be loaded */
    private static final byte LOADING_BOWLER_STATS = 0;
    /** Indicates all the stats related to the specified league should be loaded */
    private static final byte LOADING_LEAGUE_STATS = 1;
    /** Indicates all the stats related to the specified series should be loaded */
    private static final byte LOADING_SERIES_STATS = 2;
    /** Indicates only the stats related to the specified game should be loaded */
    private static final byte LOADING_GAME_STATS = 3;

    private byte STATS_GENERAL = -1;
    private byte STATS_FIRST_BALL = -1;
    private byte STATS_FOULS = -1;
    private byte STATS_PINS = -1;
    private byte STATS_GAME_AVERAGE = -1;
    private byte STATS_MATCH = -1;
    private byte STATS_OVERALL = -1;

    /** Adapter to manage data displayed in fragment */
    private StatsExpandableAdapter mAdapterStats;

    private List<String> mListStatHeaders;
    private List<List<AbstractMap.SimpleEntry<String, String>>> mListStatNamesAndValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        mListStatHeaders = new ArrayList<>();
        mListStatNamesAndValues = new ArrayList<>();

        mAdapterStats = new StatsExpandableAdapter(getActivity(), mListStatHeaders, mListStatNamesAndValues);

        ExpandableListView listView = (ExpandableListView)rootView.findViewById(R.id.elv_stats);
        listView.setAdapter(mAdapterStats);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MainActivity mainActivity = (MainActivity)getActivity();

        mListStatHeaders.clear();
        mListStatNamesAndValues.clear();
        mAdapterStats.notifyDataSetChanged();

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

        mainActivity.setActionBarTitle(titleToSet, true);
        new LoadStatsTask().execute(statsToLoad);
    }

    private void prepareListData(MainActivity mainActivity, byte statsToLoad, List<String> headers, List<List<AbstractMap.SimpleEntry<String, String>>> namesAndValues)
    {
        final String[] GENERAL = {"Middle Hit", "Strikes", "Spare Conversions"};
        final String[] FIRST_BALL =
                {"Head Pins", "Head Pins Spared",
                        "Lefts", "Lefts Spared", "Rights", "Rights Spared",
                        "Aces", "Aces Spared",
                        "Chop Offs", "Chop Offs Spared", "Left Chop Offs", "Left Chop Offs Spared", "Right Chop Offs", "Right Chop Offs Spared",
                        "Splits", "Splits Spared", "Left Splits", "Left Splits Spared", "Right Splits", "Right Splits Spared"};
        final String[] FOULS = {"Fouls"};
        final String[] PINS_TOTAL = {"Total Pins Left"};
        final String[] PINS_AVERAGE = {"Average Pins Left"};
        final String[] MATCH = {"Games Won", "Games Lost", "Games Tied"};
        final String[] OVERALL = {"Average", "High Single", "High Series", "Total Pinfall", "# of Games"};

        headers.add("General");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        STATS_GENERAL = 0;
        namesAndValues.get(STATS_GENERAL).add(new AbstractMap.SimpleEntry<>("Bowler", mainActivity.getBowlerName()));
        for (String stat : GENERAL)
            namesAndValues.get(STATS_GENERAL).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("First Ball");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        STATS_FIRST_BALL = 1;
        for (String stat : FIRST_BALL)
            namesAndValues.get(STATS_FIRST_BALL).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("Fouls");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        STATS_FOULS = 2;
        for (String stat : FOULS)
            namesAndValues.get(STATS_FOULS).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("Pins Left on Deck");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        STATS_PINS = 3;
        for (String stat : PINS_TOTAL)
            namesAndValues.get(STATS_PINS).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        if (statsToLoad < LOADING_SERIES_STATS)
        {
            headers.add("Average by Game");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            STATS_GAME_AVERAGE = 4;
            final byte numberOfGames = (statsToLoad >= LOADING_LEAGUE_STATS ? ((MainActivity)getActivity()).getNumberOfGames() : 20);
            for (byte i = 0; i < numberOfGames; i++)
                namesAndValues.get(STATS_GAME_AVERAGE).add(new AbstractMap.SimpleEntry<>("Average in Game " + (i + 1), "--"));
        }


        if (statsToLoad < LOADING_GAME_STATS)
        {
            for (String stat : PINS_AVERAGE)
                namesAndValues.get(STATS_PINS).add(new AbstractMap.SimpleEntry<>(stat, "--"));

            headers.add("Match Play");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            STATS_MATCH = (byte)(STATS_GAME_AVERAGE == -1 ? 4 : 5);
            for (String stat : MATCH)
                namesAndValues.get(STATS_MATCH).add(new AbstractMap.SimpleEntry<>(stat, "--"));

            headers.add("Overall");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            STATS_OVERALL = (byte)(STATS_MATCH + 1);
            for (String stat : OVERALL)
                namesAndValues.get(STATS_OVERALL).add(new AbstractMap.SimpleEntry<>(stat, "--"));
        }
    }

    @Override
    public void updateTheme() {mAdapterStats.updateTheme();}

    private class LoadStatsTask extends AsyncTask<Byte, Void, List<?>[]>
    {
        @Override
        protected List<?>[] doInBackground(Byte... statsToLoad)
        {
            MainActivity mainActivity = (MainActivity)getActivity();
            MainActivity.waitForSaveThreads(mainActivity);

            final byte toLoad = statsToLoad[0];
            final byte NUMBER_OF_GENERAL_DETAILS;
            Cursor cursor;
            int[][] statValues;
            List<String> listStatHeaders = new ArrayList<>();
            List<List<AbstractMap.SimpleEntry<String, String>>> listStatNamesAndValues = new ArrayList<>();

            prepareListData(mainActivity, toLoad, listStatHeaders, listStatNamesAndValues);
            statValues = new int[listStatHeaders.size()][];
            for (int i = 0; i < statValues.length; i++)
                statValues[i] = new int[listStatNamesAndValues.get(i).size()];

            switch(toLoad)
            {
                case LOADING_BOWLER_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 1;
                    cursor = getBowlerOrLeagueCursor(false);
                    break;
                case LOADING_LEAGUE_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 2;
                    listStatNamesAndValues.get(STATS_GENERAL).add(1, new AbstractMap.SimpleEntry<>("League/Event", mainActivity.getLeagueName()));
                    cursor = getBowlerOrLeagueCursor(true);
                    break;
                case LOADING_SERIES_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 3;
                    listStatNamesAndValues.get(STATS_GENERAL).add(1, new AbstractMap.SimpleEntry<>("League/Event", mainActivity.getLeagueName()));
                    listStatNamesAndValues.get(STATS_GENERAL).add(2, new AbstractMap.SimpleEntry<>("Date", mainActivity.getSeriesDate()));
                    cursor = getSeriesCursor();
                    break;
                case LOADING_GAME_STATS:
                    NUMBER_OF_GENERAL_DETAILS = 4;
                    listStatNamesAndValues.get(STATS_GENERAL).add(1, new AbstractMap.SimpleEntry<>("League/Event", mainActivity.getLeagueName()));
                    listStatNamesAndValues.get(STATS_GENERAL).add(2, new AbstractMap.SimpleEntry<>("Date", mainActivity.getSeriesDate()));
                    listStatNamesAndValues.get(STATS_GENERAL).add(3, new AbstractMap.SimpleEntry<>("Game #", String.valueOf(mainActivity.getGameNumber())));
                    cursor = getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("invalid value for toLoad: " + toLoad + ". must be between 0 and 3 (inclusive)");
            }

            /**
             * Passes through rows in cursor and updates stats which
             * are affected as each frame is analyzed
             */

            final byte numberOfGames = (toLoad >= LOADING_LEAGUE_STATS ? mainActivity.getNumberOfGames() : 20);
            int totalShotsAtMiddle = 0;
            int spareChances = 0;
            int seriesTotal = 0;
            int[] totalByGame = new int[numberOfGames];
            int[] countByGame = new int[numberOfGames];
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    byte frameNumber = (byte)cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER));
                    if (toLoad != LOADING_GAME_STATS && frameNumber == 1)
                    {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                        byte gameNumber = (byte)cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER));

                        totalByGame[gameNumber - 1] += gameScore;
                        countByGame[gameNumber - 1]++;

                        byte matchResults = (byte) (cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY)));
                        if (matchResults > 0)
                            statValues[STATS_MATCH][matchResults - 1]++;

                        if (statValues[STATS_OVERALL][Constants.STAT_HIGH_SINGLE] < gameScore)
                        {
                            statValues[STATS_OVERALL][Constants.STAT_HIGH_SINGLE] = gameScore;
                        }
                        statValues[STATS_OVERALL][Constants.STAT_TOTAL_PINS] += gameScore;
                        statValues[STATS_OVERALL][Constants.STAT_NUMBER_OF_GAMES]++;

                        if (gameNumber == 1)
                        {
                            if (statValues[STATS_OVERALL][Constants.STAT_HIGH_SERIES] < seriesTotal)
                                statValues[STATS_OVERALL][Constants.STAT_HIGH_SERIES] = seriesTotal;
                            seriesTotal = gameScore;
                        }
                        else
                            seriesTotal += gameScore;
                    }

                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual)
                    {
                        cursor.moveToNext();
                        continue;
                    }
                    boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED)) == 1);
                    if (toLoad == LOADING_GAME_STATS && !frameAccessed)
                        break;

                    String frameFouls = cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_FOULS));
                    String[] ballStrings = {cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[0])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[1])),
                            cursor.getString(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[2]))};
                    boolean[][] pinState = new boolean[3][5];

                    for (byte i = 0; i < 5; i++)
                    {
                        pinState[0][i] = ballStrings[0].charAt(i) == '1';
                        pinState[1][i] = ballStrings[1].charAt(i) == '1';
                        pinState[2][i] = ballStrings[2].charAt(i) == '1';
                    }
                    for (byte i = 1; i <= 3; i++)
                    {
                        if (frameFouls.contains(String.valueOf(i)))
                            statValues[STATS_FOULS][0]++;
                    }

                    if (frameNumber == Constants.NUMBER_OF_FRAMES)
                    {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(ballValue, statValues, 0);
                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0)
                        {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                            {
                                statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            }
                            else
                            {
                                statValues[STATS_PINS][Constants.STAT_PINS_LEFT] += countPinsLeftStanding(pinState[2]);
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            ballValue = getFirstBallValue(pinState[1]);
                            if (ballValue != -1)
                                statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(ballValue, statValues, 0);

                            if (ballValue != 0)
                            {
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(ballValue, statValues, 1);

                                    if (ballValue >= 5)
                                        spareChances++;
                                }
                                else
                                {
                                    statValues[STATS_PINS][Constants.STAT_PINS_LEFT] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                            else
                            {
                                totalShotsAtMiddle++;
                                ballValue = getFirstBallValue(pinState[2]);
                                if (ballValue != -1)
                                    statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT]++;
                                increaseFirstBallStat(ballValue, statValues, 0);

                                if (ballValue != 0)
                                {
                                    statValues[STATS_PINS][Constants.STAT_PINS_LEFT] += countPinsLeftStanding(pinState[2]);
                                }
                            }
                        }
                    }
                    else
                    {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(ballValue, statValues, 0);

                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0)
                        {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                            {
                                statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            }
                            else
                            {
                                statValues[STATS_PINS][Constants.STAT_PINS_LEFT] += countPinsLeftStanding(pinState[2]);
                            }
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (toLoad != LOADING_GAME_STATS)
            {
                if (statValues[STATS_OVERALL][Constants.STAT_HIGH_SERIES] < seriesTotal)
                    statValues[STATS_OVERALL][Constants.STAT_HIGH_SERIES] = seriesTotal;

                if (toLoad != LOADING_SERIES_STATS)
                {
                    for (byte i = 0; i < numberOfGames; i++)
                        statValues[STATS_GAME_AVERAGE][i] = (countByGame[i] > 0) ? totalByGame[i] / countByGame[i] : 0;
                }

                if (statValues[STATS_OVERALL][Constants.STAT_NUMBER_OF_GAMES] > 0)
                {
                    statValues[STATS_OVERALL][Constants.STAT_AVERAGE] =
                            statValues[STATS_OVERALL][Constants.STAT_TOTAL_PINS] / statValues[STATS_OVERALL][Constants.STAT_NUMBER_OF_GAMES];
                    statValues[STATS_PINS][Constants.STAT_PINS_AVERAGE] =
                            statValues[STATS_PINS][Constants.STAT_PINS_LEFT] / statValues[STATS_OVERALL][Constants.STAT_NUMBER_OF_GAMES];
                }
            }
            cursor.close();
            setGeneralAndDetailedStatValues(listStatNamesAndValues, statValues, totalShotsAtMiddle, spareChances, NUMBER_OF_GENERAL_DETAILS, toLoad);

            return new List<?>[]{listStatHeaders, listStatNamesAndValues};
        }

        @SuppressWarnings("unchecked") //Types of parameters are known
        @Override
        protected void onPostExecute(List<?>[] lists)
        {
            mListStatHeaders.addAll((List<String>)lists[0]);
            mListStatNamesAndValues.addAll((List<List<AbstractMap.SimpleEntry<String, String>>>)lists[1]);
            mAdapterStats.notifyDataSetChanged();
        }
    }

    /**
     * Sets the strings in the list mListStatValues
     *
     * @param statValues raw value of stat
     * @param totalShotsAtMiddle total "first ball" opportunities for a game, league or bowler
     * @param spareChances total chances a bowler had to spare a ball
     * @param statOffset position in mListStatValues to start altering
     */
    private void setGeneralAndDetailedStatValues(List<List<AbstractMap.SimpleEntry<String, String>>> listStatNamesAndValues, int[][] statValues, int totalShotsAtMiddle, int spareChances, int statOffset, byte toLoad)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT] > 0)
        {
            listStatNamesAndValues.get(STATS_GENERAL).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT] / (double)totalShotsAtMiddle * 100)
                            + "% [" + statValues[STATS_GENERAL][Constants.STAT_MIDDLE_HIT] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[STATS_GENERAL][Constants.STAT_STRIKES] > 0)
        {
            listStatNamesAndValues.get(STATS_GENERAL).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[STATS_GENERAL][Constants.STAT_STRIKES] / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[STATS_GENERAL][Constants.STAT_STRIKES] + "/" + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            listStatNamesAndValues.get(STATS_GENERAL).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS] / (double) spareChances * 100)
                            + "% [" + statValues[STATS_GENERAL][Constants.STAT_SPARE_CONVERSIONS] + "/" + spareChances + "]");
        }

        currentStatPosition = 0;
        for (int i = 0; i < Constants.STAT_RIGHT_SPLIT_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[STATS_FIRST_BALL][i] > 0)
            {
                listStatNamesAndValues.get(STATS_FIRST_BALL).get(currentStatPosition).setValue(
                        decimalFormat.format(statValues[STATS_FIRST_BALL][i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[STATS_FIRST_BALL][i] + "/" + totalShotsAtMiddle + "]");
            }
            if (statValues[STATS_FIRST_BALL][i + 1] > 0)
            {
                listStatNamesAndValues.get(STATS_FIRST_BALL).get(currentStatPosition + 1).setValue(
                        decimalFormat.format(statValues[STATS_FIRST_BALL][i + 1] / (double)statValues[STATS_FIRST_BALL][i] * 100)
                                + "% [" + statValues[STATS_FIRST_BALL][i + 1] + "/" + statValues[STATS_FIRST_BALL][i] + "]");
            }
        }

        listStatNamesAndValues.get(STATS_FOULS).get(0).setValue(String.valueOf(statValues[STATS_FOULS][0]));
        listStatNamesAndValues.get(STATS_PINS).get(0).setValue(String.valueOf(statValues[STATS_PINS][0]));

        if (toLoad < LOADING_GAME_STATS)
        {
            if (toLoad != LOADING_SERIES_STATS)
            {
                for (byte i = 0; i < statValues[STATS_GAME_AVERAGE].length; i++)
                    listStatNamesAndValues.get(STATS_GAME_AVERAGE).get(i).setValue(String.valueOf(statValues[STATS_GAME_AVERAGE][i]));
            }

            listStatNamesAndValues.get(STATS_PINS).get(1).setValue(String.valueOf(statValues[STATS_PINS][1]));

            int totalMatchPlayGames = 0;
            for (int stat : statValues[STATS_MATCH])
                totalMatchPlayGames += stat;
            for (byte i = 0; i < statValues[STATS_MATCH].length; i++)
                listStatNamesAndValues.get(STATS_MATCH).get(i).setValue(
                        decimalFormat.format(statValues[STATS_MATCH][i] / (double)totalMatchPlayGames * 100)
                                + "% [" + statValues[STATS_MATCH][i] + "/" + totalMatchPlayGames + "]");

            for (byte i = 0; i < statValues[STATS_OVERALL].length; i++)
                listStatNamesAndValues.get(STATS_OVERALL).get(i).setValue(
                        String.valueOf(statValues[STATS_OVERALL][i]));
        }

        /**final int statValuesListSize = listStatValues.size();
        for (int i = Constants.STAT_FOULS; i <= Constants.STAT_NUMBER_OF_GAMES && statValuesListSize > currentStatPosition; i++, currentStatPosition++)
        {
            listStatValues.set(currentStatPosition, String.valueOf(statValues[i]));
        }*/
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
     * Checks which situation has occurred by the state of the pins in ball
     *
     * @param ball result of the pins after a ball was thrown
     * @param statValues stat values to update
     * @param offset indicates a spare was thrown and the spare count should be increased for a stat
     */
    private void increaseFirstBallStat(int ball, int[][] statValues, int offset)
    {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch(ball)
        {
            case Constants.BALL_VALUE_STRIKE:
                if (offset == 0)
                {
                    statValues[STATS_GENERAL][Constants.STAT_STRIKES]++;
                }
                break;
            case Constants.BALL_VALUE_LEFT:statValues[STATS_FIRST_BALL][Constants.STAT_LEFT + offset]++; break;
            case Constants.BALL_VALUE_RIGHT:statValues[STATS_FIRST_BALL][Constants.STAT_RIGHT + offset]++; break;
            case Constants.BALL_VALUE_LEFT_CHOP:
                statValues[STATS_FIRST_BALL][Constants.STAT_LEFT_CHOP + offset]++;
                statValues[STATS_FIRST_BALL][Constants.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_CHOP:
                statValues[STATS_FIRST_BALL][Constants.STAT_RIGHT_CHOP + offset]++;
                statValues[STATS_FIRST_BALL][Constants.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_ACE:statValues[STATS_FIRST_BALL][Constants.STAT_ACES + offset]++; break;
            case Constants.BALL_VALUE_LEFT_SPLIT:
                statValues[STATS_FIRST_BALL][Constants.STAT_LEFT_SPLIT + offset]++;
                statValues[STATS_FIRST_BALL][Constants.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_SPLIT:
                statValues[STATS_FIRST_BALL][Constants.STAT_RIGHT_SPLIT + offset]++;
                statValues[STATS_FIRST_BALL][Constants.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_HEAD_PIN:statValues[STATS_FIRST_BALL][Constants.STAT_HEAD_PINS + offset]++;
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
                + GameEntry.COLUMN_IS_MANUAL + ", "
                + GameEntry.COLUMN_MATCH_PLAY + ", "
                + FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + FrameEntry.COLUMN_IS_ACCESSED + ", "
                + FrameEntry.COLUMN_FOULS + ", "
                + FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                + " INNER JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry.COLUMN_LEAGUE_ID
                + " INNER JOIN " + GameEntry.TABLE_NAME + " AS game"
                + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                + " INNER JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=frame." + FrameEntry.COLUMN_GAME_ID
                + ((shouldGetLeagueStats)
                        ? " WHERE league." + LeagueEntry._ID + "=?"
                        : " WHERE league." + LeagueEntry.COLUMN_BOWLER_ID + "=?")
                + " AND " + ((!shouldGetLeagueStats && !isEventIncluded)
                        ? LeagueEntry.COLUMN_IS_EVENT : "'0'") + "=?"
                + " AND " + ((!shouldGetLeagueStats && !isOpenIncluded)
                        ? LeagueEntry.COLUMN_LEAGUE_NAME + "!" : "'0'") + "=?"
                + " ORDER BY league." + LeagueEntry._ID
                + ", series." + SeriesEntry._ID
                + ", game." + GameEntry.COLUMN_GAME_NUMBER
                + ", frame." + FrameEntry.COLUMN_FRAME_NUMBER;

        String[] rawStatsArgs = {
                ((shouldGetLeagueStats)
                        ? String.valueOf(((MainActivity) getActivity()).getLeagueId())
                        : String.valueOf(((MainActivity) getActivity()).getBowlerId())),
                String.valueOf(0),
                ((!shouldGetLeagueStats && !isOpenIncluded) ? Constants.NAME_OPEN_LEAGUE : String.valueOf(0))};

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
                + GameEntry.COLUMN_IS_MANUAL + ", "
                + GameEntry.COLUMN_MATCH_PLAY + ", "
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
                + GameEntry.COLUMN_IS_MANUAL + ", "
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
