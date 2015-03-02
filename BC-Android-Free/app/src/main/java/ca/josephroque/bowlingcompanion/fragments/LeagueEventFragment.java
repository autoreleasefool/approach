package ca.josephroque.bowlingcompanion.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.DividerItemDecoration;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.LeagueEventAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewLeagueEventDialog;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-02-19.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragments
 * in project Bowling Companion
 */
public class LeagueEventFragment extends Fragment
    implements ChangeableTheme
{

    /** Tag to identify class when outputting to console */
    private static final String TAG = "LeagueEventFragment";

    /** Displays a list of items which each represent a league or event to the user */
    private RecyclerView mLeagueEventRecycler;
    /** Manages the data for mLeagueEventRecycler and provides the items to display */
    private RecyclerView.Adapter mLeagueEventAdapter;
    /** Displays an instructional message if no leagues or events exist */
    private TextView mNewLeagueEventInstructionsTextView;
    private FloatingActionButton mFloatingActionButtonNewLeagueEvent;

    /** Unique id of bowler selected by the user */
    private long mBowlerId = -1;
    /** List of league ids from "league" table in database to uniquely identify leagues */
    private List<Long> mListLeagueEventIds;
    /** List of league names which will be displayed by mLeagueEventRecycler */
    private List<String> mListLeagueEventNames;
    /** List of league average which will be displayed by mLeagueEventRecycler */
    private List<Short> mListLeagueEventAverages;
    /** List of number of games in leagues so series with proper number of games can be created */
    private List<Byte> mListLeagueEventNumberOfGames;

    /**
     * Creates a new instance of this fragment to display either leagues or events and returns it
     *
     * @param eventMode Indicates whether this object will be used to display leagues or events
     * @return new instance of this object
     */
    public static LeagueEventFragment newInstance(boolean eventMode)
    {
        LeagueEventFragment leagueEventFragment = new LeagueEventFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_EVENT_MODE, eventMode);
        leagueEventFragment.setArguments(args);

        return leagueEventFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        /*
         * Initializes member variables and obtains references to objects from root view.
         */
        View rootView = inflater.inflate(R.layout.fragment_leagues_events, container, false);

        mListLeagueEventIds = new ArrayList<>();
        mListLeagueEventNames = new ArrayList<>();
        mListLeagueEventAverages = new ArrayList<>();
        mListLeagueEventNumberOfGames = new ArrayList<>();

        mLeagueEventRecycler = (RecyclerView) rootView.findViewById(R.id.recyclerView_leagues_events);
        mLeagueEventRecycler.setHasFixedSize(true);
        mLeagueEventRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        RecyclerView.LayoutManager leagueLayoutManager = new LinearLayoutManager(getActivity());
        mLeagueEventRecycler.setLayoutManager(leagueLayoutManager);

        mLeagueEventAdapter = new LeagueEventAdapter(
                getActivity(),
                mListLeagueEventIds,
                mListLeagueEventNames,
                mListLeagueEventAverages,
                mListLeagueEventNumberOfGames,
                isEventMode());
        mLeagueEventRecycler.setAdapter(mLeagueEventAdapter);

        mFloatingActionButtonNewLeagueEvent = (FloatingActionButton)rootView.findViewById(R.id.fab_new_league_event);
        mFloatingActionButtonNewLeagueEvent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showNewLeagueOrEventDialog();
            }
        });

        mNewLeagueEventInstructionsTextView = (TextView)
                rootView.findViewById(R.id.textView_new_league_event_instructions);
        mNewLeagueEventInstructionsTextView.setText(
                isEventMode()
                ? R.string.text_new_event_instructions
                : R.string.text_new_league_instructions);

        updateTheme();
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mBowlerId = getActivity().getIntent().getLongExtra(Constants.EXTRA_ID_BOWLER, -1);

        mListLeagueEventIds.clear();
        mListLeagueEventNames.clear();
        mListLeagueEventAverages.clear();
        mListLeagueEventNumberOfGames.clear();

        if ((!isEventMode() && Theme.getLeagueFragmentThemeInvalidated())
                || (isEventMode() && Theme.getEventFragmentThemeInvalidated()))
        {
            updateTheme();
        }

        new LoadLeaguesEventsTask().execute();
    }

    /**
     * Prompts the user to add a new league or event
     */
    private void showNewLeagueOrEventDialog()
    {
        DialogFragment dialog = new NewLeagueEventDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRA_EVENT_MODE, isEventMode());
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "NewLeagueEventDialogFragment");
    }

    /**
     * Returns a boolean to indicate whether this instance of the fragment
     * is intended to display leagues or events
     *
     * @return true if it is intended to display events, false otherwise
     */
    private boolean isEventMode()
    {
        return getArguments().getBoolean(Constants.EXTRA_EVENT_MODE);
    }

    /**
     * Validates user input to create a new league/event entry in the database
     * if the input is valid
     *
     * @param leagueName name of the league/event to be created
     * @param numberOfGames number of games in a series in the league/event
     */
    public void addNewLeagueOrEvent(String leagueName, byte numberOfGames)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (numberOfGames < 1 ||
                (numberOfGames > Constants.MAX_NUMBER_LEAGUE_GAMES && !isEventMode())
                || (numberOfGames > Constants.MAX_NUMBER_EVENT_GAMES && isEventMode()))
        {
            //User has provided an invalid number of games
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and "
                    + (isEventMode()
                            ? Constants.MAX_NUMBER_EVENT_GAMES
                            : Constants.MAX_NUMBER_LEAGUE_GAMES)
                    + " (inclusive).";
        }
        else if (leagueName.equals(Constants.NAME_LEAGUE_OPEN))
        {
            /*
             * User has attempted to create a league or event entitled "Open"
             * which is a reserved name
             */
            validInput = false;
            invalidInputMessage = "That name is unavailable. It is a default used by the system. You must choose another.";
        }
        else if (mListLeagueEventNames.contains(leagueName))
        {
            //User has provided a name which is already in use for a league or event
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }

        if (!validInput)
        {
            //Displays an error dialog if the input was not valid and exits the method
            AlertDialog.Builder invalidInputBuilder = new AlertDialog.Builder(getActivity());
            invalidInputBuilder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        new AddNewLeagueEventTask().execute(leagueName, numberOfGames);
    }

    /**
     * Loads the names of relevant leagues or events and adds them to the lists
     * to be displayed to the user
     */
    private class LoadLeaguesEventsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

            String rawLeagueEventQuery = "SELECT "
                    + LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID + " AS lid, "
                    + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                    + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                    + " AVG(" + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ") AS avg"
                    + " FROM " + LeagueEntry.TABLE_NAME
                    + " LEFT JOIN " + GameEntry.TABLE_NAME
                    + " ON lid=" + GameEntry.COLUMN_NAME_LEAGUE_ID
                    + " WHERE " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=? AND " + LeagueEntry.COLUMN_NAME_IS_EVENT + "=?"
                    + " GROUP BY lid"
                    + " ORDER BY " + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " DESC";
            String[] rawLeagueEventArgs ={String.valueOf(mBowlerId), String.valueOf(isEventMode() ? 1:0)};

            Cursor leagueEventCursor = database.rawQuery(rawLeagueEventQuery, rawLeagueEventArgs);
            if (leagueEventCursor.moveToFirst())
            {
                while(!leagueEventCursor.isAfterLast())
                {
                    String leagueEventName = leagueEventCursor.getString(leagueEventCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    long leagueEventId = leagueEventCursor.getLong(leagueEventCursor.getColumnIndex("lid"));
                    byte numberOfGames = (byte)leagueEventCursor.getInt(leagueEventCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                    short leagueEventAverage = leagueEventCursor.getShort(leagueEventCursor.getColumnIndex("avg"));
                    mListLeagueEventIds.add(leagueEventId);
                    mListLeagueEventNames.add(leagueEventName);
                    mListLeagueEventAverages.add(leagueEventAverage);
                    mListLeagueEventNumberOfGames.add(numberOfGames);

                    leagueEventCursor.moveToNext();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mLeagueEventAdapter.notifyDataSetChanged();
            if (mListLeagueEventIds.size() > 0)
            {
                /*
                 * If there is at least one item loaded, the instructional text can be hidden
                 */
                hideNewLeagueEventInstructions();
            }
        }
    }

    /**
     * Creates a new entry in the database for a league or event which is then added to the list
     * of data to be displayed to the user.
     */
    private class AddNewLeagueEventTask extends AsyncTask<Object, Void, Void>
    {
        @Override
        protected Void doInBackground(Object... params)
        {
            //Gets name of league and the number of games from the parameters
            String leagueName = params[0].toString();
            byte numberOfGames = (Byte)params[1];

            long newId = -1;
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());
            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, leagueName);
            values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, currentDate);
            values.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, mBowlerId);
            values.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames);
            values.put(LeagueEntry.COLUMN_NAME_IS_EVENT, isEventMode() ? 1:0);

            database.beginTransaction();
            try
            {
                //Creates the entry for the league or event in the "league" database
                newId = database.insert(LeagueEntry.TABLE_NAME, null, values);

                if (isEventMode())
                {
                    /*
                     * If the new entry is an event, its series is also created at this time
                     * since there is only a single series to an event
                     */
                    values = new ContentValues();
                    values.put(SeriesEntry.COLUMN_NAME_DATE_CREATED, currentDate);
                    values.put(SeriesEntry.COLUMN_NAME_LEAGUE_ID, newId);
                    values.put(SeriesEntry.COLUMN_NAME_BOWLER_ID, mBowlerId);
                    long seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values);

                    for (int i = 0; i < numberOfGames; i++)
                    {
                        values = new ContentValues();
                        values.put(GameEntry.COLUMN_NAME_GAME_NUMBER, i + 1);
                        values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, 0);
                        values.put(GameEntry.COLUMN_NAME_LEAGUE_ID, newId);
                        values.put(GameEntry.COLUMN_NAME_BOWLER_ID, mBowlerId);
                        values.put(GameEntry.COLUMN_NAME_SERIES_ID, seriesId);
                        long gameId = database.insert(GameEntry.TABLE_NAME, null, values);

                        for (int j = 0; j < 10; j++)
                        {
                            values = new ContentValues();
                            values.put(FrameEntry.COLUMN_NAME_FRAME_NUMBER, j + 1);
                            values.put(FrameEntry.COLUMN_NAME_BOWLER_ID, mBowlerId);
                            values.put(FrameEntry.COLUMN_NAME_LEAGUE_ID, newId);
                            values.put(FrameEntry.COLUMN_NAME_GAME_ID, gameId);
                            database.insert(FrameEntry.TABLE_NAME, null, values);
                        }
                    }
                }

                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new league: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            //Adds the league to the top of the list (it is the most recent)
            mListLeagueEventIds.add(0, newId);
            mListLeagueEventNames.add(0, leagueName);
            mListLeagueEventAverages.add(0, (short)0);
            mListLeagueEventNumberOfGames.add(0, numberOfGames);

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            //Updates the data adapter and hides the instructional text
            mLeagueEventAdapter.notifyItemInserted(0);
            hideNewLeagueEventInstructions();
        }
    }

    @Override
    public void updateTheme()
    {
        mFloatingActionButtonNewLeagueEvent
                .setColorNormal(Theme.getActionButtonThemeColor());
        mFloatingActionButtonNewLeagueEvent
                .setColorPressed(Theme.getActionButtonThemeColor());
        mFloatingActionButtonNewLeagueEvent
                .setColorRipple(Theme.getActionButtonRippleThemeColor());

        if (isEventMode())
            Theme.validateEventFragmentTheme();
        else
            Theme.validateLeagueFragmentTheme();
    }

    public void showNewLeagueEventInstructions()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mNewLeagueEventInstructionsTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideNewLeagueEventInstructions()
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mNewLeagueEventInstructionsTextView.setVisibility(View.GONE);
            }
        });
    }
}
