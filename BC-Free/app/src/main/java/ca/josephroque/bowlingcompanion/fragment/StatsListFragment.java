package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
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
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;
import ca.josephroque.bowlingcompanion.utilities.Score;
import ca.josephroque.bowlingcompanion.utilities.StatUtils;
import ca.josephroque.bowlingcompanion.view.AnimatedExpandableListView;

/**
 * Created by Joseph Roque on 15-07-20. Manages the UI to display information about the stats in a list for a particular
 * bowler
 */
public class StatsListFragment
        extends Fragment
        implements Theme.ChangeableTheme,
        FloatingActionButtonHandler {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsListFragment";

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
    /** Number of static stats at the beginning of the first group of stats. */
    private byte mNumberOfGeneralDetails = -1;
    /** Indicates the type of stats which will be loaded. */
    private byte mStatsToLoad = -1;

    /** List of group headers. */
    private List<String> mListStatHeaders;
    /** List of list of map entries which hold a name and a value, for each group. */
    private List<List<Pair<String, String>>> mListStatNamesAndValues;

    /**
     * Creates a new instance of {@code StatsListFragment} with the parameters provided.
     *
     * @return a new instance of StatsListFragment
     */
    public static StatsListFragment newInstance() {
        return new StatsListFragment();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_list, container, false);

        mListStatHeaders = new ArrayList<>();
        mListStatNamesAndValues = new ArrayList<>();

        mAdapterStats = new StatsExpandableAdapter(getActivity(), mListStatHeaders,
                mListStatNamesAndValues);

        final AnimatedExpandableListView listView
                = (AnimatedExpandableListView) rootView.findViewById(R.id.elv_stats);
        listView.setAdapter(mAdapterStats);
        setExpandableListViewIndicator(listView);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(
                    ExpandableListView parent,
                    View v,
                    int groupPosition,
                    int childPosition,
                    long id) {
                if (mStatsToLoad != StatUtils.LOADING_BOWLER_STATS
                        && mStatsToLoad != StatUtils.LOADING_LEAGUE_STATS)
                    return true;

                if (groupPosition == 0 && childPosition - mNumberOfGeneralDetails >= 0)
                    openStatGraph(groupPosition, childPosition - mNumberOfGeneralDetails);
                else if (groupPosition > 0)
                    openStatGraph(groupPosition, childPosition);

                return true;
            }
        });

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(
                    ExpandableListView parent,
                    View v,
                    int groupPosition,
                    long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition))
                    listView.collapseGroupWithAnimation(groupPosition);
                else
                    listView.expandGroupWithAnimation(groupPosition);

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setDrawerState(false);

            //Checks what type of stats should be displayed, depending
            //on what data is available in the parent activity at the time
            int titleToSet;
            if (mainActivity.getGameId() == -1) {
                if (mainActivity.getSeriesId() == -1) {
                    if (mainActivity.getLeagueId() == -1) {
                        titleToSet = R.string.title_stats_bowler;
                        mStatsToLoad = StatUtils.LOADING_BOWLER_STATS;
                    } else {
                        titleToSet = R.string.title_stats_league;
                        mStatsToLoad = StatUtils.LOADING_LEAGUE_STATS;
                    }
                } else {
                    titleToSet = R.string.title_stats_series;
                    mStatsToLoad = StatUtils.LOADING_SERIES_STATS;
                }
            } else {
                titleToSet = R.string.title_stats_game;
                mStatsToLoad = StatUtils.LOADING_GAME_STATS;
            }

            if (mStatsToLoad == StatUtils.LOADING_BOWLER_STATS
                    || mStatsToLoad == StatUtils.LOADING_LEAGUE_STATS)
                mainActivity.setFloatingActionButtonState(R.drawable.ic_trending_up_black_24dp);
            else
                mainActivity.setFloatingActionButtonState(0);

            mainActivity.setActionBarTitle(titleToSet, true);
            new LoadStatsListTask(this).execute(mStatsToLoad);
        }

        updateTheme();
    }

    @Override
    public void updateTheme() {
        mAdapterStats.updateTheme();
    }

    @Override
    public void onFabClick() {
        if (mStatsToLoad == StatUtils.LOADING_BOWLER_STATS
                || mStatsToLoad == StatUtils.LOADING_LEAGUE_STATS)
            openStatsPrompt();
    }

    /**
     * Creates and sets the group indicator for the expandle list view.
     *
     * @param listView list view to set indicator for
     */
    @SuppressWarnings("CheckStyle")
    private void setExpandableListViewIndicator(ExpandableListView listView) {
        final Drawable indicator = DisplayUtils.getDrawable(getResources(),
                R.drawable.stat_indicator);
        if (indicator != null) {
            listView.setGroupIndicator(indicator);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                listView.setIndicatorBounds(
                        DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                                16),
                        DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                                16) + indicator.getMinimumWidth());
            } else {
                listView.setIndicatorBoundsRelative(
                        DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                                16),
                        DataFormatter.getPixelsFromDP(getResources().getDisplayMetrics().density,
                                16) + indicator.getMinimumWidth());
            }
        }
    }

    /**
     * Opens a prompt to remind user they can click on stats to see graphs.
     */
    private void openStatsPrompt() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.text_opening_graph)
                .setMessage(R.string.text_opening_graph_click)
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openStatGraph(0, 0);
                    }
                })
                .create()
                .show();
    }

    /**
     * Adds headers and filler data to lists.
     *
     * @param mainActivity activity which created this object
     * @param statsToLoad type of stats which are being loaded
     * @param headers headers of groups
     * @param namesAndValues entries in each group
     */
    @SuppressWarnings({"Convert2Diamond", "CheckStyle"})
    private void prepareListData(MainActivity mainActivity,
                                 byte statsToLoad,
                                 List<String> headers,
                                 List<List<Pair<String, String>>> namesAndValues) {
        //Stat names which could possibly be displayed, depending on stats being loaded

        headers.add("General");
        namesAndValues.add(new ArrayList<Pair<String, String>>());
        mStatsGeneral = 0;
        namesAndValues.get(mStatsGeneral).add(Pair.create("Bowler", mainActivity.getBowlerName()));
        int i = 0;
        while (true) {
            try {
                namesAndValues.get(mStatsGeneral).add(Pair.create(
                        StatUtils.getStatName(StatUtils.STAT_CATEGORY_GENERAL, i, false), "--"));
            } catch (IllegalArgumentException ex) {
                break;
            }
            i++;
        }

        headers.add("First Ball");
        namesAndValues.add(new ArrayList<Pair<String, String>>());
        mStatsFirstBall = 1;
        i = 0;
        while (true) {
            try {
                namesAndValues.get(mStatsFirstBall).add(Pair.create(
                        StatUtils.getStatName(StatUtils.STAT_CATEGORY_FIRST_BALL, i, false), "--"));
            } catch (IllegalArgumentException ex) {
                break;
            }
            i++;
        }

        headers.add("Fouls");
        namesAndValues.add(new ArrayList<Pair<String, String>>());
        mStatsFouls = 2;
        i = 0;
        while (true) {
            try {
                namesAndValues.get(mStatsFouls).add(Pair.create(
                        StatUtils.getStatName(StatUtils.STAT_CATEGORY_FOULS, i, false), "--"));
            } catch (IllegalArgumentException ex) {
                break;
            }
            i++;
        }

        headers.add("Pins Left on Deck");
        namesAndValues.add(new ArrayList<Pair<String, String>>());
        mStatsPins = 3;
        namesAndValues.get(mStatsPins).add(Pair.create(StatUtils.getStatName(
                StatUtils.STAT_CATEGORY_PINS, StatUtils.STAT_PINS_LEFT, false), "--"));

        if (statsToLoad < StatUtils.LOADING_SERIES_STATS) {
            headers.add("Average by Game");
            namesAndValues.add(new ArrayList<Pair<String, String>>());
            mStatsGameAverage = 4;
            final byte numberOfGames = (statsToLoad >= StatUtils.LOADING_LEAGUE_STATS
                    ? ((mainActivity.getLeagueName().substring(1)
                    .equals(Constants.NAME_OPEN_LEAGUE))
                    ? 5
                    : mainActivity.getDefaultNumberOfGames())
                    : 20);
            for (i = 0; i < numberOfGames; i++)
                namesAndValues.get(mStatsGameAverage).add(Pair.create(StatUtils.getStatName(
                        StatUtils.STAT_CATEGORY_AVERAGE_BY_GAME, i, false), "--"));
        }

        if (statsToLoad < StatUtils.LOADING_GAME_STATS) {
            namesAndValues.get(mStatsPins).add(Pair.create(StatUtils.getStatName(
                    StatUtils.STAT_CATEGORY_PINS, StatUtils.STAT_PINS_AVERAGE, false), "--"));

            headers.add("Match Play");
            namesAndValues.add(new ArrayList<Pair<String, String>>());
            mStatsMatch = (byte) (mStatsGameAverage == -1
                    ? 4
                    : 5);
            i = 0;
            while (true) {
                try {
                    namesAndValues.get(mStatsMatch).add(Pair.create(
                            StatUtils.getStatName(StatUtils.STAT_CATEGORY_MATCH_PLAY, i, false),
                            "--"));
                } catch (IllegalArgumentException ex) {
                    break;
                }
                i++;
            }

            headers.add("Overall");
            namesAndValues.add(new ArrayList<Pair<String, String>>());
            mStatsOverall = (byte) (mStatsMatch + 1);
            i = 0;
            while (true) {
                try {
                    namesAndValues.get(mStatsOverall).add(Pair.create(
                            StatUtils.getStatName(StatUtils.STAT_CATEGORY_OVERALL, i, false),
                            "--"));
                } catch (IllegalArgumentException ex) {
                    break;
                }
                i++;
            }
        }
    }

    /**
     * Loads data from the database and calculates relevant stats depending on which type of stats are being loaded.
     */
    private static final class LoadStatsListTask
            extends AsyncTask<Byte, Void, List<?>[]> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<StatsListFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadStatsListTask(StatsListFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            StatsListFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mListStatHeaders.clear();
            fragment.mListStatNamesAndValues.clear();
            fragment.mAdapterStats.notifyDataSetChanged();
        }

        @SuppressWarnings("CheckStyle")
        @Override
        protected List<?>[] doInBackground(Byte... statsToLoad) {
            StatsListFragment fragment = mFragment.get();
            if (fragment == null)
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;
            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            final byte toLoad = statsToLoad[0];
            Cursor cursor;
            int[][] statValues;
            List<String> listStatHeaders = new ArrayList<>();
            List<List<Pair<String, String>>> listStatNamesAndValues = new ArrayList<>();

            fragment.prepareListData(mainActivity, toLoad, listStatHeaders, listStatNamesAndValues);
            statValues = new int[listStatHeaders.size()][];
            for (int i = 0; i < statValues.length; i++)
                statValues[i] = new int[listStatNamesAndValues.get(i).size()];

            switch (toLoad) {
                case StatUtils.LOADING_BOWLER_STATS:
                    fragment.mNumberOfGeneralDetails = 1;
                    cursor = fragment.getBowlerOrLeagueCursor(false);
                    break;
                case StatUtils.LOADING_LEAGUE_STATS:
                    fragment.mNumberOfGeneralDetails = 2;
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(1,
                            Pair.create("League/Event", mainActivity.getLeagueName().substring(1)));
                    cursor = fragment.getBowlerOrLeagueCursor(true);
                    break;
                case StatUtils.LOADING_SERIES_STATS:
                    fragment.mNumberOfGeneralDetails = 3;
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(1,
                            Pair.create("League/Event", mainActivity.getLeagueName().substring(1)));
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(2,
                            Pair.create("Date", mainActivity.getSeriesDate()));
                    cursor = fragment.getSeriesCursor();
                    break;
                case StatUtils.LOADING_GAME_STATS:
                    fragment.mNumberOfGeneralDetails = 4;
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(1,
                            Pair.create("League/Event", mainActivity.getLeagueName().substring(1)));
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(2,
                            Pair.create("Date", mainActivity.getSeriesDate()));
                    listStatNamesAndValues.get(fragment.mStatsGeneral).add(3,
                            Pair.create("Game #", String.valueOf(mainActivity.getGameNumber())));
                    cursor = fragment.getGameCursor();
                    break;
                default:
                    throw new IllegalArgumentException("invalid value for toLoad: " + toLoad
                            + ". must be between 0 and 3 (inclusive)");
            }

            /**
             * Passes through rows in cursor and updates stats which
             * are affected as each frame is analyzed
             */

            final byte numberOfGames = (toLoad >= StatUtils.LOADING_LEAGUE_STATS
                    ? ((mainActivity.getLeagueName().substring(1)
                    .equals(Constants.NAME_OPEN_LEAGUE))
                    ? 5
                    : mainActivity.getDefaultNumberOfGames())
                    : 20);
            int totalShotsAtMiddle = 0;
            int spareChances = 0;
            int seriesTotal = 0;
            int[] totalByGame = new int[numberOfGames];
            int[] countByGame = new int[numberOfGames];
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    byte frameNumber = (byte) cursor.getInt(
                            cursor.getColumnIndex(Contract.FrameEntry.COLUMN_FRAME_NUMBER));
                    if (toLoad != StatUtils.LOADING_GAME_STATS && frameNumber == 1) {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(Contract.GameEntry.COLUMN_SCORE));
                        byte gameNumber
                                = (byte) cursor.getInt(cursor.getColumnIndex(Contract.GameEntry.COLUMN_GAME_NUMBER));

                        byte matchResults = (byte) (cursor.getInt(
                                cursor.getColumnIndex(Contract.GameEntry.COLUMN_MATCH_PLAY)));
                        if (matchResults > 0)
                            statValues[fragment.mStatsMatch][matchResults - 1]++;

                        if (gameScore > 0) {
                            totalByGame[gameNumber - 1] += gameScore;
                            countByGame[gameNumber - 1]++;

                            if (gameScore > statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SINGLE])
                                statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SINGLE] = gameScore;
                            statValues[fragment.mStatsOverall][StatUtils.STAT_TOTAL_PINS] += gameScore;
                            statValues[fragment.mStatsOverall][StatUtils.STAT_NUMBER_OF_GAMES]++;
                        }

                        if (gameNumber == 1) {
                            if (statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SERIES] < seriesTotal)
                                statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SERIES] = seriesTotal;
                            seriesTotal = gameScore;
                        } else {
                            seriesTotal += gameScore;
                        }
                    }

                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(
                            Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual) {
                        cursor.moveToNext();
                        continue;
                    }
                    boolean frameAccessed = (cursor.getInt(cursor.getColumnIndex(
                            Contract.FrameEntry.COLUMN_IS_ACCESSED)) == 1);
                    if (toLoad == StatUtils.LOADING_GAME_STATS && !frameAccessed)
                        break;

                    String frameFouls = Score.foulIntToString(cursor.getInt(cursor.getColumnIndex(
                            Contract.FrameEntry.COLUMN_FOULS)));

                    boolean[][] pinState = new boolean[3][5];
                    for (byte i = 0; i < pinState.length; i++) {
                        pinState[i] = Score.ballIntToBoolean(cursor.getInt(cursor.getColumnIndex(
                                Contract.FrameEntry.COLUMN_PIN_STATE[i])));
                    }

                    for (byte i = 1; i <= 3; i++) {
                        if (frameFouls.contains(String.valueOf(i)))
                            statValues[fragment.mStatsFouls][0]++;
                    }

                    if (frameNumber == Constants.NUMBER_OF_FRAMES) {
                        totalShotsAtMiddle++;
                        int ballValue = fragment.getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[fragment.mStatsGeneral][StatUtils.STAT_MIDDLE_HIT]++;
                        fragment.increaseFirstBallStat(ballValue, statValues, 0);
                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0) {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN)) {
                                statValues[fragment.mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS]++;
                                fragment.increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            } else {
                                statValues[fragment.mStatsPins][StatUtils.STAT_PINS_LEFT] +=
                                        fragment.countPinsLeftStanding(pinState[2]);
                            }
                        } else {
                            totalShotsAtMiddle++;
                            ballValue = fragment.getFirstBallValue(pinState[1]);
                            if (ballValue != -1)
                                statValues[fragment.mStatsGeneral][StatUtils.STAT_MIDDLE_HIT]++;
                            fragment.increaseFirstBallStat(ballValue, statValues, 0);

                            if (ballValue != 0) {
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN)) {
                                    statValues[fragment.mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS]++;
                                    fragment.increaseFirstBallStat(ballValue, statValues, 1);

                                    if (ballValue >= 5)
                                        spareChances++;
                                } else {
                                    statValues[fragment.mStatsPins][StatUtils.STAT_PINS_LEFT] +=
                                            fragment.countPinsLeftStanding(pinState[2]);
                                }
                            } else {
                                totalShotsAtMiddle++;
                                ballValue = fragment.getFirstBallValue(pinState[2]);
                                if (ballValue != -1)
                                    statValues[fragment.mStatsGeneral][StatUtils.STAT_MIDDLE_HIT]++;
                                fragment.increaseFirstBallStat(ballValue, statValues, 0);

                                if (ballValue != 0) {
                                    statValues[fragment.mStatsPins][StatUtils.STAT_PINS_LEFT] +=
                                            fragment.countPinsLeftStanding(pinState[2]);
                                }
                            }
                        }
                    } else {
                        totalShotsAtMiddle++;
                        int ballValue = fragment.getFirstBallValue(pinState[0]);
                        if (ballValue != -1)
                            statValues[fragment.mStatsGeneral][StatUtils.STAT_MIDDLE_HIT]++;
                        fragment.increaseFirstBallStat(ballValue, statValues, 0);

                        if (ballValue < 5 && ballValue != Constants.BALL_VALUE_STRIKE)
                            spareChances++;

                        if (ballValue != 0) {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN)) {
                                statValues[fragment.mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS]++;
                                fragment.increaseFirstBallStat(ballValue, statValues, 1);

                                if (ballValue >= 5)
                                    spareChances++;
                            } else {
                                statValues[fragment.mStatsPins][StatUtils.STAT_PINS_LEFT] +=
                                        fragment.countPinsLeftStanding(pinState[2]);
                            }
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (toLoad != StatUtils.LOADING_GAME_STATS) {
                if (statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SERIES] < seriesTotal)
                    statValues[fragment.mStatsOverall][StatUtils.STAT_HIGH_SERIES] = seriesTotal;

                if (toLoad != StatUtils.LOADING_SERIES_STATS) {
                    for (byte i = 0; i < numberOfGames; i++)
                        statValues[fragment.mStatsGameAverage][i] = (countByGame[i] > 0)
                                ? totalByGame[i] / countByGame[i]
                                : 0;
                }

                if (statValues[fragment.mStatsOverall][StatUtils.STAT_NUMBER_OF_GAMES] > 0) {
                    statValues[fragment.mStatsOverall][StatUtils.STAT_AVERAGE] =
                            statValues[fragment.mStatsOverall][StatUtils.STAT_TOTAL_PINS]
                                    / statValues[fragment.mStatsOverall][StatUtils.STAT_NUMBER_OF_GAMES];
                    statValues[fragment.mStatsPins][StatUtils.STAT_PINS_AVERAGE] =
                            statValues[fragment.mStatsPins][StatUtils.STAT_PINS_LEFT]
                                    / statValues[fragment.mStatsOverall][StatUtils.STAT_NUMBER_OF_GAMES];
                }
            }
            cursor.close();
            fragment.setGeneralAndDetailedStatValues(listStatNamesAndValues,
                    statValues,
                    totalShotsAtMiddle,
                    spareChances,
                    fragment.mNumberOfGeneralDetails, toLoad);

            return new List<?>[]{listStatHeaders, listStatNamesAndValues};
        }

        @SuppressWarnings("unchecked") //Types of parameters are known
        @Override
        protected void onPostExecute(List<?>[] lists) {
            StatsListFragment fragment = mFragment.get();
            if (lists == null || fragment == null)
                return;

            fragment.mListStatHeaders.addAll((List<String>) lists[0]);
            fragment.mListStatNamesAndValues.addAll(
                    (List<List<Pair<String, String>>>) lists[1]);
            fragment.mAdapterStats.notifyDataSetChanged();
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
    @SuppressWarnings("CheckStyle")
    private void setGeneralAndDetailedStatValues(
            List<List<Pair<String, String>>> listStatNamesAndValues,
            int[][] statValues, int totalShotsAtMiddle, int spareChances, int statOffset,
            byte toLoad) {
        int currentStatPosition = statOffset;
        final DecimalFormat decimalFormat = new DecimalFormat("##0.#");
        if (statValues[mStatsGeneral][StatUtils.STAT_MIDDLE_HIT] > 0) {
            listStatNamesAndValues.get(mStatsGeneral).set(currentStatPosition, Pair.create(
                    listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).first,
                    decimalFormat.format(statValues[mStatsGeneral][StatUtils.STAT_MIDDLE_HIT]
                            / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[mStatsGeneral][StatUtils.STAT_MIDDLE_HIT] + "/"
                            + totalShotsAtMiddle + "]"));
        }
        currentStatPosition++;
        if (statValues[mStatsGeneral][StatUtils.STAT_STRIKES] > 0) {
            listStatNamesAndValues.get(mStatsGeneral).set(currentStatPosition, Pair.create(
                    listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).first,
                    decimalFormat.format(statValues[mStatsGeneral][StatUtils.STAT_STRIKES]
                            / (double) totalShotsAtMiddle * 100)
                            + "% [" + statValues[mStatsGeneral][StatUtils.STAT_STRIKES] + "/"
                            + totalShotsAtMiddle + "]"));
        }
        currentStatPosition++;
        if (statValues[mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS] > 0) {
            listStatNamesAndValues.get(mStatsGeneral).set(currentStatPosition, Pair.create(
                    listStatNamesAndValues.get(mStatsGeneral).get(currentStatPosition).first,
                    decimalFormat.format(statValues[mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS]
                            / (double) spareChances * 100)
                            + "% [" + statValues[mStatsGeneral][StatUtils.STAT_SPARE_CONVERSIONS]
                            + "/" + spareChances + "]"));
        }

        currentStatPosition = 0;
        for (int i = 0; i < StatUtils.STAT_RIGHT_SPLIT_SPARED; i += 2, currentStatPosition += 2) {
            if (statValues[mStatsFirstBall][i] > 0) {
                listStatNamesAndValues.get(mStatsFirstBall).set(currentStatPosition, Pair.create(
                        listStatNamesAndValues.get(mStatsFirstBall).get(currentStatPosition).first,
                        decimalFormat.format(
                                statValues[mStatsFirstBall][i] / (double) totalShotsAtMiddle * 100)
                                + "% [" + statValues[mStatsFirstBall][i] + "/" + totalShotsAtMiddle
                                + "]"));
            }
            if (statValues[mStatsFirstBall][i + 1] > 0) {
                listStatNamesAndValues.get(mStatsFirstBall).set(currentStatPosition + 1,
                        Pair.create(listStatNamesAndValues.get(mStatsFirstBall)
                                        .get(currentStatPosition + 1).first,
                                decimalFormat.format(statValues[mStatsFirstBall][i + 1]
                                        / (double) statValues[mStatsFirstBall][i] * 100)
                                        + "% [" + statValues[mStatsFirstBall][i + 1] + "/"
                                        + statValues[mStatsFirstBall][i] + "]"));
            }
        }

        listStatNamesAndValues.get(mStatsFouls).set(0, Pair.create(
                listStatNamesAndValues.get(mStatsFouls).get(0).first,
                String.valueOf(statValues[mStatsFouls][0])));
        listStatNamesAndValues.get(mStatsPins).set(0, Pair.create(
                listStatNamesAndValues.get(mStatsPins).get(0).first,
                String.valueOf(statValues[mStatsPins][0])));

        if (toLoad < StatUtils.LOADING_GAME_STATS) {
            if (toLoad != StatUtils.LOADING_SERIES_STATS) {
                for (byte i = 0; i < statValues[mStatsGameAverage].length; i++)
                    listStatNamesAndValues.get(mStatsGameAverage).set(i, Pair.create(
                            listStatNamesAndValues.get(mStatsGameAverage).get(i).first,
                            String.valueOf(statValues[mStatsGameAverage][i])));
            }

            listStatNamesAndValues.get(mStatsPins).set(1, Pair.create(
                    listStatNamesAndValues.get(mStatsPins).get(1).first,
                    String.valueOf(statValues[mStatsPins][1])));

            int totalMatchPlayGames = 0;
            for (int stat : statValues[mStatsMatch])
                totalMatchPlayGames += stat;
            for (byte i = 0; i < statValues[mStatsMatch].length; i++)
                listStatNamesAndValues.get(mStatsMatch).set(i, Pair.create(
                        listStatNamesAndValues.get(mStatsMatch).get(i).first,
                        decimalFormat.format(
                                statValues[mStatsMatch][i] / (double) totalMatchPlayGames * 100)
                                + "% [" + statValues[mStatsMatch][i] + "/" + totalMatchPlayGames
                                + "]"));

            for (byte i = 0; i < statValues[mStatsOverall].length; i++)
                listStatNamesAndValues.get(mStatsOverall).set(i, Pair.create(
                        listStatNamesAndValues.get(mStatsOverall).get(i).first,
                        String.valueOf(statValues[mStatsOverall][i])));
        }
    }

    /**
     * Returns the indicated state of the pins after a ball was thrown.
     *
     * @param firstBall the ball thrown
     * @return the state of the pins after a ball was thrown
     */
    @SuppressWarnings("CheckStyle")
    private int getFirstBallValue(boolean[] firstBall) {
        if (!firstBall[2]) {
            return -1;
        }

        int numberOfPinsKnockedDown = 0;
        for (boolean knockedDown : firstBall) {
            if (knockedDown)
                numberOfPinsKnockedDown++;
        }

        if (numberOfPinsKnockedDown == 5)
            return Constants.BALL_VALUE_STRIKE;
        else if (numberOfPinsKnockedDown == 4) {
            if (!firstBall[0])
                return Constants.BALL_VALUE_LEFT;
            else if (!firstBall[4])
                return Constants.BALL_VALUE_RIGHT;
        } else if (numberOfPinsKnockedDown == 3) {
            if (!firstBall[3] && !firstBall[4])
                return Constants.BALL_VALUE_LEFT_CHOP;
            else if (!firstBall[0] && !firstBall[1])
                return Constants.BALL_VALUE_RIGHT_CHOP;
            else if (!firstBall[0] && !firstBall[4])
                return Constants.BALL_VALUE_ACE;
        } else if (numberOfPinsKnockedDown == 2) {
            if (firstBall[1])
                return Constants.BALL_VALUE_LEFT_SPLIT;
            else if (firstBall[3])
                return Constants.BALL_VALUE_RIGHT_SPLIT;
        } else
            return Constants.BALL_VALUE_HEAD_PIN;

        return -2;
    }

    /**
     * Counts the total value of pins which were left at the end of a frame on the third ball.
     *
     * @param thirdBall state of the pins after the third ball
     * @return total value of pins left standing
     */
    @SuppressWarnings("CheckStyle")
    private int countPinsLeftStanding(boolean[] thirdBall) {
        int pinsLeftStanding = 0;
        for (int i = 0; i < thirdBall.length; i++) {
            if (!thirdBall[i]) {
                switch (i) {
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
                    default:
                        // does nothing
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
     * @param offset indicates a spare was thrown and the spare count should be increased for a stat
     */
    private void increaseFirstBallStat(int ball, int[][] statValues, int offset) {
        if (offset > 1 || offset < 0)
            throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

        switch (ball) {
            case Constants.BALL_VALUE_STRIKE:
                if (offset == 0) {
                    statValues[mStatsGeneral][StatUtils.STAT_STRIKES]++;
                }
                break;
            case Constants.BALL_VALUE_LEFT:
                statValues[mStatsFirstBall][StatUtils.STAT_LEFT + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT:
                statValues[mStatsFirstBall][StatUtils.STAT_RIGHT + offset]++;
                break;
            case Constants.BALL_VALUE_LEFT_CHOP:
                statValues[mStatsFirstBall][StatUtils.STAT_LEFT_CHOP + offset]++;
                statValues[mStatsFirstBall][StatUtils.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_CHOP:
                statValues[mStatsFirstBall][StatUtils.STAT_RIGHT_CHOP + offset]++;
                statValues[mStatsFirstBall][StatUtils.STAT_CHOP + offset]++;
                break;
            case Constants.BALL_VALUE_ACE:
                statValues[mStatsFirstBall][StatUtils.STAT_ACES + offset]++;
                break;
            case Constants.BALL_VALUE_LEFT_SPLIT:
                statValues[mStatsFirstBall][StatUtils.STAT_LEFT_SPLIT + offset]++;
                statValues[mStatsFirstBall][StatUtils.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_RIGHT_SPLIT:
                statValues[mStatsFirstBall][StatUtils.STAT_RIGHT_SPLIT + offset]++;
                statValues[mStatsFirstBall][StatUtils.STAT_SPLIT + offset]++;
                break;
            case Constants.BALL_VALUE_HEAD_PIN:
                statValues[mStatsFirstBall][StatUtils.STAT_HEAD_PINS + offset]++;
            default:
                // does nothing
        }
    }

    /**
     * Returns a cursor from database to load either bowler or league stats.
     *
     * @param shouldGetLeagueStats if true, league stats will be loaded. Bowler stats will be loaded otherwise
     * @return a cursor with rows relevant to mBowlerId or mLeagueId
     */
    private Cursor getBowlerOrLeagueCursor(boolean shouldGetLeagueStats) {
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
    private Cursor getSeriesCursor() {
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
    private Cursor getGameCursor() {
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

    /**
     * Displays the selected statistic as a graph.
     *
     * @param statCategory category of stat to display
     * @param statIndex index in category of stat to displau
     */
    private void openStatGraph(int statCategory, int statIndex) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null)
            mainActivity.openStatGraph(statCategory, statIndex);
    }
}
