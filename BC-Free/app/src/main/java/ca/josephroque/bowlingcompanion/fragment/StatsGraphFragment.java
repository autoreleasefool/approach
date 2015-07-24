package ca.josephroque.bowlingcompanion.fragment;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

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
