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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DateUtils;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.Score;
import ca.josephroque.bowlingcompanion.utilities.StatUtils;

/**
 * Created by Joseph Roque on 15-07-20. Manages the UI to display information about the stats in a graph for a
 * particular bowler
 */
public class StatsGraphFragment
        extends Fragment
        implements Theme.ChangeableTheme {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsGraphFragment";

    /** Represents the stat category being displayed. */
    private static final String ARG_STAT_CATEGORY = "arg_stat_cat";
    /** Represents the stat index being displayed. */
    private static final String ARG_STAT_INDEX = "arg_stat_index";
    /** Represents whether the stat calculation is accumulated over time. */
    private static final String ARG_STAT_ACCUMULATE = "arg_stat_accumulate";

    /** LineChart to display statistics over time. */
    private LineChart mLineChartStats;
    /** TextView to display name of statistic. */
    private TextView mTextViewStat;
    /** Switch to allow user to set stats show as accumulated over time, or be week by week. */
    private Switch mSwitchAccumulate;
    /** Provides context to the user of the purpose of {@code mSwitchAccumulate}. */
    private TextView mTextViewAccumulate;

    /** Contains views for navigating through stats. */
    private RelativeLayout mStatNavigation;
    /** Image view for user to advance to next stat graph. */
    private ImageView mImageViewNextStat;
    /** Image view for user to backtrack to previous stat graph. */
    private ImageView mImageViewPrevStat;
    /** Text view for user to advance to next stat graph. */
    private TextView mTextViewNextStat;
    /** Text view for user to backtrack to previous stat graph. */
    private TextView mTextViewPrevStat;

    /** The category of the stat being displayed. */
    private int mStatCategory;
    /** The index of the stat being displayed. */
    private int mStatIndex;
    /** Indicates if stats should be accumulated over time, or be calculated week by week. */
    private boolean mStatAccumulate = false;

    /**
     * Creates a new instance of {@code StatsGraphFragment} with the parameters provided.
     *
     * @param statCategory category of stat displayed in graph
     * @param statIndex index of stat displayed in graph
     * @return a new instance of StatsGraphFragment
     */
    public static StatsGraphFragment newInstance(int statCategory, int statIndex) {
        StatsGraphFragment fragment = new StatsGraphFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STAT_CATEGORY, statCategory);
        args.putInt(ARG_STAT_INDEX, statIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_graph, container, false);

        if (savedInstanceState != null) {
            mStatCategory = savedInstanceState.getInt(ARG_STAT_CATEGORY, 0);
            mStatIndex = savedInstanceState.getInt(ARG_STAT_INDEX, 0);
            mStatAccumulate = savedInstanceState.getBoolean(ARG_STAT_ACCUMULATE, false);
        } else {
            Bundle arguments = getArguments();
            mStatCategory = arguments.getInt(ARG_STAT_CATEGORY, 0);
            mStatIndex = arguments.getInt(ARG_STAT_INDEX, 0);
        }

        mLineChartStats = (LineChart) rootView.findViewById(R.id.chart_stats);
        mTextViewStat = (TextView) rootView.findViewById(R.id.tv_stat_name);
        mSwitchAccumulate = (Switch) rootView.findViewById(R.id.switch_stat_accumulate);
        mTextViewAccumulate = (TextView) rootView.findViewById(R.id.tv_stat_accumulate);
        setupNavigation(rootView);
        setupAccumulateSwitch();

        if (mStatAccumulate) {
            mSwitchAccumulate.setChecked(true);
            mTextViewAccumulate.setText(R.string.text_stats_accumulate);
        } else {
            mSwitchAccumulate.setChecked(false);
            mTextViewAccumulate.setText(R.string.text_stats_by_week);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setFloatingActionButtonState(0);
            mainActivity.setDrawerState(false);

            //Checks what type of stats should be displayed, depending
            //on what data is available in the parent activity at the time
            byte statsToLoad;
            int titleToSet;
            if (mainActivity.getLeagueId() == -1) {
                titleToSet = R.string.title_stats_bowler;
                statsToLoad = StatUtils.LOADING_BOWLER_STATS;
            } else {
                titleToSet = R.string.title_stats_league;
                statsToLoad = StatUtils.LOADING_LEAGUE_STATS;
            }

            mainActivity.setActionBarTitle(titleToSet, true);
            new LoadStatsGraphTask(this).execute(statsToLoad);
        }

        updateTheme();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_STAT_CATEGORY, mStatCategory);
        outState.putInt(ARG_STAT_INDEX, mStatIndex);
        outState.putBoolean(ARG_STAT_ACCUMULATE, mStatAccumulate);
    }

    @Override
    public void updateTheme() {
        if (mTextViewStat != null)
            mTextViewStat.setBackgroundColor(Theme.getHighlightThemeColor());
        if (mStatNavigation != null)
            mStatNavigation.setBackgroundColor(Theme.getPrimaryThemeColor());
    }

    /**
     * Sets up on click listeners for switch between accumulated or weekly stats.
     */
    private void setupAccumulateSwitch() {
        View.OnClickListener switchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewStat(!mStatAccumulate);
            }
        };

        mSwitchAccumulate.setOnClickListener(switchListener);
        mTextViewAccumulate.setOnClickListener(switchListener);
    }

    /**
     * Sets on click listeners for next / prev stat buttons and gets reference for {@code mStatNavigation}.
     *
     * @param rootView root view of fragment
     */
    private void setupNavigation(View rootView) {
        mStatNavigation = (RelativeLayout) rootView.findViewById(R.id.fl_stat_navigation);
        mImageViewNextStat = (ImageView) rootView.findViewById(R.id.iv_next_stat);
        mTextViewNextStat = (TextView) rootView.findViewById(R.id.tv_next_stat);
        mImageViewPrevStat = (ImageView) rootView.findViewById(R.id.iv_prev_stat);
        mTextViewPrevStat = (TextView) rootView.findViewById(R.id.tv_prev_stat);

        View.OnClickListener nextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextStatCategory = mStatCategory;
                int nextStatIndex = mStatIndex + 1;

                try {
                    StatUtils.getStatName(nextStatCategory, nextStatIndex, false);
                } catch (IllegalArgumentException ex) {
                    nextStatCategory++;
                    nextStatIndex = 0;
                }

                if (nextStatCategory > StatUtils.STAT_CATEGORY_OVERALL)
                    return;

                mStatCategory = nextStatCategory;
                mStatIndex = nextStatIndex;

                loadNewStat(false);
            }
        };

        View.OnClickListener prevClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextStatCategory = mStatCategory;
                int nextStatIndex = mStatIndex - 1;

                if (nextStatIndex < 0) {
                    nextStatCategory--;
                    if (nextStatCategory < 0)
                        return;

                    nextStatIndex = 0;
                    while (true) {
                        try {
                            StatUtils.getStatName(nextStatCategory, nextStatIndex, false);
                        } catch (IllegalArgumentException ex) {
                            break;
                        }
                        nextStatIndex++;
                    }
                    nextStatIndex -= 1;
                }

                mStatCategory = nextStatCategory;
                mStatIndex = nextStatIndex;

                loadNewStat(false);
            }
        };

        mImageViewNextStat.setOnClickListener(nextClickListener);
        mTextViewNextStat.setOnClickListener(nextClickListener);
        mImageViewPrevStat.setOnClickListener(prevClickListener);
        mTextViewPrevStat.setOnClickListener(prevClickListener);
    }

    /**
     * Loads a new stat to the graph.
     *
     * @param accumulate true if the stat should be accumulated over time.
     */
    private void loadNewStat(boolean accumulate) {
        byte statsToLoad;
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null)
            return;

        if (mainActivity.getLeagueId() == -1)
            statsToLoad = StatUtils.LOADING_BOWLER_STATS;
        else
            statsToLoad = StatUtils.LOADING_LEAGUE_STATS;

        mStatAccumulate = accumulate;
        mSwitchAccumulate.setChecked(mStatAccumulate);

        mTextViewAccumulate.setText((mStatAccumulate)
                ? R.string.text_stats_accumulate
                : R.string.text_stats_by_week);

        new LoadStatsGraphTask(StatsGraphFragment.this).execute(statsToLoad);
    }

    /**
     * Enables or disables UI elements.
     *
     * @param enable true to enable, false to disable
     */
    private void setUIEnabled(boolean enable) {
        mSwitchAccumulate.setEnabled(enable);
        mTextViewAccumulate.setEnabled(enable);
        mImageViewNextStat.setEnabled(enable);
        mTextViewNextStat.setEnabled(enable);
        mImageViewPrevStat.setEnabled(enable);
        mTextViewPrevStat.setEnabled(enable);
    }

    /**
     * Loads data from the database and calculates relevant stats depending on which type of stats are being loaded.
     */
    private static final class LoadStatsGraphTask
            extends AsyncTask<Byte, Void, LineData> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<StatsGraphFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadStatsGraphTask(StatsGraphFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            StatsGraphFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.setUIEnabled(false);
            int visibilityNext = (fragment.mStatIndex == StatUtils.STAT_NUMBER_OF_GAMES
                    && fragment.mStatCategory == StatUtils.STAT_CATEGORY_OVERALL)
                    ? View.GONE
                    : View.VISIBLE;
            int visibilityPrev = (fragment.mStatIndex == 0 && fragment.mStatCategory == 0)
                    ? View.GONE
                    : View.VISIBLE;

            fragment.mImageViewNextStat.setVisibility(visibilityNext);
            fragment.mTextViewNextStat.setVisibility(visibilityNext);
            fragment.mImageViewPrevStat.setVisibility(visibilityPrev);
            fragment.mTextViewPrevStat.setVisibility(visibilityPrev);
        }

        @Override
        protected LineData doInBackground(Byte... statsToLoad) {
            StatsGraphFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;
            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            final byte toLoad = statsToLoad[0];
            Cursor cursor;

            switch (toLoad) {
                case StatUtils.LOADING_LEAGUE_STATS:
                    cursor = fragment.getBowlerOrLeagueCursor(true);
                    break;
                case StatUtils.LOADING_BOWLER_STATS:
                    cursor = fragment.getBowlerOrLeagueCursor(false);
                    break;
                default:
                    throw new IllegalArgumentException("invalid value for toLoad: " + toLoad
                            + ". must be between 0 and 1 (inclusive)");
            }

            List<Entry> listChanceEntries = new ArrayList<>();
            List<Entry> listSuccessEntries = new ArrayList<>();
            List<String> listLabels = new ArrayList<>();
            compileGraphData(fragment, cursor, listChanceEntries, listSuccessEntries, listLabels);
            if (!cursor.isClosed())
                cursor.close();

            List<LineDataSet> datasets = new ArrayList<>();
            ValueFormatter valueFormatter = new DefaultValueFormatter(0);

            LineDataSet datasetSuccess = new LineDataSet(listSuccessEntries,
                    StatUtils.getStatName(fragment.mStatCategory, fragment.mStatIndex, false));
            LineDataSet datasetChances = null;
            String statChanceName = StatUtils.getStatName(fragment.mStatCategory,
                    fragment.mStatIndex,
                    true);
            if (statChanceName != null)
                datasetChances = new LineDataSet(listChanceEntries, statChanceName);


            if (datasetChances != null) {
                datasetChances.setValueFormatter(valueFormatter);
                datasetChances.setCircleColor(DisplayUtils.getColorResource(fragment.getResources(),
                        R.color.chance_data));
                datasetChances.setColor(DisplayUtils.getColorResource(fragment.getResources(), R.color.chance_data));
                datasets.add(datasetChances);
            }

            datasetSuccess.setValueFormatter(valueFormatter);
            datasetSuccess.setCircleColor(DisplayUtils.getColorResource(fragment.getResources(), R.color.success_data));
            datasetSuccess.setColor(DisplayUtils.getColorResource(fragment.getResources(), R.color.success_data));
            datasets.add(datasetSuccess);

            return new LineData(listLabels, datasets);
        }

        @Override
        protected void onPostExecute(LineData result) {
            StatsGraphFragment fragment = mFragment.get();
            if (fragment == null || result == null)
                return;

            // Setting  up UI
            final float textSizeBody = 14;
            fragment.mTextViewStat.setText(StatUtils.getStatName(fragment.mStatCategory,
                    fragment.mStatIndex, false));
            fragment.mLineChartStats.setDescription(StatUtils.getStatName(fragment.mStatCategory,
                    fragment.mStatIndex, false));
            fragment.mLineChartStats.setDescriptionTextSize(textSizeBody);
            fragment.mLineChartStats.getAxisLeft().setValueFormatter(new DefaultYAxisValueFormatter(0));

            // Styling legend
            Legend legend = fragment.mLineChartStats.getLegend();
            legend.setTextSize(textSizeBody);

            // Applying data
            fragment.mLineChartStats.setData(result);

            fragment.mLineChartStats.invalidate();
            fragment.setUIEnabled(true);
        }

        /**
         * Invokes relevant methods for getting the graph data for a stat.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listChanceEntries list of data entries for chances at increasing a stat
         * @param listSuccessEntries list of data entries for achieved stat values
         * @param listLabels list of labels for x axis
         */
        private void compileGraphData(StatsGraphFragment fragment,
                                      Cursor cursor,
                                      List<Entry> listChanceEntries,
                                      List<Entry> listSuccessEntries,
                                      List<String> listLabels) {
            switch (fragment.mStatCategory) {
                case StatUtils.STAT_CATEGORY_GENERAL:
                    compileGeneralStats(fragment,
                            cursor,
                            listChanceEntries,
                            listSuccessEntries,
                            listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_FIRST_BALL:
                    compileFirstBallStats(fragment,
                            cursor,
                            listChanceEntries,
                            listSuccessEntries,
                            listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_FOULS:
                    compileFoulStats(fragment, cursor, listSuccessEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_PINS:
                    compilePinStats(fragment, cursor, listSuccessEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_AVERAGE_BY_GAME:
                    compileAverageStats(fragment, cursor, listSuccessEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_MATCH_PLAY:
                    compileMatchPlayStats(fragment,
                            cursor,
                            listChanceEntries,
                            listSuccessEntries,
                            listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_OVERALL:
                    compileOverallStats(fragment, cursor, listSuccessEntries, listLabels);
                    break;
                default:
                    throw new IllegalStateException(
                            "invalid stat category: " + fragment.mStatCategory);
            }
        }

        /**
         * Generates line chart data for general stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listChanceEntries list of data entries for chances at increasing a stat
         * @param listSuccessEntries list of data entries for achieved stat values
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle") // I ain't even gonna bother
        private void compileGeneralStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listChanceEntries,
                                         List<Entry> listSuccessEntries,
                                         List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int totalShotsAtMiddle = 0, middleHits = 0;
            int spareChances = 0, spares = 0;
            int strikes = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        Entry chanceEntry;
                        Entry successEntry;
                        switch (fragment.mStatIndex) {
                            case StatUtils.STAT_MIDDLE_HIT:
                                chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                                successEntry = new Entry(middleHits, currentEntry);
                                break;
                            case StatUtils.STAT_SPARE_CONVERSIONS:
                                chanceEntry = new Entry(spareChances, currentEntry);
                                successEntry = new Entry(spares, currentEntry);
                                break;
                            case StatUtils.STAT_STRIKES:
                                chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                                successEntry = new Entry(strikes, currentEntry);
                                break;
                            default:
                                chanceEntry = null;
                                successEntry = null;
                                // does nothing
                        }
                        if (chanceEntry != null) {
                            listChanceEntries.add(chanceEntry);
                            listSuccessEntries.add(successEntry);
                            listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                            currentEntry++;
                        }

                        if (!fragment.mStatAccumulate) {
                            totalShotsAtMiddle = 0;
                            middleHits = 0;
                            spareChances = 0;
                            spares = 0;
                            strikes = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(
                            Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual || gameScore == 0) {
                        cursor.moveToNext();
                        continue;
                    }

                    boolean[][] pinState = new boolean[3][5];
                    for (byte i = 0; i < pinState.length; i++) {
                        pinState[i] = Score.ballIntToBoolean(cursor.getInt(cursor.getColumnIndex(
                                FrameEntry.COLUMN_PIN_STATE[i])));
                    }

                    int frameNumber = cursor.getInt(cursor.getColumnIndex(
                            FrameEntry.COLUMN_FRAME_NUMBER));
                    if (pinState[0][2])
                        middleHits++;
                    if (frameNumber == Constants.LAST_FRAME) {
                        totalShotsAtMiddle++;
                        if (Arrays.equals(pinState[0], Constants.FRAME_PINS_DOWN)) {
                            totalShotsAtMiddle++;
                            strikes++;
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN)) {
                                totalShotsAtMiddle++;
                                strikes++;
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    strikes++;
                            } else {
                                spareChances++;
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    spares++;
                            }
                        } else {
                            spareChances++;
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN)) {
                                totalShotsAtMiddle++;
                                spares++;
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    strikes++;
                            }
                        }
                    } else {
                        totalShotsAtMiddle++;
                        if (Arrays.equals(pinState[0], Constants.FRAME_PINS_DOWN))
                            strikes++;
                        else {
                            spareChances++;
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                spares++;
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                Entry chanceEntry;
                Entry successEntry;
                switch (fragment.mStatIndex) {
                    case StatUtils.STAT_MIDDLE_HIT:
                        chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                        successEntry = new Entry(middleHits, currentEntry);
                        break;
                    case StatUtils.STAT_SPARE_CONVERSIONS:
                        chanceEntry = new Entry(spareChances, currentEntry);
                        successEntry = new Entry(spares, currentEntry);
                        break;
                    case StatUtils.STAT_STRIKES:
                        chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                        successEntry = new Entry(strikes, currentEntry);
                        break;
                    default:
                        chanceEntry = null;
                        successEntry = null;
                        // does nothing
                }
                if (chanceEntry != null) {
                    listChanceEntries.add(chanceEntry);
                    listSuccessEntries.add(successEntry);
                    listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                }
            }
        }

        /**
         * Generates line chart data for first ball stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listChanceEntries list of data entries for chances at increasing a stat
         * @param listSuccessEntries list of data entries for achieved stat values
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle")
        private void compileFirstBallStats(StatsGraphFragment fragment,
                                           Cursor cursor,
                                           List<Entry> listChanceEntries,
                                           List<Entry> listSuccessEntries,
                                           List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int totalShotsAtMiddle = 0;
            //noinspection CheckStyle
            int[] firstBallStats = new int[20];

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        Entry chanceEntry;
                        Entry successEntry;
                        if (fragment.mStatIndex % 2 == 0) {
                            chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                            successEntry = new Entry(firstBallStats[fragment.mStatIndex],
                                    currentEntry);
                        } else {
                            chanceEntry = new Entry(firstBallStats[fragment.mStatIndex - 1],
                                    currentEntry);
                            successEntry = new Entry(firstBallStats[fragment.mStatIndex],
                                    currentEntry);
                        }

                        listChanceEntries.add(chanceEntry);
                        listSuccessEntries.add(successEntry);
                        listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                        currentEntry++;

                        if (!fragment.mStatAccumulate) {
                            totalShotsAtMiddle = 0;
                            for (int i = 0; i < firstBallStats.length; i++)
                                firstBallStats[i] = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(
                            Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual || gameScore == 0) {
                        cursor.moveToNext();
                        continue;
                    }

                    //noinspection CheckStyle
                    boolean[][] pinState = new boolean[3][5];
                    for (byte i = 0; i < pinState.length; i++) {
                        pinState[i] = Score.ballIntToBoolean(cursor.getInt(cursor.getColumnIndex(
                                FrameEntry.COLUMN_PIN_STATE[i])));
                    }

                    int frameNumber = cursor.getInt(cursor.getColumnIndex(
                            FrameEntry.COLUMN_FRAME_NUMBER));
                    if (frameNumber == Constants.NUMBER_OF_FRAMES) {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        increaseFirstBallStat(ballValue, firstBallStats, 0);

                        if (ballValue != 0) {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                increaseFirstBallStat(ballValue, firstBallStats, 1);
                        } else {
                            totalShotsAtMiddle++;
                            ballValue = getFirstBallValue(pinState[1]);
                            increaseFirstBallStat(ballValue, firstBallStats, 0);

                            if (ballValue != 0) {
                                if (Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    increaseFirstBallStat(ballValue, firstBallStats, 1);
                            } else {
                                totalShotsAtMiddle++;
                                ballValue = getFirstBallValue(pinState[2]);
                                increaseFirstBallStat(ballValue, firstBallStats, 0);
                            }
                        }
                    } else {
                        totalShotsAtMiddle++;
                        int ballValue = getFirstBallValue(pinState[0]);
                        increaseFirstBallStat(ballValue, firstBallStats, 0);

                        if (ballValue != 0) {
                            if (Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                increaseFirstBallStat(ballValue, firstBallStats, 1);
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                Entry chanceEntry;
                Entry successEntry;
                if (fragment.mStatIndex % 2 == 0) {
                    chanceEntry = new Entry(totalShotsAtMiddle, currentEntry);
                    successEntry = new Entry(firstBallStats[fragment.mStatIndex],
                            currentEntry);
                } else {
                    chanceEntry = new Entry(firstBallStats[fragment.mStatIndex - 1],
                            currentEntry);
                    successEntry = new Entry(firstBallStats[fragment.mStatIndex],
                            currentEntry);
                }

                listChanceEntries.add(chanceEntry);
                listSuccessEntries.add(successEntry);
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
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
         * Checks which situation has occurred by the state of the pins in ball.
         *
         * @param ball result of the pins after a ball was thrown
         * @param statValues stat values to update
         * @param offset indicates a spare was thrown and the spare count should be increased for a stat
         */
        private void increaseFirstBallStat(int ball, int[] statValues, int offset) {
            if (offset > 1 || offset < 0)
                throw new IllegalArgumentException("Offset must be either 0 or 1: " + offset);

            switch (ball) {
                case Constants.BALL_VALUE_LEFT:
                    statValues[StatUtils.STAT_LEFT + offset]++;
                    break;
                case Constants.BALL_VALUE_RIGHT:
                    statValues[StatUtils.STAT_RIGHT + offset]++;
                    break;
                case Constants.BALL_VALUE_LEFT_CHOP:
                    statValues[StatUtils.STAT_LEFT_CHOP + offset]++;
                    statValues[StatUtils.STAT_CHOP + offset]++;
                    break;
                case Constants.BALL_VALUE_RIGHT_CHOP:
                    statValues[StatUtils.STAT_RIGHT_CHOP + offset]++;
                    statValues[StatUtils.STAT_CHOP + offset]++;
                    break;
                case Constants.BALL_VALUE_ACE:
                    statValues[StatUtils.STAT_ACES + offset]++;
                    break;
                case Constants.BALL_VALUE_LEFT_SPLIT:
                    statValues[StatUtils.STAT_LEFT_SPLIT + offset]++;
                    statValues[StatUtils.STAT_SPLIT + offset]++;
                    break;
                case Constants.BALL_VALUE_RIGHT_SPLIT:
                    statValues[StatUtils.STAT_RIGHT_SPLIT + offset]++;
                    statValues[StatUtils.STAT_SPLIT + offset]++;
                    break;
                case Constants.BALL_VALUE_HEAD_PIN:
                    statValues[StatUtils.STAT_HEAD_PINS + offset]++;
                default:
                    // does nothing
            }
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
         * Generates line chart data for foul stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        private void compileFoulStats(StatsGraphFragment fragment,
                                      Cursor cursor,
                                      List<Entry> listEntries,
                                      List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int totalFouls = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        listEntries.add(new Entry(totalFouls, currentEntry));
                        listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                        currentEntry++;

                        if (!fragment.mStatAccumulate)
                            totalFouls = 0;
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(
                            Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual || gameScore == 0) {
                        cursor.moveToNext();
                        continue;
                    }

                    String frameFouls = Score.foulIntToString(cursor.getInt(cursor.getColumnIndex(
                            Contract.FrameEntry.COLUMN_FOULS)));
                    //noinspection CheckStyle
                    for (byte i = 1; i <= 3; i++) {
                        if (frameFouls.contains(String.valueOf(i)))
                            totalFouls++;
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                listEntries.add(new Entry(totalFouls, currentEntry));
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
            }
        }

        /**
         * Generates line chart data for pin stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle")
        private void compilePinStats(StatsGraphFragment fragment,
                                     Cursor cursor,
                                     List<Entry> listEntries,
                                     List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int totalPinsLeft = 0, numberOfGames = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;

                        Entry entry;
                        if (fragment.mStatIndex == StatUtils.STAT_PINS_LEFT)
                            entry = new Entry(totalPinsLeft, currentEntry);
                        else if (numberOfGames > 0)
                            entry = new Entry(totalPinsLeft / numberOfGames, currentEntry);
                        else
                            entry = new Entry(0, currentEntry);

                        listEntries.add(entry);
                        listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                        currentEntry++;

                        if (!fragment.mStatAccumulate) {
                            totalPinsLeft = 0;
                            numberOfGames = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    boolean gameIsManual = (cursor.getInt(cursor.getColumnIndex(
                            Contract.GameEntry.COLUMN_IS_MANUAL)) == 1);
                    if (gameIsManual || gameScore == 0) {
                        cursor.moveToNext();
                        continue;
                    }

                    numberOfGames++;
                    //noinspection CheckStyle
                    boolean[][] pinState = new boolean[3][5];
                    for (byte i = 0; i < pinState.length; i++) {
                        pinState[i] = Score.ballIntToBoolean(cursor.getInt(cursor.getColumnIndex(
                                FrameEntry.COLUMN_PIN_STATE[i])));
                    }

                    int frameNumber = cursor.getInt(cursor.getColumnIndex(
                            FrameEntry.COLUMN_FRAME_NUMBER));

                    if (frameNumber == Constants.NUMBER_OF_FRAMES) {
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != 0) {
                            if (!Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN))
                                totalPinsLeft += countPinsLeftStanding(pinState[2]);
                        } else {
                            ballValue = getFirstBallValue(pinState[1]);
                            if (ballValue != 0) {
                                if (!Arrays.equals(pinState[2], Constants.FRAME_PINS_DOWN))
                                    totalPinsLeft += countPinsLeftStanding(pinState[2]);
                            }
                        }
                    } else {
                        int ballValue = getFirstBallValue(pinState[0]);
                        if (ballValue != 0) {
                            if (!Arrays.equals(pinState[1], Constants.FRAME_PINS_DOWN)) {
                                totalPinsLeft += countPinsLeftStanding(pinState[2]);
                            }
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                Entry entry;
                if (fragment.mStatIndex == StatUtils.STAT_PINS_LEFT)
                    entry = new Entry(totalPinsLeft, currentEntry);
                else if (numberOfGames > 0)
                    entry = new Entry(totalPinsLeft / numberOfGames, currentEntry);
                else
                    entry = new Entry(0, currentEntry);

                listEntries.add(entry);
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
            }
        }

        /**
         * Generates line chart data for game average stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle")
        private void compileAverageStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listEntries,
                                         List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int numberOfGames = 0, totalPinfall = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        if (numberOfGames > 0)
                            listEntries.add(new Entry(totalPinfall / numberOfGames, currentEntry));
                        else
                            listEntries.add(new Entry(0, currentEntry));
                        listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                        currentEntry++;

                        if (!fragment.mStatAccumulate) {
                            numberOfGames = 0;
                            totalPinfall = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    int gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER));
                    int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER));

                    if (frameNumber == 1 && gameNumber - 1 == fragment.mStatIndex) {
                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));

                        if (gameScore > 0) {
                            numberOfGames++;
                            totalPinfall += gameScore;
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                if (numberOfGames > 0)
                    listEntries.add(new Entry(totalPinfall / numberOfGames, currentEntry));
                else
                    listEntries.add(new Entry(0, currentEntry));
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
            }
        }

        /**
         * Generates line chart data for match play stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listChanceEntries list of data entries for chances at increasing a stat
         * @param listSuccessEntries list of data entries for achieved stat values
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle")
        private void compileMatchPlayStats(StatsGraphFragment fragment,
                                           Cursor cursor,
                                           List<Entry> listChanceEntries,
                                           List<Entry> listSuccessEntries,
                                           List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int matchResults = 0, totalMatchPlayGames = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        if (totalMatchPlayGames > 0)
                            listSuccessEntries.add(new Entry(matchResults / totalMatchPlayGames,
                                    currentEntry));
                        else
                            listSuccessEntries.add(new Entry(0, currentEntry));
                        listChanceEntries.add(new Entry(totalMatchPlayGames, currentEntry));
                        listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                        currentEntry++;

                        if (!fragment.mStatAccumulate) {
                            matchResults = 0;
                            totalMatchPlayGames = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    int frameNumber = cursor.getInt(cursor.getColumnIndex(
                            FrameEntry.COLUMN_FRAME_NUMBER));

                    if (frameNumber == 1) {
                        int matchPlay = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));
                        if (matchPlay > 0) {
                            totalMatchPlayGames++;
                            if (matchPlay - 1 == fragment.mStatIndex)
                                matchResults++;
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                if (totalMatchPlayGames > 0)
                    listSuccessEntries.add(new Entry(matchResults / totalMatchPlayGames,
                            currentEntry));
                else
                    listSuccessEntries.add(new Entry(0, currentEntry));
                listChanceEntries.add(new Entry(totalMatchPlayGames, currentEntry));
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
            }
        }

        /**
         * Generates line chart data for overall stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        @SuppressWarnings("CheckStyle") // I ain't even gonna bother
        private void compileOverallStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listEntries,
                                         List<String> listLabels) {
            Calendar lastEntryDate = null;
            Calendar lastLabelDate = null;
            Calendar currentDate = null;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);
            boolean addLabelOnDateChange = false;
            int numberOfGames = 0, totalPinfall = 0;
            int highSingle = 0, highSeries = 0, currentSeries = 0;

            int currentEntry = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Date entryDate = DateUtils.parseEntryDate(cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)));
                    if (entryDate == null)
                        return;

                    currentDate = DateUtils.getCalendarAtMidnight(entryDate);

                    if (addLabelOnDateChange
                            && currentDate.getTimeInMillis() != lastEntryDate.getTimeInMillis()) {
                        if (currentSeries > highSeries)
                            highSeries = currentSeries;

                        addLabelOnDateChange = false;
                        lastLabelDate = lastEntryDate;
                        Entry entry;
                        switch (fragment.mStatIndex) {
                            case StatUtils.STAT_AVERAGE:
                                if (numberOfGames > 0)
                                    entry = new Entry(totalPinfall / numberOfGames, currentEntry);
                                else
                                    entry = new Entry(0, currentEntry);
                                break;
                            case StatUtils.STAT_HIGH_SINGLE:
                                entry = new Entry(highSingle, currentEntry);
                                break;
                            case StatUtils.STAT_HIGH_SERIES:
                                entry = new Entry(highSeries, currentEntry);
                                break;
                            case StatUtils.STAT_TOTAL_PINS:
                                entry = new Entry(totalPinfall, currentEntry);
                                break;
                            case StatUtils.STAT_NUMBER_OF_GAMES:
                                entry = new Entry(numberOfGames, currentEntry);
                                break;
                            default:
                                entry = null;
                        }

                        if (entry != null) {
                            listEntries.add(entry);
                            listLabels.add(dateFormat.format(lastEntryDate.getTime()));
                            currentEntry++;
                        }

                        if (!fragment.mStatAccumulate) {
                            numberOfGames = 0;
                            totalPinfall = 0;
                            highSeries = 0;
                            highSingle = 0;
                            currentSeries = 0;
                        }
                    }

                    if (lastLabelDate == null
                            || lastLabelDate.getTimeInMillis()
                            <= currentDate.getTimeInMillis() + DateUtils.MILLIS_ONE_WEEK)
                        addLabelOnDateChange = true;
                    lastEntryDate = currentDate;

                    int gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER));
                    int frameNumber = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER));
                    if (frameNumber == 1) {
                        if (gameNumber == 1) {
                            if (currentSeries > highSeries)
                                highSeries = currentSeries;
                            currentSeries = 0;
                        }

                        short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));

                        if (gameScore > 0) {
                            numberOfGames++;
                            totalPinfall += gameScore;
                            currentSeries += gameScore;
                            if (gameScore > highSingle)
                                highSingle = gameScore;
                        }
                    }

                    cursor.moveToNext();
                }
            }

            if (lastEntryDate != null && (lastLabelDate == null
                    || currentDate.getTimeInMillis() != lastLabelDate.getTimeInMillis())) {
                if (numberOfGames > 0)
                    listEntries.add(new Entry(totalPinfall / numberOfGames, currentEntry));
                else
                    listEntries.add(new Entry(0, currentEntry));
                listLabels.add(dateFormat.format(lastEntryDate.getTime()));
            }
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
                PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isEventIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_EVENTS, true);
        boolean isOpenIncluded = preferences.getBoolean(Constants.KEY_INCLUDE_OPEN, true);
        SQLiteDatabase database = DatabaseHelper.getInstance(getContext()).getReadableDatabase();

        String rawStatsQuery = "SELECT "
                + SeriesEntry.COLUMN_SERIES_DATE + ", "
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
                + " ON league." + LeagueEntry._ID + "=series."
                + SeriesEntry.COLUMN_LEAGUE_ID
                + " INNER JOIN " + GameEntry.TABLE_NAME + " AS game"
                + " ON series." + SeriesEntry._ID + "=game."
                + GameEntry.COLUMN_SERIES_ID
                + " INNER JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                + " ON game." + GameEntry._ID + "=frame."
                + FrameEntry.COLUMN_GAME_ID
                + ((shouldGetLeagueStats)
                ? " WHERE league." + LeagueEntry._ID + "=?"
                : " WHERE league." + LeagueEntry.COLUMN_BOWLER_ID + "=?")
                + " AND " + ((!shouldGetLeagueStats && !isEventIncluded)
                ? LeagueEntry.COLUMN_IS_EVENT
                : "'0'") + "=?"
                + " AND " + ((!shouldGetLeagueStats && !isOpenIncluded)
                ? LeagueEntry.COLUMN_LEAGUE_NAME + "!"
                : "'0'") + "=?"
                + " ORDER BY series." + SeriesEntry.COLUMN_SERIES_DATE
                + ", series." + SeriesEntry._ID
                + ", game." + GameEntry.COLUMN_GAME_NUMBER
                + ", frame." + FrameEntry.COLUMN_FRAME_NUMBER;

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
}
