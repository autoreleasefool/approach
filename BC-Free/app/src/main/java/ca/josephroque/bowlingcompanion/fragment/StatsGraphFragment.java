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
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.StatUtils;

/**
 * Created by Joseph Roque on 15-07-20. Manages the UI to display information about the stats in a
 * graph for a particular bowler
 */
public class StatsGraphFragment
        extends Fragment
        implements Theme.ChangeableTheme
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsGraphFragment";

    /** Represents the stat category being displayed. */
    private static final String ARG_STAT_CATEGORY = "arg_stat_cat";
    /** Represents the stat index being displayed. */
    private static final String ARG_STAT_INDEX = "arg_stat_index";

    /** LineChart to display statistics over time. */
    private LineChart mLineChartStats;
    /** TextView to display name of statistic. */
    private TextView mTextViewStat;

    /** The category of the stat being displayed. */
    private int mStatCategory;
    /** The index of the stat being displayed. */
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
        mTextViewStat = (TextView) rootView.findViewById(R.id.tv_stat_name);

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

            mainActivity.setActionBarTitle(titleToSet, true);
            new LoadStatsGraphTask(this).execute(statsToLoad);
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
    private static final class LoadStatsGraphTask
            extends AsyncTask<Byte, Void, LineData>
    {

        /** Weak reference to the parent fragment. */
        private WeakReference<StatsGraphFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadStatsGraphTask(StatsGraphFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected LineData doInBackground(Byte... statsToLoad)
        {
            StatsGraphFragment fragment = mFragment.get();
            if (fragment == null)
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;
            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            final byte toLoad = statsToLoad[0];
            Cursor cursor;

            switch (toLoad)
            {
                case StatsFragment.LOADING_LEAGUE_STATS:
                    cursor = fragment.getBowlerOrLeagueCursor(true);
                    break;
                case StatsFragment.LOADING_BOWLER_STATS:
                    cursor = fragment.getBowlerOrLeagueCursor(false);
                    break;
                default:
                    throw new IllegalArgumentException("invalid value for toLoad: " + toLoad
                            + ". must be between 0 and 1 (inclusive)");
            }

            List<Entry> listEntries = new ArrayList<>();
            List<String> listLabels = new ArrayList<>();
            compileGraphData(fragment, cursor, listEntries, listLabels);
            if (!cursor.isClosed())
                cursor.close();
            LineDataSet dataset = new LineDataSet(listEntries,
                    StatUtils.getStatName(fragment.mStatCategory, fragment.mStatIndex));

            List<LineDataSet> datasets = new ArrayList<>();
            datasets.add(dataset);

            return new LineData(listLabels, datasets);
        }

        @Override
        protected void onPostExecute(LineData result)
        {
            StatsGraphFragment fragment = mFragment.get();
            if (fragment == null || result == null)
                return;

            fragment.mTextViewStat.setText(StatUtils.getStatName(fragment.mStatCategory,
                    fragment.mStatIndex));
            fragment.mLineChartStats.setDescription(StatUtils.getStatName(fragment.mStatCategory,
                    fragment.mStatIndex));
            fragment.mLineChartStats.setData(result);
            fragment.mLineChartStats.invalidate();
        }

        /**
         * Invokes relevant methods for getting the graph data for a stat.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        private void compileGraphData(StatsGraphFragment fragment,
                                      Cursor cursor,
                                      List<Entry> listEntries,
                                      List<String> listLabels)
        {
            switch (fragment.mStatCategory)
            {
                case StatUtils.STAT_CATEGORY_GENERAL:
                    compileGeneralStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_FIRST_BALL:
                    compileFirstBallStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_FOULS:
                    compileFoulStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_PINS:
                    compilePinStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_AVERAGE_BY_GAME:
                    compileAverageStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_MATCH_PLAY:
                    compileMatchPlayStats(fragment, cursor, listEntries, listLabels);
                    break;
                case StatUtils.STAT_CATEGORY_OVERALL:
                    compileOverallStats(fragment, cursor, listEntries, listLabels);
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
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        private void compileGeneralStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listEntries,
                                         List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
            }
        }

        /**
         * Generates line chart data for first ball stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        private void compileFirstBallStats(StatsGraphFragment fragment,
                                           Cursor cursor,
                                           List<Entry> listEntries,
                                           List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
            }
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
                                      List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
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
        private void compilePinStats(StatsGraphFragment fragment,
                                     Cursor cursor,
                                     List<Entry> listEntries,
                                     List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
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
        private void compileAverageStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listEntries,
                                         List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
            }
        }

        /**
         * Generates line chart data for match play stats.
         *
         * @param fragment parent fragment
         * @param cursor bowler / league data
         * @param listEntries list of data entries
         * @param listLabels list of labels for x axis
         */
        private void compileMatchPlayStats(StatsGraphFragment fragment,
                                           Cursor cursor,
                                           List<Entry> listEntries,
                                           List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
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
        private void compileOverallStats(StatsGraphFragment fragment,
                                         Cursor cursor,
                                         List<Entry> listEntries,
                                         List<String> listLabels)
        {
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    cursor.moveToNext();
                }
            }
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
