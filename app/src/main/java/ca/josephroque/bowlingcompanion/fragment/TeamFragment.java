package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.TeamAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;
import ca.josephroque.bowlingcompanion.wrapper.Bowler;
import ca.josephroque.bowlingcompanion.wrapper.LeagueEvent;

/**
 * Created by Joseph Roque on 2015-12-29. Provides a method for users to select multiple bowlers at once to record games
 * for. This prevents having to return to the home menu and navigate through bowlers to record 2 or more games at once.
 */
public class TeamFragment
        extends Fragment
        implements FloatingActionButtonHandler,
        TeamAdapter.TeamEventHandler {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TeamFragment";

    /** Identifier for boolean indicating if a league or event team is being created. */
    private static final String ARG_EVENT_TEAM = "arg_event_team";

    /** Instance of the callback interface. */
    private TeamCallback mCallback;

    /** View to display bowlers to the user. */
    private RecyclerView mRecyclerViewBowlers;
    /** View to display the leagues of a selected bowler to the user. */
    private RecyclerView mRecyclerViewLeagues;
    /** TextView to display the number of bowlers selected for the team so far. */
    private TextView mTextViewTeamSize;

    /** Organizes the list of bowlers to display in the {@link android.support.v7.widget.RecyclerView}. */
    private TeamAdapter<Bowler> mAdapterBowlers;
    /** Organizes the list of bowler's leagues to display in the {@link android.support.v7.widget.RecyclerView}. */
    private TeamAdapter<LeagueEvent> mAdapterLeagues;

    /** The list of bowlers available to be added to or removed from the team. */
    private List<Bowler> mAvailableBowlers;
    /**
     * A map of bowlers which are available to be added to the team. If the bowler maps to a boolean which is {@code
     * true}, then the bowler has been selected and added to the game.
     */
    private Map<Bowler, Boolean> mSelectedBowlers;

    /**
     * A list of {@link ca.josephroque.bowlingcompanion.wrapper.LeagueEvent} items belonging to the bowlers to selected
     * or ensure that the name of a new event does not interfere with them.
     */
    private Map<Bowler, ArrayList<LeagueEvent>> mBowlerLeagueEvents;
    /** A list of the leagues selected to be used by a bowler. */
    private Map<Bowler, LeagueEvent> mSelectedLeagues;

    /** The last bowler selected, for whom a league is being selected. */
    private Bowler mBowlerSelected;
    /** The list of leagues belonging to the last bowler selected. */
    private List<LeagueEvent> mBowlerLeagues;


    /** Indicates if the user is forming a team for an event or using a league from each bowler. */
    private boolean mCreatingEventTeam;
    /**
     * If this fragment is being used to create an event team, then this represents the name that will be used for the
     * event. It cannot be the same name as any event that a selected bowler already has.
     */
    private String mEventName;
    /**
     * Either the number of games for the event being made, or the number of games which a single series in a selected
     * bowler's league must contain.
     */
    private byte mNumberOfTeamGames;
    /** Number of bowlers that have been selected for the team so far. */
    private byte mTotalBowlersSelected = 0;

    /** Indicates if the button to finish creating the team is enabled or not. */
    private boolean mFinishButtonEnabled = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         * This makes sure the container Activity has implemented
         * the callback interface. If not, an exception is thrown
         */
        try {
            mCallback = (TeamCallback) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement TeamCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        // Retrieve saved instance state, or the arguments used to creating the fragment
        if (savedInstanceState != null) {
            mCreatingEventTeam = savedInstanceState.getBoolean(ARG_EVENT_TEAM, true);
            mEventName = savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE, null);
            mNumberOfTeamGames = savedInstanceState.getByte(Constants.EXTRA_NUMBER_OF_GAMES, (byte) -1);
        } else {
            Bundle args = getArguments();
            mCreatingEventTeam = args.getBoolean(ARG_EVENT_TEAM, true);
            mEventName = args.getString(Constants.EXTRA_NAME_LEAGUE, null);
            mNumberOfTeamGames = args.getByte(Constants.EXTRA_NUMBER_OF_GAMES, (byte) -1);
        }

        // Throw exception if invalid value provided
        if (mCreatingEventTeam && TextUtils.isEmpty(mEventName))
            throw new IllegalStateException("An empty event name was provided.");
        else if (mNumberOfTeamGames <= 0
                || (mCreatingEventTeam && mNumberOfTeamGames > Constants.MAX_NUMBER_EVENT_GAMES)
                || (!mCreatingEventTeam && mNumberOfTeamGames > Constants.MAX_NUMBER_LEAGUE_GAMES)) {
            int maxGames = (mCreatingEventTeam)
                    ? Constants.MAX_NUMBER_EVENT_GAMES
                    : Constants.MAX_NUMBER_LEAGUE_GAMES;
            throw new IllegalStateException(String.format(
                    "An invalid number of games was provided. There should be between %d and %d and there are %d.",
                    1, maxGames, mNumberOfTeamGames));
        }

        // Initializing lists to track team
        mAvailableBowlers = new ArrayList<>();
        mSelectedBowlers = new HashMap<>();
        mBowlerLeagueEvents = new HashMap<>();
        mSelectedLeagues = new HashMap<>();
        mBowlerLeagues = new ArrayList<>();

        // Setting up recycler views to show the available bowlers
        mRecyclerViewBowlers = (RecyclerView) rootView.findViewById(R.id.rv_bowlers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewBowlers.setLayoutManager(layoutManager);
        mAdapterBowlers = new TeamAdapter<>(this, mAvailableBowlers, mSelectedBowlers, TeamAdapter.DATA_BOWLERS);
        mRecyclerViewBowlers.setAdapter(mAdapterBowlers);

        mRecyclerViewLeagues = (RecyclerView) rootView.findViewById(R.id.rv_leagues);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewLeagues.setLayoutManager(layoutManager);
        mAdapterLeagues = new TeamAdapter<>(this, mBowlerLeagues, null, TeamAdapter.DATA_LEAGUES_EVENTS);
        mRecyclerViewLeagues.setAdapter(mAdapterLeagues);

        // Setting up text view to display team size
        mTextViewTeamSize = (TextView) rootView.findViewById(R.id.tv_team_size);
        mTotalBowlersSelected = -1;
        updateTeamSize(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setActionBarTitle(R.string.title_fragment_team, true);
            mainActivity.setDrawerState(false);
        }

        new LoadTeamBowlersTask(this).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ARG_EVENT_TEAM, mCreatingEventTeam);
        outState.putString(Constants.EXTRA_NAME_LEAGUE, mEventName);
        outState.putByte(Constants.EXTRA_NUMBER_OF_GAMES, mNumberOfTeamGames);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_team, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_cancel_team).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel_team:
                cancelTeamBuilding();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFabClick() {
        if (mFinishButtonEnabled) {
            if (mCallback != null && mTotalBowlersSelected > 0 && mTotalBowlersSelected <= Constants.MAX_TEAM_MEMBERS) {
                List<Bowler> selectedBowlers = new ArrayList<>();
                for (Bowler key : mSelectedBowlers.keySet()) {
                    if (mSelectedBowlers.get(key))
                        selectedBowlers.add(key);
                }
                if (mCreatingEventTeam)
                    mCallback.onEventTeamFinished(mEventName, mNumberOfTeamGames, selectedBowlers);
                else
                    mCallback.onLeagueTeamFinished(mNumberOfTeamGames, selectedBowlers, mSelectedLeagues);
            }
        } else {
            cancelTeamBuilding();
        }
    }

    @Override
    public void onSecondaryFabClick() {
        cancelTeamBuilding();
    }

    @Override
    public void onItemSelected(byte type, int position) {
        if (type == TeamAdapter.DATA_BOWLERS) {
            Bowler selectedBowler = mAvailableBowlers.get(position);
            Boolean wasSelected = mSelectedBowlers.get(selectedBowler);

            if (wasSelected != null && wasSelected) {
                // If the bowler was previously selected, remove them from the team
                mSelectedBowlers.put(selectedBowler, false);
                mSelectedLeagues.remove(selectedBowler);
                mAdapterBowlers.notifyItemChanged(position);
                updateTeamSize(false);
            } else {
                if (mCreatingEventTeam) {
                    if (checkForIdenticalEvent(mBowlerLeagueEvents.get(selectedBowler)))
                        // The bowler has an event with the name, so inform the user and offer to change the event name
                        showIdenticalEventDialog(selectedBowler);
                    else {
                        // Add the bowler to the list of bowlers which have been selected
                        mSelectedBowlers.put(selectedBowler, true);
                        mAdapterBowlers.notifyItemChanged(position);
                        updateTeamSize(true);
                    }
                } else {
                    showBowlerLeagues(selectedBowler);
                }
            }
        } else if (type == TeamAdapter.DATA_LEAGUES_EVENTS) {
            mSelectedLeagues.put(mBowlerSelected, mBowlerLeagues.get(position));
            showBowlers();
        }
    }

    /**
     * Iterates through {@code leagueEvents} and compares their names to {@code mEventName}. If any of the names are
     * equal (ignoring case), then the method returns {@code true}, or {@code false} otherwise.
     *
     * @param leagueEvents list of a bowler's events to check
     * @return {@code true} if the name of any instance in {@code leagueEvents} is equal to {@code mEventName}
     */
    private boolean checkForIdenticalEvent(List<LeagueEvent> leagueEvents) {
        for (LeagueEvent leagueEvent : leagueEvents) {
            if (mEventName.equalsIgnoreCase(leagueEvent.getLeagueEventName()))
                return true;
        }

        return false;
    }

    /**
     * Displays a dialog informing the user that the bowler they selected has an event with a name identical to the new
     * event. Additionally, this method calls another method which will rename the event if the user chooses, or
     * deselects the bowler.
     *
     * @param selectedBowler bowler to deselect if the user does not want to change the name of the event
     */
    private void showIdenticalEventDialog(final Bowler selectedBowler) {
        final StringBuilder newEventName = new StringBuilder();
        if (mEventName.length() >= Constants.NAME_MAX_LENGTH - 1)
            newEventName.append(mEventName.substring(0, Constants.NAME_MAX_LENGTH - 2));
        else
            newEventName.append(mEventName);
        newEventName.append(" 2");

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    mEventName = newEventName.toString();
                    mSelectedBowlers.put(selectedBowler, true);
                } else {
                    mSelectedBowlers.remove(selectedBowler);
                    mSelectedLeagues.remove(selectedBowler);
                }
                mAdapterBowlers.notifyItemChanged(mAvailableBowlers.indexOf(selectedBowler));
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_identical_event_name_title)
                .setMessage(String.format(getResources().getString(R.string.text_identical_event_name_placeholder),
                        mEventName))
                .setPositiveButton(R.string.dialog_rename, onClickListener)
                .setNegativeButton(R.string.dialog_remove_bowler, onClickListener)
                .create()
                .show();
    }

    /**
     * Displays a dialog informing the user they have selected too many bowlers and must deselect one or more before
     * they can finish.
     */
    private void showOversizeTeamDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.text_oversize_team_title)
                .setMessage(String.format(getResources().getString(R.string.text_oversize_team_placeholder),
                        mTotalBowlersSelected,
                        Constants.MAX_TEAM_MEMBERS))
                .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Presents a {@link android.support.v7.widget.RecyclerView} to display the list of leagues belonging to a bowler.
     *
     * @param bowler bowler to display the leagues of
     */
    private void showBowlerLeagues(Bowler bowler) {
        // Get the selected bowler's leagues
        mBowlerSelected = bowler;
        List<LeagueEvent> leagues = mBowlerLeagueEvents.get(mBowlerSelected);

        // Show the leagues of the new bowler
        mBowlerLeagues.clear();
        mBowlerLeagues.addAll(leagues);
        mAdapterLeagues.notifyDataSetChanged();

        // Show the appropriate list
        mRecyclerViewLeagues.setVisibility(View.VISIBLE);
        mRecyclerViewBowlers.setVisibility(View.GONE);
    }

    /**
     * Hides the leagues and shows the list of bowlers.
     */
    private void showBowlers() {
        // Clear the selected bowler
        mBowlerSelected = null;
        mBowlerLeagues.clear();

        // Show the appropriate list
        mRecyclerViewBowlers.setVisibility(View.VISIBLE);
        mRecyclerViewLeagues.setVisibility(View.GONE);
    }

    /**
     * Increments or decrements the size of the team and sets a text view displaying the size of the team the user has
     * chosen so far.
     *
     * @param increment {@code true} to increase the size of the team by 1, {@code false} to decrease it
     */
    private void updateTeamSize(boolean increment) {
        // Increment or decrement team size and check if the size of the team is still valid
        if (increment) {
            mTotalBowlersSelected++;
            if (mTotalBowlersSelected > Constants.MAX_TEAM_MEMBERS)
                showOversizeTeamDialog();
        } else {
            mTotalBowlersSelected--;
            if (mTotalBowlersSelected < 0)
                throw new IllegalStateException("Cannot have a negative number of bowlers.");
        }

        mTextViewTeamSize.setText(String.format(getResources().getString(R.string.text_team_size_so_far_placeholder),
                mTotalBowlersSelected,
                Constants.MAX_TEAM_MEMBERS));
        setFinishButtonEnabled(mTotalBowlersSelected > 0 && mTotalBowlersSelected <= Constants.MAX_TEAM_MEMBERS);
    }

    /**
     * Adjusts the floating action buttons to either display a "finish" button and a "cancel" button, or only a "cancel"
     * button.
     *
     * @param enabled {@code true} to show to "finish" button, {@code false} to only show a "cancel" button
     */
    private void setFinishButtonEnabled(boolean enabled) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mFinishButtonEnabled = enabled;

        if (mFinishButtonEnabled) {
            mainActivity.setFloatingActionButtonState(R.drawable.ic_done_black_24dp,
                    Theme.getPrimaryThemeColor(),
                    Theme.getTertiaryThemeColor(),
                    R.drawable.ic_clear_black_24dp,
                    DisplayUtils.getColorResource(getResources(), R.color.cancel_primary),
                    DisplayUtils.getColorResource(getResources(), R.color.cancel_secondary));
        } else {
            mainActivity.setFloatingActionButtonState(R.drawable.ic_clear_black_24dp,
                    DisplayUtils.getColorResource(getResources(), R.color.cancel_primary),
                    DisplayUtils.getColorResource(getResources(), R.color.cancel_secondary),
                    0,
                    0,
                    0);
        }
    }

    /**
     * Returns to the previous fragment.
     */
    private void cancelTeamBuilding() {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    /**
     * Creates a new instance of this fragment to display. This constructor assumes an event team is being created.
     *
     * @param eventName name of the event to create
     * @param numberOfGames number of games in the event
     * @return new instance of {@link ca.josephroque.bowlingcompanion.fragment.TeamFragment}
     */
    public static TeamFragment newInstance(String eventName, byte numberOfGames) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_EVENT_TEAM, true);
        args.putString(Constants.EXTRA_NAME_LEAGUE, eventName);
        args.putByte(Constants.EXTRA_NUMBER_OF_GAMES, numberOfGames);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new instance of this fragment to display. This constructor assumes a league team is being created.
     *
     * @param numberOfGames number of games in the league
     * @return new instance of {@link ca.josephroque.bowlingcompanion.fragment.TeamFragment}
     */
    public static TeamFragment newInstance(byte numberOfGames) {
        TeamFragment fragment = new TeamFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_EVENT_TEAM, false);
        args.putByte(Constants.EXTRA_NUMBER_OF_GAMES, numberOfGames);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Callback interface for user events.
     */
    public interface TeamCallback {

        /**
         * Invoked when the user opts to finish creating their event team.
         *
         * @param eventName name of the new event
         * @param numberOfGames number of games for the new event
         * @param selectedBowlers bowlers selected for the team
         */
        void onEventTeamFinished(String eventName, byte numberOfGames, List<Bowler> selectedBowlers);

        /**
         * Invoked when the user opts to finish creating their league team.
         *
         * @param numberOfGames number of games that the leagues must have
         * @param selectedBowlers bowlers selected for the team
         * @param selectedLeagues the league selected for each bowler
         */
        void onLeagueTeamFinished(byte numberOfGames,
                                  List<Bowler> selectedBowlers,
                                  Map<Bowler, LeagueEvent> selectedLeagues);
    }

    /**
     * Loads names of bowlers and their leagues or events, and adds them to recycler view.
     */
    private static final class LoadTeamBowlersTask
            extends AsyncTask<Void, Void, Void> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<TeamFragment> mFragment;

        /** A list of bowlers loaded from the database. */
        private List<Bowler> mLoadedBowlers;
        /** A list of the leagues belonging to each bowler, loaded from the database. */
        private HashMap<Bowler, ArrayList<LeagueEvent>> mLoadedBowlerLeagueEvents;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadTeamBowlersTask(TeamFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            TeamFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mAvailableBowlers.clear();
            fragment.mSelectedBowlers.clear();
            fragment.mBowlerLeagueEvents.clear();
            fragment.mSelectedLeagues.clear();

            fragment.mAdapterBowlers.notifyDataSetChanged();
            fragment.mAdapterLeagues.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {
            TeamFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            // Ensure the database is up to date
            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            // Data structures
            mLoadedBowlers = new ArrayList<>();
            mLoadedBowlerLeagueEvents = new HashMap<>();

            SQLiteDatabase database = DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            Cursor cursor = getBowlersAndLeaguesOrEvents(database,
                    fragment.mCreatingEventTeam,
                    fragment.mNumberOfTeamGames);

            long lastBowlerId = -1;
            Bowler currentBowler = null;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long bowlerId = cursor.getLong(cursor.getColumnIndex("bid"));
                    if (bowlerId != lastBowlerId) {
                        String bowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                        currentBowler = new Bowler(bowlerId, bowlerName, (short) -1);
                        mLoadedBowlers.add(currentBowler);
                        mLoadedBowlerLeagueEvents.put(currentBowler, new ArrayList<LeagueEvent>());
                    }

                    if (!cursor.isNull(cursor.getColumnIndex("lid"))) {
                        long leagueEventId = cursor.getLong(cursor.getColumnIndex("lid"));
                        String leagueEventName
                                = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));

                        LeagueEvent leagueEvent = new LeagueEvent(leagueEventId,
                                leagueEventName,
                                fragment.mCreatingEventTeam,
                                (short) -1,
                                (short) -1,
                                -1,
                                fragment.mNumberOfTeamGames);
                        mLoadedBowlerLeagueEvents.get(currentBowler).add(leagueEvent);
                    }

                    cursor.moveToNext();
                }
            }

            // Close the cursor when finished with it, so it does not retain unnecessary resources
            cursor.close();

            return null;
        }

        /**
         * Queries the database for the bowlers and joins their entries with either the leagues or events. If an event
         * team is being created, only the bowler's events are included, so it can be verified that the event name does
         * not match an existing event name. If a league team is being created, only the bowler's leagues which have
         * exactly {@code numberOfGames} games are included, or the open league.
         *
         * @param database access to database
         * @param creatingEvent {@code true} if an event team is being created, {@code false} for a league team
         * @param numberOfGames the number of games in the event or league
         * @return a cursor to access the values from the database
         */
        private Cursor getBowlersAndLeaguesOrEvents(SQLiteDatabase database,
                                                    boolean creatingEvent,
                                                    byte numberOfGames) {
            String rawQuery;
            String[] rawArgs;
            if (creatingEvent) {
                rawQuery = "SELECT "
                        + "bowler." + BowlerEntry._ID + " AS bid, "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + "league." + LeagueEntry._ID + " AS lid, "
                        + LeagueEntry.COLUMN_LEAGUE_NAME
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " LEFT OUTER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bid=" + LeagueEntry.COLUMN_BOWLER_ID
                        + " AND " + LeagueEntry.COLUMN_IS_EVENT + "=?"
                        + " ORDER BY " + BowlerEntry.COLUMN_DATE_MODIFIED + ", "
                        + LeagueEntry.COLUMN_DATE_MODIFIED;
                rawArgs = new String[]{String.valueOf(1)};
            } else {
                rawQuery = "SELECT "
                        + "bowler." + BowlerEntry._ID + " AS bid, "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + "league." + LeagueEntry._ID + " AS lid, "
                        + LeagueEntry.COLUMN_LEAGUE_NAME
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bid=" + LeagueEntry.COLUMN_BOWLER_ID
                        + " WHERE " + LeagueEntry.COLUMN_IS_EVENT + "=?"
                        + " AND (" + LeagueEntry.COLUMN_NUMBER_OF_GAMES + "=?"
                        + " OR " + LeagueEntry.COLUMN_LEAGUE_NAME + "=?)"
                        + " ORDER BY " + BowlerEntry.COLUMN_DATE_MODIFIED + ", "
                        + LeagueEntry.COLUMN_DATE_MODIFIED;
                rawArgs = new String[]{String.valueOf(0), String.valueOf(numberOfGames), Constants.NAME_OPEN_LEAGUE};
            }
            Log.d(TAG, rawQuery);
            Log.d(TAG, Arrays.toString(rawArgs));

            return database.rawQuery(rawQuery, rawArgs);
        }

        @Override
        protected void onPostExecute(Void result) {
            TeamFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return;

            if (mLoadedBowlers == null || mLoadedBowlerLeagueEvents == null)
                return;

            // Add the items loaded from the database to the lists in the fragment
            fragment.mAvailableBowlers.addAll(mLoadedBowlers);
            fragment.mBowlerLeagueEvents.putAll(mLoadedBowlerLeagueEvents);

            fragment.mAdapterBowlers.notifyDataSetChanged();
            fragment.mAdapterLeagues.notifyDataSetChanged();
        }
    }
}
