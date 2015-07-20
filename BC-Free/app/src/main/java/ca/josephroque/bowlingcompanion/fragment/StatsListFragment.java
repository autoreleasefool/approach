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
import ca.josephroque.bowlingcompanion.database.Contract;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsListFragment
        extends Fragment
        implements Theme.ChangeableTheme
{

    /** Adapter to manage data displayed in fragment. */
    private StatsExpandableAdapter mAdapterStats;

    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsGeneral = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsFirstBall = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsFouls = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsPins = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsGameAverage = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsMatch = -1;
    /** Indicates index of stat group in array, if the group exists at all. */
    private byte mStatsOverall = -1;

    /** List of group headers. */
    private List<String> mListStatHeaders;
    /** List of list of map entries which hold a name and a value, for each group. */
    private List<List<AbstractMap.SimpleEntry<String, String>>> mListStatNamesAndValues;

    /**
     * Creates a new instance of {@code StatsListFragment} with the parameters provided.
     *
     * @return a new instance of StatsListFragment
     */
    public static StatsListFragment newInstance()
    {
        return new StatsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_list, container, false);

        mListStatHeaders = new ArrayList<>();
        mListStatNamesAndValues = new ArrayList<>();

        mAdapterStats = new StatsExpandableAdapter(getActivity(), mListStatHeaders,
                mListStatNamesAndValues);

        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.elv_stats);
        listView.setAdapter(mAdapterStats);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getActivity() != null)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setFloatingActionButtonIcon(0);
            mainActivity.setCurrentFragment(this);
            mainActivity.setDrawerState(false);

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
                        statsToLoad = StatsFragment.LOADING_BOWLER_STATS;
                    }
                    else
                    {
                        titleToSet = R.string.title_stats_league;
                        statsToLoad = StatsFragment.LOADING_LEAGUE_STATS;
                    }
                }
                else
                {
                    titleToSet = R.string.title_stats_series;
                    statsToLoad = StatsFragment.LOADING_SERIES_STATS;
                }
            }
            else
            {
                titleToSet = R.string.title_stats_game;
                statsToLoad = StatsFragment.LOADING_GAME_STATS;
            }

            mListStatHeaders.clear();
            mListStatNamesAndValues.clear();
            mAdapterStats.notifyDataSetChanged();

            mainActivity.setActionBarTitle(titleToSet, true);
            new LoadStatsTask().execute(statsToLoad);
        }

        updateTheme();
    }

    @Override
    public void updateTheme()
    {
        mAdapterStats.updateTheme();
    }

    /**
     * Adds headers and filler data to lists.
     *
     * @param mainActivity activity which created this object
     * @param statsToLoad type of stats which are being loaded
     * @param headers headers of groups
     * @param namesAndValues entries in each group
     */
    @SuppressWarnings("Convert2Diamond")
    private void prepareListData(MainActivity mainActivity,
                                 byte statsToLoad,
                                 List<String> headers,
                                 List<List<AbstractMap.SimpleEntry<String,
                                         String>>> namesAndValues)
    {
        //Stat names which could possibly be displayed, depending on stats being loaded
        final String[] statNamesGeneral = {"Middle Hit", "Strikes", "Spare Conversions"};
        final String[] statNamesFirstBall = {
                "Head Pins", "Head Pins Spared", "Lefts",
                "Lefts Spared", "Rights", "Rights Spared", "Aces", "Aces Spared", "Chop Offs",
                "Chop Offs Spared", "Left Chop Offs", "Left Chop Offs Spared", "Right Chop Offs",
                "Right Chop Offs Spared", "Splits", "Splits Spared", "Left Splits",
                "Left Splits Spared", "Right Splits", "Right Splits Spared"};
        final String[] statNamesFoul = {"Fouls"};
        final String[] statNamesTotalPins = {"Total Pins Left"};
        final String[] statNamesAveragePins = {"Average Pins Left"};
        final String[] statNamesMatch = {"Games Won", "Games Lost", "Games Tied"};
        final String[] statNamesOverall = {
                "Average", "High Single", "High Series", "Total Pinfall",
                "# of Games"};

        headers.add("General");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        mStatsGeneral = 0;
        namesAndValues.get(mStatsGeneral).add(
                new AbstractMap.SimpleEntry<>("Bowler", mainActivity.getBowlerName()));
        for (String stat : statNamesGeneral)
            namesAndValues.get(mStatsGeneral).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("First Ball");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        mStatsFirstBall = 1;
        for (String stat : statNamesFirstBall)
            namesAndValues.get(mStatsFirstBall).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("Fouls");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        mStatsFouls = 2;
        for (String stat : statNamesFoul)
            namesAndValues.get(mStatsFouls).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        headers.add("Pins Left on Deck");
        namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
        mStatsPins = 3;
        for (String stat : statNamesTotalPins)
            namesAndValues.get(mStatsPins).add(new AbstractMap.SimpleEntry<>(stat, "--"));

        if (statsToLoad < StatsFragment.LOADING_SERIES_STATS)
        {
            headers.add("Average by Game");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            mStatsGameAverage = 4;
            final byte numberOfGames = (statsToLoad >= StatsFragment.LOADING_LEAGUE_STATS
                    ? ((MainActivity) getActivity()).getNumberOfGames()
                    : 20);
            for (byte i = 0; i < numberOfGames; i++)
                namesAndValues.get(mStatsGameAverage).add(
                        new AbstractMap.SimpleEntry<>("Average in Game " + (i + 1), "--"));
        }


        if (statsToLoad < StatsFragment.LOADING_GAME_STATS)
        {
            for (String stat : statNamesAveragePins)
                namesAndValues.get(mStatsPins).add(new AbstractMap.SimpleEntry<>(stat, "--"));

            headers.add("Match Play");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            mStatsMatch = (byte) (mStatsGameAverage == -1
                    ? 4
                    : 5);
            for (String stat : statNamesMatch)
                namesAndValues.get(mStatsMatch).add(new AbstractMap.SimpleEntry<>(stat, "--"));

            headers.add("Overall");
            namesAndValues.add(new ArrayList<AbstractMap.SimpleEntry<String, String>>());
            mStatsOverall = (byte) (mStatsMatch + 1);
            for (String stat : statNamesOverall)
                namesAndValues.get(mStatsOverall).add(new AbstractMap.SimpleEntry<>(stat, "--"));
        }
    }

    /**
     * Loads data from the database and calculates relevant stats depending on which type of stats
     * are being loaded.
     */
    private class LoadStatsTask
            extends AsyncTask<Byte, Void, List<?>[]>
    {

        @Override
        protected List<?>[] doInBackground(Byte... statsToLoad)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            MainActivity.waitForSaveThreads(mainActivity);

            final byte toLoad = statsToLoad[0];
            final byte numberOfGeneralDetails;
            Cursor cursor;
            int[][] statValues;
            List<String> listStatHeaders = new ArrayList<>();
            List<List<AbstractMap.SimpleEntry<String, String>>> listStatNamesAndValues =
                    new ArrayList<>();

            prepareListData(mainActivity, toLoad, listStatHeaders, listStatNamesAndValues);
            statValues = new int[listStatHeaders.size()][];
            for (int i = 0; i < statValues.length; i++)
                statValues[i] = new int[listStatNamesAndValues.get(i).size()];

            switch (toLoad)
            {
                case StatsFragment.LOADING_BOWLER_STATS:
                    numberOfGeneralDetails = 1;
                    cursor = getBowlerOrLeagueCursor(false);
                    break;
                case StatsFragment.LOADING_LEAGUE_STATS:
                    numberOfGeneralDetails = 2;
                    listStatNamesAndValues.get(mStatsGeneral).add(1,
                            new AbstractMap.SimpleEntry<>("League/Event",
                                    mainActivity.getLeagueName()));
                    cursor = getBowlerOrLeagueCursor(true);
                    break;
                case StatsFragment.LOADING_SERIES_STATS:
                    numberOfGeneralDetails = 3;
                    listStatNamesAndValues.get(mStatsGeneral).add(1,
                            new AbstractMap.SimpleEntry<>("League/Event",
                                    mainActivity.getLeagueName()));
                    listStatNamesAndValues.get(mStatsGeneral).add(2,
                            new AbstractMap.SimpleEntry<>("Date", mainActivity.getSeriesDate()));
                    cursor = getSeriesCursor();
                    break;
                case StatsFragment.LOADING_GAME_STATS:
                    numberOfGeneralDetails = 4;
                    listStatNamesAndValues.get(mStatsGeneral).add(1,
                            new AbstractMap.SimpleEntry<>("League/Event",
                                    mainActivity.getLeagueName()));
                    listStatNamesAndValues.get(mStatsGeneral).add(2,
                            new AbstractMap.SimpleEntry<>("Date", mainActivity.getSeriesDate()));
                    listStatNamesAndValues.get(mStatsGeneral).add(3,
                            new AbstractMap.SimpleEntry<>("Game #",
                                    String.valueOf(mainActivity.getGameNumber())));
                    cursor = getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("invalid value for toLoad: " + toLoad
                            + ". must be between 0 and 3 (inclusive)");
            }

            /**
             * Passes through rows in cursor and updates stats which
             * are affected as each frame is analyzed
             */

            final byte numberOfGames = (toLoad >= StatsFragment.LOADING_LEAGUE_STATS
                    ? mainActivity.getNumberOfGames()
                    : 20);
            int totalShotsAtMiddle = 0;
            int spareChances = 0;
            int seriesTotal = 0;
            int[] totalByGame = new int[numberOfGames];
            int[] countByGame = new int[numberOfGames];
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    byte frameNumber = (byte) cursor.getInt(
                            cursor.getColumnIndex(Contract.FrameEntry.COLUMN_FRAME_NUMBER));
                    if (toLoad != StatsFragment.LOADING_GAME_STATS && frameNumber == 1)
                    {
                        short gameScore =
                                cursor.getShort(cursor.getColumnIndex(Contract.GameEntry.COLUMN_SCORE));
                        byte gameNumber = (byte) cursor.getInt(
                                cursor.getColumnIndex(Contract.GameEntry.COLUMN_GAME_NUMBER));

                        totalByGame[gameNumber - 1] += gameScore;
                        countByGame[gameNumber - 1]++;

                        byte matchResults = (byte) (cursor.getInt(
                                cursor.getColumnIndex(Contract.GameEntry.COLUMN_MATCH_PLAY)));
                        if (matchResults > 0)
                            statValues[mStatsMatch][matchResults - 1]++;

                        if (statValues[mStatsOverall][Constants.STAT_HIGH_SINGLE] < gameScore)
                        {
                            statValues[mStatsOverall][Constants.STAT_HIGH_SINGLE] = gameScore;
                        }
                        statValues[mStatsOverall][Constants.STAT_TOTAL_PINS] += gameScore;
                        statValues[mStatsOverall][Constants.STAT_NUMBER_OF_GAMES]++;

                        if (gameNumber == 1)
                        {
                            if (statValues[mStatsOverall][Constants.STAT_HIGH_SERIES] < seriesTotal)
                                statValues[mStatsOverall][Constants.STAT_HIGH_SERIES] = seriesTotal;
                            seriesTotal = gameScore;
                        }
                        else
                            seriesTotal += gameScore;
                    }

                    boolean gameIsManual =
                            (cursor.getInt(cursor.getColumnIndex(Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual)
                    {
                        cursor.moveToNext();
                        continue;
                    }
                    boolean frameAccessed =
                            (cursor.getInt(cursor.getColumnIndex(Contract.FrameEntry.COLUMN_IS_ACCESSED))
                                    == 1);
                    if (toLoad == StatsFragment.LOADING_GAME_STATS && !frameAccessed)
                        break;

                    String frameFouls =
                            cursor.getString(cursor.getColumnIndex(Contract.FrameEntry.COLUMN_FOULS));
                    String[] ballStrings = {
                            cursor.getString(cursor.getColumnIndex(Contract.FrameEntry.COLUMN_PIN_STATE[0])),
                            cursor.getString(cursor.getColumnIndex(Contract.FrameEntry.COLUMN_PIN_STATE[1])),
                            cursor.getString(
                                    cursor.getColumnIndex(Contract.FrameEntry.COLUMN_PIN_STATE[2]))};
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
                            statValues[mStatsFouls][0]++;
                    }

                    if (frameNumber == Constants.NUMBER_OF_FRAMES)
                    {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(ballValue, statValues, 0);
                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0)
                        {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                            {
                                statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            }
                            else
                            {
                                statValues[mStatsPins][Constants.STAT_PINS_LEFT] +=
                                        countPinsLeftStanding(pinState[2]);
                            }
                        }
                        else
                        {
                            totalShotsAtMiddle++;
                            ballValue = getFirstBallValue(pinState[1]);
                            if (ballValue != -1)
                                statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT]++;
                            increaseFirstBallStat(ballValue, statValues, 0);

                            if (ballValue != 0)
                            {
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                {
                                    statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS]++;
                                    increaseFirstBallStat(ballValue, statValues, 1);

                                    if (ballValue >= 5)
                                        spareChances++;
                                }
                                else
                                {
                                    statValues[mStatsPins][Constants.STAT_PINS_LEFT] +=
                                            countPinsLeftStanding(pinState[2]);
                                }
                            }
                            else
                            {
                                totalShotsAtMiddle++;
                                ballValue = getFirstBallValue(pinState[2]);
                                if (ballValue != -1)
                                    statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT]++;
                                increaseFirstBallStat(ballValue, statValues, 0);

                                if (ballValue != 0)
                                {
                                    statValues[mStatsPins][Constants.STAT_PINS_LEFT] +=
                                            countPinsLeftStanding(pinState[2]);
                                }
                            }
                        }
                    }
                    else
                    {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT]++;
                        increaseFirstBallStat(ballValue, statValues, 0);

                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0)
                        {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                            {
                                statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS]++;
                                increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            }
                            else
                            {
                                statValues[mStatsPins][Constants.STAT_PINS_LEFT] +=
                                        countPinsLeftStanding(pinState[2]);
                            }
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (toLoad != StatsFragment.LOADING_GAME_STATS)
            {
                if (statValues[mStatsOverall][Constants.STAT_HIGH_SERIES] < seriesTotal)
                    statValues[mStatsOverall][Constants.STAT_HIGH_SERIES] = seriesTotal;

                if (toLoad != StatsFragment.LOADING_SERIES_STATS)
                {
                    for (byte i = 0; i < numberOfGames; i++)
                        statValues[mStatsGameAverage][i] = (countByGame[i] > 0)
                                ? totalByGame[i] / countByGame[i]
                                : 0;
                }

                if (statValues[mStatsOverall][Constants.STAT_NUMBER_OF_GAMES] > 0)
                {
                    statValues[mStatsOverall][Constants.STAT_AVERAGE] =
                            statValues[mStatsOverall][Constants.STAT_TOTAL_PINS]
                                    / statValues[mStatsOverall][Constants.STAT_NUMBER_OF_GAMES];
                    statValues[mStatsPins][Constants.STAT_PINS_AVERAGE] =
                            statValues[mStatsPins][Constants.STAT_PINS_LEFT]
                                    / statValues[mStatsOverall][Constants.STAT_NUMBER_OF_GAMES];
                }
            }
            cursor.close();
            setGeneralAndDetailedStatValues(listStatNamesAndValues, statValues, totalShotsAtMiddle,
                    spareChances, numberOfGeneralDetails, toLoad);

            return new List<?>[]{listStatHeaders, listStatNamesAndValues};
        }

        @SuppressWarnings("unchecked") //Types of parameters are known
        @Override
        protected void onPostExecute(List<?>[] lists)
        {
            mListStatHeaders.addAll((List<String>) lists[0]);
            mListStatNamesAndValues.addAll(
                    (List<List<AbstractMap.SimpleEntry<String, String>>>) lists[1]);
            mAdapterStats.notifyDataSetChanged();
        }
    }

    /**
     * Sets the strings in the list mListStatValues.
     *
     * @param listStatNamesAndValues stat names and values
     * @param statValues raw value of stat
     * @param totalShotsAtMiddle total "first ball" opportunities for a game, league or bowler
     * @param spareChances total chances a bowler had to spare a ball
     * @param statOffset position in mListStatValues to start altering
     * @param toLoad stats being loaded
     */
    private void setGeneralAndDetailedStatValues(
            List<List<AbstractMap.SimpleEntry<String, String>>> listStatNamesAndValues,
            int[][] statValues, int totalShotsAtMiddle, int spareChances, int statOffset,
            byte toLoad)
    {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT] > 0)
        {
            listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT]
                            / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[mStatsGeneral][Constants.STAT_MIDDLE_HIT] + "/"
                            + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[mStatsGeneral][Constants.STAT_STRIKES] > 0)
        {
            listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[mStatsGeneral][Constants.STAT_STRIKES]
                            / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[mStatsGeneral][Constants.STAT_STRIKES] + "/"
                            + totalShotsAtMiddle + "]");
        }
        currentStatPosition++;
        if (statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS] > 0)
        {
            listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).setValue(
                    decimalFormat.format(statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS]
                            / (double) spareChances * 100)
                            + "% [" + statValues[mStatsGeneral][Constants.STAT_SPARE_CONVERSIONS]
                            + "/" + spareChances + "]");
        }

        currentStatPosition = 0;
        for (int i = 0; i < Constants.STAT_RIGHT_SPLIT_SPARED; i += 2, currentStatPosition += 2)
        {
            if (statValues[mStatsFirstBall][i] > 0)
            {
                listStatNamesAndValues.get(mStatsFirstBall).get(currentStatPosition).setValue(
                        decimalFormat.format(
                                statValues[mStatsFirstBall][i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[mStatsFirstBall][i] + "/" + totalShotsAtMiddle
                                + "]");
            }
            if (statValues[mStatsFirstBall][i + 1] > 0)
            {
                listStatNamesAndValues.get(mStatsFirstBall).get(currentStatPosition + 1).setValue(
                        decimalFormat.format(statValues[mStatsFirstBall][i + 1]
                                / (double) statValues[mStatsFirstBall][i] * 100)
                                + "% [" + statValues[mStatsFirstBall][i + 1] + "/"
                                + statValues[mStatsFirstBall][i] + "]");
            }
        }

        listStatNamesAndValues.get(mStatsFouls).get(0).setValue(
                String.valueOf(statValues[mStatsFouls][0]));
        listStatNamesAndValues.get(mStatsPins).get(0).setValue(
                String.valueOf(statValues[mStatsPins][0]));

        if (toLoad < StatsFragment.LOADING_GAME_STATS)
        {
            if (toLoad != StatsFragment.LOADING_SERIES_STATS)
            {
                for (byte i = 0; i < statValues[mStatsGameAverage].length; i++)
                    listStatNamesAndValues.get(mStatsGameAverage).get(i).setValue(
                            String.valueOf(statValues[mStatsGameAverage][i]));
            }

            listStatNamesAndValues.get(mStatsPins).get(1).setValue(
                    String.valueOf(statValues[mStatsPins][1]));

            int totalMatchPlayGames = 0;
            for (int stat : statValues[mStatsMatch])
                totalMatchPlayGames += stat;
            for (byte i = 0; i < statValues[mStatsMatch].length; i++)
                listStatNamesAndValues.get(mStatsMatch).get(i).setValue(
                        decimalFormat.format(
                                statValues[mStatsMatch][i] / (double) totalMatchPlayGames * 100)
                                + "% [" + statValues[mStatsMatch][i] + "/" + totalMatchPlayGames
                                + "]");

            for (byte i = 0; i < statValues[mStatsOverall].length; i++)
                listStatNamesAndValues.get(mStatsOverall).get(i).setValue(
                        String.valueOf(statValues[mStatsOverall][i]));
        }
    }

    /**
     * Returns the indicated state of the pins after a ball was thrown.
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
        for (boolean knockedDown : firstBall)
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
     * Counts the total value of pins which were left at the end of a frame on the third ball.
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
                switch (i)
                {
                    case 0:
                    case 4:
                        pinsLeftStanding += 2;
                        break;
                    case 1:
                    case 3:
                        pinsLeftStanding += 3;
                        break;
                    case 2:
                        pinsLeftStanding += 5;
                        break;
                }
            }
        }
        return pinsLeftStanding;
    }

    /**
     * Checks which situation has occurred by the state of the pins in ball.
     *
     * @param ball result of the pins after a ball was thrown
     * @param statValues stat values to update
     * @param offset indicates a spare was thrown and the spare count should be increased for a
     * stat
     */
    private void increaseFirstBallStat(int ball, int[][] statValues, int offset)
    {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch (ball)
        {
            case Constants.BALL_VALUE_STRIKE:
                if (offset == 0)
                {
                    statValues[mStatsGeneral][Constants.STAT_STRIKES]++;
                }
                break;
            case Constants.BALL_VALUE_LEFT:
                statValues[mStatsFirstBall][Constants.STAT_LEFT + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT:
                statValues[mStatsFirstBall][Constants.STAT_RIGHT + offset]++;
                break;
            case Constants.BALL_VALUE_LEFT_CHOP:
                statValues[mStatsFirstBall][Constants.STAT_LEFT_CHOP + offset]++;
                statValues[mStatsFirstBall][Constants.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_CHOP:
                statValues[mStatsFirstBall][Constants.STAT_RIGHT_CHOP + offset]++;
                statValues[mStatsFirstBall][Constants.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_ACE:
                statValues[mStatsFirstBall][Constants.STAT_ACES + offset]++;
                break;
            case Constants.BALL_VALUE_LEFT_SPLIT:
                statValues[mStatsFirstBall][Constants.STAT_LEFT_SPLIT + offset]++;
                statValues[mStatsFirstBall][Constants.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_SPLIT:
                statValues[mStatsFirstBall][Constants.STAT_RIGHT_SPLIT + offset]++;
                statValues[mStatsFirstBall][Constants.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_HEAD_PIN:
                statValues[mStatsFirstBall][Constants.STAT_HEAD_PINS + offset]++;
        }
    }

    /**
     * Returns a cursor from database to load either bowler or league stats.
     *
     * @param shouldGetLeagueStats if true, league stats will be loaded. Bowler stats will be loaded
     * otherwise
     * @return a cursor with rows relevant to mBowlerId or mLeagueId
     */
    private Cursor getBowlerOrLeagueCursor(boolean shouldGetLeagueStats)
    {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isEventIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_EVENTS, true);
        boolean isOpenIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_OPEN, true);
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + Contract.GameEntry.COLUMN_SCORE + ", "
                + Contract.GameEntry.COLUMN_GAME_NUMBER + ", "
                + Contract.GameEntry.COLUMN_IS_MANUAL + ", "
                + Contract.GameEntry.COLUMN_MATCH_PLAY + ", "
                + Contract.FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + Contract.FrameEntry.COLUMN_IS_ACCESSED + ", "
                + Contract.FrameEntry.COLUMN_FOULS + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + Contract.LeagueEntry.TABLE_NAME + " AS league"
                + " INNER JOIN " + Contract.SeriesEntry.TABLE_NAME + " AS series"
                + " ON league." + Contract.LeagueEntry._ID + "=series." + Contract.SeriesEntry.COLUMN_LEAGUE_ID
                + " INNER JOIN " + Contract.GameEntry.TABLE_NAME + " AS game"
                + " ON series." + Contract.SeriesEntry._ID + "=game." + Contract.GameEntry.COLUMN_SERIES_ID
                + " INNER JOIN " + Contract.FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + Contract.GameEntry._ID + "=frame." + Contract.FrameEntry.COLUMN_GAME_ID
                + ((shouldGetLeagueStats)
                ? " WHERE league." + Contract.LeagueEntry._ID + "=?"
                : " WHERE league." + Contract.LeagueEntry.COLUMN_BOWLER_ID + "=?")
                + " AND " + ((!shouldGetLeagueStats && !isEventIncluded)
                ? Contract.LeagueEntry.COLUMN_IS_EVENT
                : "'0'") + "=?"
                + " AND " + ((!shouldGetLeagueStats && !isOpenIncluded)
                ? Contract.LeagueEntry.COLUMN_LEAGUE_NAME + "!"
                : "'0'") + "=?"
                + " ORDER BY league." + Contract.LeagueEntry._ID
                + ", series." + Contract.SeriesEntry._ID
                + ", game." + Contract.GameEntry.COLUMN_GAME_NUMBER
                + ", frame." + Contract.FrameEntry.COLUMN_FRAME_NUMBER;

        String[] rawStatsArgs = {
                ((shouldGetLeagueStats)
                        ? String.valueOf(((MainActivity) getActivity()).getLeagueId())
                        : String.valueOf(((MainActivity) getActivity()).getBowlerId())),
                String.valueOf(0),
                ((!shouldGetLeagueStats && !isOpenIncluded)
                        ? Constants.NAME_OPEN_LEAGUE
                        : String.valueOf(0))};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a cursor from database to load series stats.
     *
     * @return a cursor with rows relevant to mSeriesId
     */
    private Cursor getSeriesCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + Contract.GameEntry.COLUMN_SCORE + ", "
                + Contract.GameEntry.COLUMN_GAME_NUMBER + ", "
                + Contract.GameEntry.COLUMN_IS_MANUAL + ", "
                + Contract.GameEntry.COLUMN_MATCH_PLAY + ", "
                + Contract.FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + Contract.FrameEntry.COLUMN_IS_ACCESSED + ", "
                + Contract.FrameEntry.COLUMN_FOULS + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + Contract.GameEntry.TABLE_NAME + " AS game"
                + " INNER JOIN " + Contract.FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + Contract.GameEntry._ID + "=frame." + Contract.FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + Contract.GameEntry.COLUMN_SERIES_ID + "=?"
                + " ORDER BY game." + Contract.GameEntry.COLUMN_GAME_NUMBER + ", frame."
                + Contract.FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(((MainActivity) getActivity()).getSeriesId())};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }

    /**
     * Returns a cursor from the database to load game stats.
     *
     * @return a cursor with rows relevant to mGameId
     */
    private Cursor getGameCursor()
    {
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
        String rawStatsQuery = "SELECT "
                + Contract.GameEntry.COLUMN_SCORE + ", "
                + Contract.GameEntry.COLUMN_IS_MANUAL + ", "
                + Contract.FrameEntry.COLUMN_FRAME_NUMBER + ", "
                + Contract.FrameEntry.COLUMN_IS_ACCESSED + ", "
                + Contract.FrameEntry.COLUMN_FOULS + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[0] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[1] + ", "
                + Contract.FrameEntry.COLUMN_PIN_STATE[2]
                + " FROM " + Contract.GameEntry.TABLE_NAME + " AS game"
                + " INNER JOIN " + Contract.FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + Contract.GameEntry._ID + "=frame." + Contract.FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + Contract.GameEntry._ID + "=?"
                + " ORDER BY " + Contract.FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(((MainActivity) getActivity()).getGameId())};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }
}
