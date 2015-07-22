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

import com.github.mikephil.charting.charts.LineChart;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by Joseph Roque on 15-07-20. Manages the UI to display information about the stats in a
 * graph for a particular bowler
 */
public class StatsGraphFragment
        extends Fragment
        implements Theme.ChangeableTheme
{

    private static final String ARG_STAT_CATEGORY = "arg_stat_cat";
    private static final String ARG_STAT_INDEX = "arg_stat_index";

    private LineChart mLineChartStats;

    private int mStatCategory;
    private int mStatIndex;

    /**
     * Creates a new instance of {@code StatsGraphFragment} with the parameters provided.
     *
     * @param statCategory category of stat displayed in graph
     * @param statIndex index of stat displayed in graph
     * @return a new instance of StatsGraphFragment
     */
    public static StatsGraphFragment newInstance(int statCategory, int statIndex)
    {
        StatsGraphFragment fragment = new StatsGraphFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STAT_CATEGORY, statCategory);
        args.putInt(ARG_STAT_INDEX, statIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_graph, container, false);

        if (savedInstanceState != null)
        {
            mStatCategory = savedInstanceState.getInt(ARG_STAT_CATEGORY, 0);
            mStatIndex = savedInstanceState.getInt(ARG_STAT_INDEX, 0);
        }
        else
        {
            Bundle arguments = getArguments();
            mStatCategory = arguments.getInt(ARG_STAT_CATEGORY, 0);
            mStatIndex = arguments.getInt(ARG_STAT_INDEX, 0);
        }

        mLineChartStats = (LineChart) rootView.findViewById(R.id.chart_stats);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (getActivity() != null)
        {
            MainActivity mainActivity = (MainActivity) getActivity();

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

            mainActivity.setActionBarTitle(titleToSet, true);
            //new LoadStatsGraphTask().execute(statsToLoad);
        }

        updateTheme();
    }

    @Override
    public void updateTheme()
    {
        // does nothing right now
    }

    /**
     * Loads data from the database and calculates relevant stats depending on which type of stats
     * are being loaded.
     */
    /*private class LoadStatsGraphTask
            extends AsyncTask<Byte, Void, List<?>[]>
    {

        @Override
        protected void onPreExecute()
        {
            mLineChartStats.clear();
        }

        @Override
        protected List<?>[] doInBackground(Byte... statsToLoad)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            MainActivity.waitForSaveThreads(mainActivity);

            final byte toLoad = statsToLoad[0];
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

            *//**
             * Passes through rows in cursor and updates stats which
             * are affected as each frame is analyzed
             *//*

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
                            (cursor.getInt(cursor.getColumnIndex(Contract.GameEntry.COLUMN_IS_MANUAL))
                                    == 1);
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
                                    cursor.getColumnIndex(Contract.FrameEntry.COLUMN_PIN_STATE[2]))
                    };
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

            return null;
        }

        @Override
        protected void onPostExecute(List<?>[] result)
        {

        }
    }*/

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
                + " ON league." + Contract.LeagueEntry._ID + "=series."
                + Contract.SeriesEntry.COLUMN_LEAGUE_ID
                + " INNER JOIN " + Contract.GameEntry.TABLE_NAME + " AS game"
                + " ON series." + Contract.SeriesEntry._ID + "=game."
                + Contract.GameEntry.COLUMN_SERIES_ID
                + " INNER JOIN " + Contract.FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + Contract.GameEntry._ID + "=frame."
                + Contract.FrameEntry.COLUMN_GAME_ID
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
                        : String.valueOf(0))
        };

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
                + " ON game." + Contract.GameEntry._ID + "=frame."
                + Contract.FrameEntry.COLUMN_GAME_ID
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
                + " ON game." + Contract.GameEntry._ID + "=frame."
                + Contract.FrameEntry.COLUMN_GAME_ID
                + " WHERE game." + Contract.GameEntry._ID + "=?"
                + " ORDER BY " + Contract.FrameEntry.COLUMN_FRAME_NUMBER;
        String[] rawStatsArgs = {String.valueOf(((MainActivity) getActivity()).getGameId())};

        return database.rawQuery(rawStatsQuery, rawStatsArgs);
    }
}
