package ca.josephroque.bowlingcompanion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-04-03. Manages the UI to display information about the stats for a
 * particular bowler
 */
public class StatsFragment
        extends Fragment
        implements StatsListFragment.StatClickListener
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsFragment";

    /** Represents a boolean indicating if the user is viewing the data in a graph. */
    private static final String ARG_VIEWING_GRAPH = "arg_viewing_graph";

    /** Indicates all the stats related to the specified bowler should be loaded. */
    public static final byte LOADING_BOWLER_STATS = 0;
    /** Indicates all the stats related to the specified league should be loaded. */
    public static final byte LOADING_LEAGUE_STATS = 1;
    /** Indicates all the stats related to the specified series should be loaded. */
    public static final byte LOADING_SERIES_STATS = 2;
    /** Indicates only the stats related to the specified game should be loaded. */
    public static final byte LOADING_GAME_STATS = 3;

    /** Indicates if the user is currently viewing the data in a graph or not. */
    private boolean mViewingGraph = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        if (savedInstanceState == null)
        {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.stats_container, StatsListFragment.newInstance(this))
                    .commit();
        }
        else
        {
            mViewingGraph = savedInstanceState.getBoolean(ARG_VIEWING_GRAPH, false);
        }

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
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_VIEWING_GRAPH, mViewingGraph);
    }

    @Override
    public void onStatClicked(int statCategory, int statIndex)
    {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.stats_container,
                        StatsGraphFragment.newInstance(statCategory, statIndex))
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Creates a new instance of StatsFragment and returns it.
     *
     * @return new instance of StatsFragment
     */
    public static StatsFragment newInstance()
    {
        return new StatsFragment();
    }
}
