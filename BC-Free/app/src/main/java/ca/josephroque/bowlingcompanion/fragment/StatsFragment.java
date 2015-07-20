package ca.josephroque.bowlingcompanion.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by Joseph Roque on 15-04-03.
 * <p/>
 * Manages the UI to display information about the stats for a particular bowler
 */
public class StatsFragment
        extends Fragment

{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsFragment";

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        if (savedInstanceState == null)
        {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.stats_container, StatsListFragment.newInstance())
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

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_stats, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();

        MenuItem menuItem = menu.findItem(R.id.action_stats_graph).setVisible(!drawerOpen
                && !mViewingGraph);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
        {
            drawable.mutate();
            //noinspection CheckStyle
            drawable.setAlpha(0x8A);
        }

        menuItem = menu.findItem(R.id.action_stats_list).setVisible(!drawerOpen
                && mViewingGraph);
        drawable = menuItem.getIcon();
        if (drawable != null)
        {
            drawable.mutate();
            //noinspection CheckStyle
            drawable.setAlpha(0x8A);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_stats_graph:
                mViewingGraph = true;
                getActivity().invalidateOptionsMenu();

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.stats_container, StatsGraphFragment.newInstance())
                        .commit();
                return true;
            case R.id.action_stats_list:
                mViewingGraph = false;
                getActivity().invalidateOptionsMenu();

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.stats_container, StatsListFragment.newInstance())
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_VIEWING_GRAPH, mViewingGraph);
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
