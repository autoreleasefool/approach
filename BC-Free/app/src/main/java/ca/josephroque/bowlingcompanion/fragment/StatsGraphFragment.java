package ca.josephroque.bowlingcompanion.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * A simple {@link Fragment} subclass.
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
            new LoadStatsGraphTask().execute(statsToLoad);
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
    private class LoadStatsGraphTask
            extends AsyncTask<Byte, Void, List<?>[]>
    {
        @Override
        protected List<?>[] doInBackground(Byte... statsToLoad)
        {
            return null;
        }
    }
}
