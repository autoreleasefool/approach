package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.NameAverageAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewLeagueEventDialog;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-15.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class LeagueEventFragment extends Fragment
    implements Theme.ChangeableTheme, NameAverageAdapter.NameAverageEventHandler, NewLeagueEventDialog.NewLeagueEventDialogListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "LeagueEventFragment";

    private RecyclerView mRecyclerViewLeagueEvents;
    private NameAverageAdapter mAdapterLeagueEvents;

    private long mBowlerId = -1;
    private List<Long> mListLeagueEventIds;
    private List<String> mListLeagueEventNames;
    private List<Short> mListLeagueEventAverages;
    private List<Byte> mListLeagueEventNumberOfGames;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fab_list, container, false);

        mListLeagueEventIds = new ArrayList<>();
        mListLeagueEventNames = new ArrayList<>();
        mListLeagueEventAverages = new ArrayList<>();
        mListLeagueEventNumberOfGames = new ArrayList<>();

        mRecyclerViewLeagueEvents = (RecyclerView)rootView.findViewById(R.id.rv_names);
        mRecyclerViewLeagueEvents.setHasFixedSize(true);
        mRecyclerViewLeagueEvents.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewLeagueEvents.setLayoutManager(layoutManager);

        mAdapterLeagueEvents = new NameAverageAdapter(this,
                mListLeagueEventNames,
                mListLeagueEventAverages,
                NameAverageAdapter.DATA_LEAGUES_EVENTS);
        mRecyclerViewLeagueEvents.setAdapter(mAdapterLeagueEvents);

        FloatingActionButton floatingActionButton =
                (FloatingActionButton)rootView.findViewById(R.id.fab_new_list_item);
        floatingActionButton.setImageResource(R.drawable.ic_action_new);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showLeagueOrEventDialog();
            }
        });

        ((TextView)rootView.findViewById(R.id.tv_new_list_item)).setText(R.string.text_new_league_event);
        ((TextView)rootView.findViewById(R.id.tv_delete_list_item)).setText(R.string.text_delete_league_event);

        if (savedInstanceState != null)
        {
            mBowlerId = savedInstanceState.getLong(Constants.EXTRA_ID_BOWLER);
        }

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle(R.string.title_league_event);

        if (mBowlerId == -1)
        {
            mBowlerId = getArguments().getLong(Constants.EXTRA_ID_BOWLER);
        }

        mListLeagueEventIds.clear();
        mListLeagueEventNames.clear();
        mListLeagueEventAverages.clear();
        mListLeagueEventNumberOfGames.clear();

        if (Theme.getLeagueEventFragmentThemeInvalidated())
        {
            updateTheme();
        }

        new LoadLeaguesEventsTask().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_leagues_events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_stats:
                //TODO show stats
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.EXTRA_ID_BOWLER, mBowlerId);
    }

    @Override
    public void updateTheme()
    {
        FloatingActionButton fab = (FloatingActionButton)getView().findViewById(R.id.fab_new_list_item);
        fab.setColorNormal(Theme.getPrimaryThemeColor());
        fab.setColorPressed(Theme.getPrimaryThemeColor());
        fab.setColorRipple(Theme.getTertiaryThemeColor());
        Theme.validateLeagueEventFragmentTheme();
    }

    @Override
    public void onNAItemClick(final int position)
    {
        new OpenLeagueEventSeriesTask().execute(position);
    }

    @Override
    public void onNALongClick(final int position)
    {
        showDeleteLeagueOrEventDialog(position);
    }

    @Override
    public void onAddNewLeagueEvent(boolean isEvent, String leagueEventName, byte numberOfGames)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (numberOfGames < 1 ||
                (isEvent && numberOfGames > Constants.MAX_NUMBER_EVENT_GAMES)
                || (!isEvent && numberOfGames > Constants.MAX_NUMBER_LEAGUE_GAMES))
        {
            //User has provided an invalid number of games
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and "
                    + (isEvent
                            ? Constants.MAX_NUMBER_EVENT_GAMES
                            : Constants.MAX_NUMBER_LEAGUE_GAMES)
                    + " (inclusive).";
        }
        else if (leagueEventName.equalsIgnoreCase(Constants.NAME_OPEN_LEAGUE))
        {
            /*
             * User has attempted to create a league or event entitled "Open"
             * which is a reserved name
             */
            validInput = false;
            invalidInputMessage = "That name is unavailable. It is a default used by the system. You must choose another.";
        }
        else if (mListLeagueEventNames.contains(leagueEventName))
        {
            //User has provided a name which is already in use for a league or event
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }
        else if (!leagueEventName.matches(Constants.REGEX_LEAGUE_EVENT_NAME))
        {
            //Name is not made up of letters, numbers and spaces
            validInput = false;
            invalidInputMessage = "You can only use letters, numbers and spaces in a name.";
        }

        if (!validInput)
        {
            //Displays an error dialog if the input was not valid and exits the method
            AlertDialog.Builder invalidInputBuilder = new AlertDialog.Builder(getActivity());
            invalidInputBuilder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener()
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

        new AddNewLeagueEventTask().execute(isEvent, leagueEventName, numberOfGames);
    }

    /**
     * Prompts user with a dialog to delete all data regarding a certain
     * league or event in the database
     *
     * @param position position of league id in mListLeagueEventIds
     */
    private void showDeleteLeagueOrEventDialog(final int position)
    {
        final String leagueEventName = mListLeagueEventNames.get(position).substring(1);
        final long leagueId = mListLeagueEventIds.get(position);

        /*
         * There is a default league "Open" which is created along with a new bowler
         * and cannot be deleted - this conditional prevents its removal
         */
        if (leagueEventName.equals(Constants.NAME_OPEN_LEAGUE))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("The league \"" + leagueEventName + "\" cannot be deleted.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener()
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

        DatabaseHelper.deleteData(getActivity(),
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteLeagueEvent(leagueId);
                    }
                },
                leagueEventName);
    }

    /**
     * Deletes all data regarding a certain league id in the database
     *
     * @param leagueEventId id of league/event whose data will be deleted
     */
    private void deleteLeagueEvent(final long leagueEventId)
    {
        //Removes league from RecyclerView immediately  UI doesn't hang
        final int index = mListLeagueEventIds.indexOf(leagueEventId);
        final String leagueEventName = mListLeagueEventNames.remove(index);
        mListLeagueEventAverages.remove(index);
        mListLeagueEventNumberOfGames.remove(index);
        mListLeagueEventIds.remove(index);
        mAdapterLeagueEvents.notifyItemRemoved(index);

        //Deletion occurs on separate thread so UI does not hang
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Deletes data from all tables corresponding to leagueEventId
                SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                String[] whereArgs = {String.valueOf(leagueEventId)};

                database.beginTransaction();
                try
                {
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Error deleting league: " + leagueEventName + " - " + ex.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Prompts user to select either league or event to add
     */
    private void showLeagueOrEventDialog()
    {
        AlertDialog.Builder leagueOrEventBuilder = new AlertDialog.Builder(getActivity());
        leagueOrEventBuilder.setTitle("New league or event?")
                .setItems(new CharSequence[]{"League", "Event"}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        showNewLeagueEventDialog(which == 1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts the user to add a new league or event
     * @param newEvent if true, a new event will be made. If false,
     *                 a new league will be made.
     */
    private void showNewLeagueEventDialog(boolean newEvent)
    {
        DialogFragment dialogFragment = NewLeagueEventDialog.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRA_EVENT_MODE, newEvent);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "NewLeagueEventDialog");
    }

    /**
     * Creates a new instance of this fragment to display
     * @return a new instance of LeagueEventFragment
     */
    public static LeagueEventFragment newInstance()
    {
        return new LeagueEventFragment();
    }

    private class OpenLeagueEventSeriesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            long selectedLeagueId = mListLeagueEventIds.get(position[0]);
            String selectedLeagueName = mListLeagueEventNames.get(position[0]);
            byte numberOfGames = mListLeagueEventNumberOfGames.get(position[0]);
            boolean isEvent = selectedLeagueName.substring(0, 1).equals("E");
            selectedLeagueName = selectedLeagueName.substring(1);

            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);

            //Updates the date modified in the database of the selected league
            database.beginTransaction();
            try
            {
                database.update(LeagueEntry.TABLE_NAME,
                        values,
                        LeagueEntry._ID + "=?",
                        new String[]{String.valueOf(selectedLeagueId)});
                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error updating league: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            /*
             * If an event was selected by the user the corresponding series of the event is
             * loaded from the database so an instance of GameFragment can be created, since
             * creating a SeriesFragment would be redundant for a single series.
             */
            if (isEvent)
            {
                String rawSeriesQuery = "SELECT "
                        + SeriesEntry._ID + ", "
                        + SeriesEntry.COLUMN_SERIES_DATE
                        + " FROM " + SeriesEntry.TABLE_NAME
                        + " WHERE " + SeriesEntry.COLUMN_LEAGUE_ID + "=?";

                Cursor cursor = database.rawQuery(rawSeriesQuery, new String[]{String.valueOf(selectedLeagueId)});
                if (cursor.moveToFirst())
                {
                    long seriesId = cursor.getLong(cursor.getColumnIndex(SeriesEntry._ID));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE));
                    cursor.close();

                    return new Object[]{true, seriesId, numberOfGames, selectedLeagueId, selectedLeagueName, seriesDate};
                }
                else
                {
                    Log.w(TAG, "Could not load event series: " + selectedLeagueId);
                    return null;
                }
            }
            else
            {
                return new Object[]{false, numberOfGames, selectedLeagueId, selectedLeagueName};
            }
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            boolean isEvent = (Boolean)params[0];

            if (isEvent)
            {
                /*
                 * If an event was selected, creates an instance of GameFragment
                 * displaying the event's corresponding series
                 */
                long seriesId = (Long)params[1];
                byte numberOfGames = (Byte)params[2];
                long leagueId = (Long)params[3];
                String leagueName = params[4].toString();
                String seriesDate = params[5].toString();

                //TODO: create fragment transaction to game
                Log.w(TAG, "Fragment transaction incomplete: Event-Game");
            }
            else
            {
                /*
                 * If a league was selected, creates an instance of SeriesActivity
                 * to display all available series in the league
                 */
                byte numberOfGames = (Byte)params[1];
                long leagueId = (Long)params[2];
                String leagueName = params[3].toString();

                if (!leagueName.equals(Constants.NAME_OPEN_LEAGUE))
                {
                    getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
                            .edit()
                            .putLong(Constants.PREF_RECENT_LEAGUE_ID, leagueId)
                            .putLong(Constants.PREF_RECENT_BOWLER_ID, mBowlerId)
                            .apply();
                }

                //TODO: create fragment transaction to series
                Log.w(TAG, "Fragment transaction incomplete: League-Series");
            }
        }
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
                    + "league." + LeagueEntry._ID + " AS lid, "
                    + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                    + LeagueEntry.COLUMN_IS_EVENT + ", "
                    + LeagueEntry.COLUMN_NUMBER_OF_GAMES + ", "
                    + " AVG(" + GameEntry.COLUMN_SCORE + ") AS avg"
                    + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                    + " LEFT JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                    + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry.COLUMN_LEAGUE_ID
                    + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                    + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                    + " WHERE " + LeagueEntry.COLUMN_BOWLER_ID + "=?"
                    + " GROUP BY lid"
                    + " ORDER BY " + LeagueEntry.COLUMN_DATE_MODIFIED + " DESC";

            Cursor cursor = database.rawQuery(rawLeagueEventQuery, new String[]{String.valueOf(mBowlerId)});
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    boolean isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1;

                    mListLeagueEventIds.add(cursor.getLong(cursor.getColumnIndex("lid")));
                    mListLeagueEventNames.add(((isEvent) ? "E" : "L")
                            + cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME)));
                    mListLeagueEventAverages.add(cursor.getShort(cursor.getColumnIndex("avg")));
                    mListLeagueEventNumberOfGames.add((byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES)));
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mAdapterLeagueEvents.notifyDataSetChanged();
        }
    }

    /**
     * Creates a new entry in the database for a league or event which is then added to the list
     * of data to be displayed to the user.
     */
    private class AddNewLeagueEventTask extends AsyncTask<Object, Void, Long>
    {
        @Override
        protected Long doInBackground(Object... params)
        {
            boolean isEvent = (Boolean)params[0];
            String leagueEventName = params[1].toString();
            byte numberOfGames = (Byte)params[2];

            long newId = -1;
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_LEAGUE_NAME, leagueEventName);
            values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);
            values.put(LeagueEntry.COLUMN_BOWLER_ID, mBowlerId);
            values.put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, numberOfGames);
            values.put(LeagueEntry.COLUMN_IS_EVENT, isEvent);

            database.beginTransaction();
            try
            {
                //Creates the entry for the league or event in the "league" database
                newId = database.insert(LeagueEntry.TABLE_NAME, null, values);

                if (isEvent)
                {
                    /*
                     * If the new entry is an event, its series is also created at this time
                     * since there is only a single series to an event
                     */
                    values = new ContentValues();
                    values.put(SeriesEntry.COLUMN_SERIES_DATE, currentDate);
                    values.put(SeriesEntry.COLUMN_LEAGUE_ID, newId);
                    long seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values);

                    for (int i = 0; i < numberOfGames; i++)
                    {
                        values = new ContentValues();
                        values.put(GameEntry.COLUMN_GAME_NUMBER, i + 1);
                        values.put(GameEntry.COLUMN_SCORE, 0);
                        values.put(GameEntry.COLUMN_SERIES_ID, seriesId);
                        long gameId = database.insert(GameEntry.TABLE_NAME, null, values);

                        for (int j = 0; j < Constants.NUMBER_OF_FRAMES; j++)
                        {
                            values = new ContentValues();
                            values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1);
                            values.put(FrameEntry.COLUMN_GAME_ID, gameId);
                            database.insert(FrameEntry.TABLE_NAME, null, values);
                        }
                    }
                }
                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new league/event: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            if (newId > -1)
            {
                //Adds the league to the top of the list (it is the most recent)
                mListLeagueEventIds.add(0, newId);
                mListLeagueEventNames.add(((isEvent) ? "E":"L")
                        + leagueEventName);
                mListLeagueEventAverages.add(0, (short)0);
                mListLeagueEventNumberOfGames.add(0, numberOfGames);
            }

            return newId;
        }

        @Override
        protected void onPostExecute(Long newLeagueId)
        {
            if (newLeagueId > 0)
            {
                mAdapterLeagueEvents.notifyItemInserted(0);
            }
        }
    }
}
