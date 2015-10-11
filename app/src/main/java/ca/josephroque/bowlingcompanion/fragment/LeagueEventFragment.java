package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.NameAverageAdapter;
import ca.josephroque.bowlingcompanion.bowling.LeagueEvent;
import ca.josephroque.bowlingcompanion.bowling.Series;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NameLeagueEventDialog;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-15. Manages the UI to display information about the leagues being tracked by the
 * application, and offers a callback interface {@code LeagueEventFragment.LeagueEventCallback} for handling
 * interactions.
 */
public class LeagueEventFragment
        extends Fragment
        implements NameAverageAdapter.NameAverageEventHandler,
        NameLeagueEventDialog.NameLeagueEventDialogListener,
        FloatingActionButtonHandler {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "LeagueEventFragment";

    /** View to display league and event names and averages to user. */
    private RecyclerView mRecyclerViewLeagueEvents;
    /** Adapter to manage data displayed in mRecyclerViewLeagueEvents. */
    private NameAverageAdapter<LeagueEvent> mAdapterLeagueEvents;

    /** Callback listener for user events related to leagues. */
    private LeagueEventCallback mLeagueCallback;
    /** Callback listener for user events related to series. */
    private SeriesFragment.SeriesCallback mSeriesCallback;

    /** List to store league / event data from league / event table in database. */
    private List<LeagueEvent> mListLeaguesEvents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         * This makes sure the container Activity has implemented
         * the callback interface. If not, an exception is thrown
         */
        try {
            mLeagueCallback = (LeagueEventCallback) context;
            mSeriesCallback = (SeriesFragment.SeriesCallback) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLeagueSelectedListener and SeriesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLeagueCallback = null;
        mSeriesCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListLeaguesEvents = new ArrayList<>();

        mRecyclerViewLeagueEvents = (RecyclerView) rootView.findViewById(R.id.rv_names);
        mRecyclerViewLeagueEvents.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (mListLeaguesEvents.get(position).getLeagueEventName().substring(1).equals(
                        Constants.NAME_OPEN_LEAGUE)) {
                    mAdapterLeagueEvents.notifyItemChanged(position);
                    return;
                }

                mListLeaguesEvents.get(position).setIsDeleted(
                        !mListLeaguesEvents.get(position).wasDeleted());
                mAdapterLeagueEvents.notifyItemChanged(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewLeagueEvents);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewLeagueEvents.setLayoutManager(layoutManager);

        mAdapterLeagueEvents = new NameAverageAdapter<>(this,
                mListLeaguesEvents,
                NameAverageAdapter.DATA_LEAGUES_EVENTS);
        mRecyclerViewLeagueEvents.setAdapter(mAdapterLeagueEvents);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.title_fragment_league_event, true);
            mainActivity.setFloatingActionButtonState(R.drawable.ic_add_black_24dp);
            mainActivity.setDrawerState(false);
        }

        new LoadLeaguesEventsTask(this).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_leagues_events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_stats).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stats:
                //Displays stats of current bowler in a new StatsFragment
                if (mLeagueCallback != null)
                    mLeagueCallback.onBowlerStatsOpened();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNAItemClick(final int position) {
        //When league name is clicked, its data is opened in a new SeriesFragment
        new OpenLeagueEventSeriesTask(this).execute(position);
    }

    @Override
    public void onNAItemLongClick(final int position) {
        // When league name is long clicked, a dialog is opened to change or delete the item
        // Ignores long clicks on the "Open" league
        if (!Constants.NAME_OPEN_LEAGUE.equals(mListLeaguesEvents.get(position).getLeagueEventName().substring(1)))
            showLongClickLeagueDialog(position);
    }

    @Override
    public void onNAItemDelete(long id) {
        for (int i = 0; i < mListLeaguesEvents.size(); i++) {
            if (mListLeaguesEvents.get(i).getLeagueEventId() == id) {
                LeagueEvent leagueEvent = mListLeaguesEvents.remove(i);
                mAdapterLeagueEvents.notifyItemRemoved(i);
                deleteLeagueEvent(leagueEvent.getLeagueEventId());
            }
        }
    }

    @Override
    public void onNAItemUndoDelete(long id) {
        for (int i = 0; i < mListLeaguesEvents.size(); i++) {
            if (mListLeaguesEvents.get(i).getLeagueEventId() == id) {
                mListLeaguesEvents.get(i).setIsDeleted(false);
                mAdapterLeagueEvents.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onChangeLeagueEventName(final LeagueEvent leagueEventToChange, String name) {
        boolean validInput = true;
        int invalidInputMessage = -1;
        final LeagueEvent leagueEventWithNewName = new LeagueEvent(leagueEventToChange.getId(),
                ((leagueEventToChange.isEvent())
                        ? "E"
                        : "L") + name,
                leagueEventToChange.getAverage(),
                leagueEventToChange.getLeagueEventNumberOfGames());

        if (name.equalsIgnoreCase(Constants.NAME_OPEN_LEAGUE)) {
            /*
             * User has attempted to create a league or event entitled "Open"
             * which is a reserved name
             */
            validInput = false;
            invalidInputMessage = R.string.dialog_default_name;
        } else if (mListLeaguesEvents.contains(leagueEventWithNewName)) {
            //User has provided a name which is already in use for a league or event
            validInput = false;
            invalidInputMessage = R.string.dialog_name_exists;
        } else if (!name.matches(Constants.REGEX_LEAGUE_EVENT_NAME)) {
            //Name is not made up of letters, numbers and spaces
            validInput = false;
            invalidInputMessage = R.string.dialog_name_letters_spaces_numbers;
        }

        if (!validInput) {
            //Displays an error dialog if the input was not valid and exits the method
            new AlertDialog.Builder(getActivity())
                    .setMessage(invalidInputMessage)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            int position = mListLeaguesEvents.indexOf(leagueEventToChange);
            mListLeaguesEvents.set(position, leagueEventWithNewName);
            mAdapterLeagueEvents.notifyItemChanged(position);

            ((MainActivity) getActivity()).addSavingThread(new Thread(new Runnable() {
                @Override
                public void run() {
                    SQLiteDatabase database = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
                    String[] whereArgs = {String.valueOf(leagueEventWithNewName.getId())};
                    ContentValues values = new ContentValues();
                    values.put(LeagueEntry.COLUMN_LEAGUE_NAME, leagueEventWithNewName.getLeagueEventName());
                    database.beginTransaction();
                    try {
                        database.update(LeagueEntry.TABLE_NAME, values, LeagueEntry._ID + "=?", whereArgs);
                        database.setTransactionSuccessful();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error updating league/event name.", ex);
                    } finally {
                        database.endTransaction();
                    }
                }
            }));
        }
    }

    @Override
    public void onAddNewLeagueEvent(boolean isEvent, String leagueEventName, byte numberOfGames) {
        boolean validInput = true;
        int invalidInputMessageVal = -1;
        String invalidInputMessage = null;
        LeagueEvent leagueEvent = new LeagueEvent(0,
                ((isEvent)
                        ? "E"
                        : "L") + leagueEventName,
                (short) 0,
                numberOfGames);

        if (numberOfGames < 1 || (isEvent && numberOfGames > Constants.MAX_NUMBER_EVENT_GAMES)
                || (!isEvent && numberOfGames > Constants.MAX_NUMBER_LEAGUE_GAMES)) {
            //User has provided an invalid number of games
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and "
                    + (isEvent
                    ? Constants.MAX_NUMBER_EVENT_GAMES
                    : Constants.MAX_NUMBER_LEAGUE_GAMES) + " (inclusive).";
        } else if (leagueEventName.equalsIgnoreCase(Constants.NAME_OPEN_LEAGUE)) {
            /*
             * User has attempted to create a league or event entitled "Open"
             * which is a reserved name
             */
            validInput = false;
            invalidInputMessageVal = R.string.dialog_default_name;
        } else if (mListLeaguesEvents.contains(leagueEvent)) {
            //User has provided a name which is already in use for a league or event
            validInput = false;
            invalidInputMessageVal = R.string.dialog_name_exists;
        } else if (!leagueEventName.matches(Constants.REGEX_LEAGUE_EVENT_NAME)) {
            //Name is not made up of letters, numbers and spaces
            validInput = false;
            invalidInputMessageVal = R.string.dialog_name_letters_spaces_numbers;
        }

        if (!validInput) {
            //Displays an error dialog if the input was not valid and exits the method
            AlertDialog.Builder invalidInputBuilder = new AlertDialog.Builder(getActivity());
            if (invalidInputMessageVal == -1)
                invalidInputBuilder.setMessage(invalidInputMessage);
            else
                invalidInputBuilder.setMessage(invalidInputMessageVal);
            invalidInputBuilder
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            new AddNewLeagueEventTask(this).execute(leagueEvent);
        }
    }

    @Override
    public void onFabClick() {
        showLeagueOrEventDialog();
    }

    /**
     * Invoked when a league item is long clicked by the user. Displays a dialog with events relevant to the item.
     *
     * @param position position of the long clicked item
     */
    private void showLongClickLeagueDialog(final int position) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    if (selectedPosition == 0)
                        promptChangeItemName(position);
                    else
                        promptDeleteItem(position);
                }
                dialog.dismiss();
            }
        };

        final LeagueEvent leagueEvent = mListLeaguesEvents.get(position);
        new AlertDialog.Builder(getContext())
                .setTitle(leagueEvent.getLeagueEventName().substring(1))
                .setSingleChoiceItems(R.array.arr_long_click_name_average, 0, null)
                .setPositiveButton(R.string.dialog_okay, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener)
                .create()
                .show();
    }

    /**
     * Prompts user to change the name of a league or event.
     *
     * @param position position of the league / event to change the name of
     */
    private void promptChangeItemName(final int position) {
        DialogFragment dialogFragment = NameLeagueEventDialog.newInstance(this,
                false,
                true,
                mListLeaguesEvents.get(position));
        dialogFragment.show(getFragmentManager(), "ChangeNameLeagueEventDialog");
    }

    /**
     * Prompts the user to delete a league or event.
     *
     * @param position position of the item to delete
     */
    private void promptDeleteItem(final int position) {
        final LeagueEvent leagueEvent = mListLeaguesEvents.get(position);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    mListLeaguesEvents.remove(position);
                    mAdapterLeagueEvents.notifyItemRemoved(position);
                    deleteLeagueEvent(leagueEvent.getId());
                }
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete " + leagueEvent.getLeagueEventName().substring(1) + "?"
                        + " This cannot be undone!")
                .setPositiveButton(R.string.dialog_delete, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener)
                .create()
                .show();
    }

    /**
     * Deletes all data regarding a certain league id in the database.
     *
     * @param leagueEventId id of league/event whose data will be deleted
     */
    private void deleteLeagueEvent(final long leagueEventId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor prefsEditor = prefs.edit();
        long recentId = prefs.getLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
        long quickId = prefs.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);

        //Clears recent/quick ids if they match the deleted league
        if (recentId == leagueEventId) {
            prefsEditor.putLong(Constants.PREF_RECENT_BOWLER_ID, -1)
                    .putLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
        }
        if (quickId == leagueEventId) {
            prefsEditor.putLong(Constants.PREF_QUICK_BOWLER_ID, -1)
                    .putLong(Constants.PREF_QUICK_LEAGUE_ID, -1);
        }
        prefsEditor.apply();

        //Deletion occurs on separate thread so UI does not hang
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Deletes data from all tables corresponding to leagueEventId
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                String[] whereArgs = {String.valueOf(leagueEventId)};

                database.beginTransaction();
                try {
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.e(TAG, "Error deleting from database", ex);
                } finally {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Prompts user to select either league or event to add.
     */
    private void showLeagueOrEventDialog() {
        AlertDialog.Builder leagueOrEventBuilder = new AlertDialog.Builder(getActivity());
        leagueOrEventBuilder.setTitle("New league or event?")
                .setSingleChoiceItems(new CharSequence[]{"League", "Event"}, 0, null)
                .setPositiveButton(R.string.dialog_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showNewLeagueEventDialog(((AlertDialog) dialog)
                                .getListView().getCheckedItemPosition() == 1);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Prompts the user to add a new league or event.
     *
     * @param newEvent if true, a new event will be made. If false, a new league will be made.
     */
    private void showNewLeagueEventDialog(boolean newEvent) {
        DialogFragment dialogFragment = NameLeagueEventDialog.newInstance(this, newEvent, false, null);
        dialogFragment.show(getFragmentManager(), "NewLeagueEventDialog");
    }

    /**
     * Creates a new instance of this fragment to display.
     *
     * @return a new instance of LeagueEventFragment
     */
    public static LeagueEventFragment newInstance() {
        return new LeagueEventFragment();
    }

    /**
     * Loads/updates data for the league/event from the database and creates a new SeriesFragment or GameFragment to
     * display selected league or event, respectively.
     */
    private static final class OpenLeagueEventSeriesTask
            extends AsyncTask<Integer, Void, Pair<LeagueEvent, Series>> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<LeagueEventFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private OpenLeagueEventSeriesTask(LeagueEventFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Pair<LeagueEvent, Series> doInBackground(Integer... position) {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            LeagueEvent selectedLeagueEvent = fragment.mListLeaguesEvents.get(position[0]);
            boolean isEvent = selectedLeagueEvent.getLeagueEventName().substring(0, 1).equals("E");

            SQLiteDatabase db = DatabaseHelper.getInstance(mainActivity).getWritableDatabase();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            String currentDate = df.format(new Date());

            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);

            //Updates the date modified in the database of the selected league
            db.beginTransaction();
            try {
                db.update(LeagueEntry.TABLE_NAME,
                        values,
                        LeagueEntry._ID + "=?",
                        new String[]{String.valueOf(selectedLeagueEvent.getLeagueEventId())});
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                //does nothing - error updating league date - non-fatal
            } finally {
                db.endTransaction();
            }

            /*
             * If an event was selected by the user the corresponding series of the event is
             * loaded from the database so an instance of GameFragment can be created, since
             * creating a SeriesFragment would be redundant for a single series.
             */
            if (isEvent) {
                String rawSeriesQuery = "SELECT "
                        + SeriesEntry._ID + ", "
                        + SeriesEntry.COLUMN_SERIES_DATE
                        + " FROM " + SeriesEntry.TABLE_NAME
                        + " WHERE " + SeriesEntry.COLUMN_LEAGUE_ID + "=?";

                Cursor cursor = db.rawQuery(rawSeriesQuery,
                        new String[]{String.valueOf(selectedLeagueEvent.getLeagueEventId())});
                if (cursor.moveToFirst()) {
                    long seriesId = cursor.getLong(cursor.getColumnIndex(SeriesEntry._ID));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE));
                    cursor.close();

                    return Pair.create(selectedLeagueEvent, new Series(seriesId, seriesDate, null, null));
                } else
                    cursor.close();
                throw new RuntimeException("Event series id could not be loaded from database");
            } else
                return Pair.create(selectedLeagueEvent, new Series(-1, null, null, null));
        }

        @Override
        protected void onPostExecute(Pair<LeagueEvent, Series> result) {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null || result == null)
                return;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return;

            boolean isEvent = result.second.getSeriesId() >= 0;

            if (isEvent) {
                /*
                 * If an event was selected, creates an instance of GameFragment
                 * displaying the event's corresponding series
                 */

                if (fragment.mLeagueCallback != null
                        && fragment.mSeriesCallback != null) {
                    fragment.mLeagueCallback.onLeagueSelected(result.first, false);
                    fragment.mSeriesCallback.onSeriesSelected(result.second, true);
                }
            } else {
                /*
                 * If a league was selected, creates an instance of SeriesActivity
                 * to display all available series in the league
                 */
                long bowlerId = mainActivity.getBowlerId();

                if (!result.first.getLeagueEventName().equals(Constants.NAME_OPEN_LEAGUE)) {
                    PreferenceManager.getDefaultSharedPreferences(mainActivity)
                            .edit()
                            .putLong(Constants.PREF_RECENT_LEAGUE_ID, result.first.getLeagueEventId())
                            .putLong(Constants.PREF_RECENT_BOWLER_ID, bowlerId)
                            .apply();
                }

                if (fragment.mLeagueCallback != null)
                    fragment.mLeagueCallback.onLeagueSelected(result.first, true);
            }
        }
    }

    /**
     * Loads the names of relevant leagues or events and adds them to the lists to be displayed to the user.
     */
    private static final class LoadLeaguesEventsTask
            extends AsyncTask<Void, Void, List<LeagueEvent>> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<LeagueEventFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadLeaguesEventsTask(LeagueEventFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mListLeaguesEvents.clear();
            fragment.mAdapterLeagueEvents.notifyDataSetChanged();
        }

        @Override
        protected List<LeagueEvent> doInBackground(Void... params) {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            SQLiteDatabase database = DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            List<LeagueEvent> listLeagueEvents = new ArrayList<>();

            String rawLeagueEventQuery = "SELECT "
                    + "league." + LeagueEntry._ID + " AS lid, "
                    + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                    + LeagueEntry.COLUMN_IS_EVENT + ", "
                    + LeagueEntry.COLUMN_NUMBER_OF_GAMES + ", "
                    + GameEntry.COLUMN_SCORE
                    + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                    + " LEFT JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                    + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry.COLUMN_LEAGUE_ID
                    + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                    + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                    + " WHERE " + LeagueEntry.COLUMN_BOWLER_ID + "=?"
                    + " ORDER BY " + LeagueEntry.COLUMN_DATE_MODIFIED + " DESC";

            long bowlerId = mainActivity.getBowlerId();
            getLeaguesFromDatabase(database, listLeagueEvents, rawLeagueEventQuery, bowlerId);

            return listLeagueEvents;
        }

        /**
         * Retrieves a list of leagues/events and their averages from the database.
         *
         * @param database app database
         * @param listLeagueEvents a list which will contain the leagues/events loaded from the database
         * @param rawLeagueEventQuery query for leagues/events
         * @param bowlerId id of the bowler
         */
        private void getLeaguesFromDatabase(SQLiteDatabase database,
                                            List<LeagueEvent> listLeagueEvents,
                                            String rawLeagueEventQuery,
                                            long bowlerId) {
            Cursor cursor = database.rawQuery(rawLeagueEventQuery, new String[]{String.valueOf(bowlerId)});
            long lastLeagueEventId = -1;
            int leagueTotal = 0;
            int leagueNumberOfGames = 0;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long leagueEventId = cursor.getLong(cursor.getColumnIndex("lid"));
                    if (leagueEventId != lastLeagueEventId && lastLeagueEventId != -1) {
                        cursor.moveToPrevious();
                        boolean isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1;
                        short average = (leagueNumberOfGames > 0)
                                ? (short) (leagueTotal / leagueNumberOfGames)
                                : 0;
                        LeagueEvent leagueEvent = new LeagueEvent(cursor.getLong(cursor.getColumnIndex(
                                "lid")),
                                ((isEvent)
                                        ? "E"
                                        : "L")
                                        + cursor.getString(cursor.getColumnIndex(
                                        LeagueEntry.COLUMN_LEAGUE_NAME)),
                                average,
                                (byte) cursor.getInt(cursor.getColumnIndex(
                                        LeagueEntry.COLUMN_NUMBER_OF_GAMES)));
                        listLeagueEvents.add(leagueEvent);
                        leagueTotal = 0;
                        leagueNumberOfGames = 0;
                        cursor.moveToNext();
                    }
                    short score = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    if (score > 0) {
                        leagueTotal += score;
                        leagueNumberOfGames++;
                    }

                    lastLeagueEventId = leagueEventId;
                    cursor.moveToNext();
                }
                cursor.moveToPrevious();
                boolean isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1;
                short average = (leagueNumberOfGames > 0)
                        ? (short) (leagueTotal / leagueNumberOfGames)
                        : 0;
                LeagueEvent leagueEvent = new LeagueEvent(cursor.getLong(cursor.getColumnIndex(
                        "lid")),
                        ((isEvent)
                                ? "E"
                                : "L")
                                + cursor.getString(cursor.getColumnIndex(
                                LeagueEntry.COLUMN_LEAGUE_NAME)),
                        average,
                        (byte) cursor.getInt(cursor.getColumnIndex(
                                LeagueEntry.COLUMN_NUMBER_OF_GAMES)));
                listLeagueEvents.add(leagueEvent);
            }

            cursor.close();
        }

        @Override
        protected void onPostExecute(List<LeagueEvent> listLeagueEvents) {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null || listLeagueEvents == null)
                return;

            fragment.mListLeaguesEvents.addAll(listLeagueEvents);
            fragment.mAdapterLeagueEvents.notifyDataSetChanged();
        }
    }

    /**
     * Creates a new entry in the database for a league or event which is then added to the list of data to be displayed
     * to the user.
     */
    private static final class AddNewLeagueEventTask
            extends AsyncTask<LeagueEvent, Void, LeagueEvent> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<LeagueEventFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private AddNewLeagueEventTask(LeagueEventFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected LeagueEvent doInBackground(LeagueEvent... league) {
            LeagueEventFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            league[0].setLeagueEventId(-1);
            long bowlerId = mainActivity.getBowlerId();

            SQLiteDatabase db = DatabaseHelper.getInstance(mainActivity).getWritableDatabase();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            String currentDate = df.format(new Date());

            ContentValues values = new ContentValues();
            values.put(LeagueEntry.COLUMN_LEAGUE_NAME, league[0].getLeagueEventName().substring(1));
            values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);
            values.put(LeagueEntry.COLUMN_BOWLER_ID, bowlerId);
            values.put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, league[0].getLeagueEventNumberOfGames());
            values.put(LeagueEntry.COLUMN_IS_EVENT, league[0].getLeagueEventName().startsWith("E"));

            db.beginTransaction();
            try {
                //Creates the entry for the league or event in the "league" database
                league[0].setLeagueEventId(db.insert(LeagueEntry.TABLE_NAME, null, values));
                if (league[0].getLeagueEventName().startsWith("E")) {
                    /*
                     * If the new entry is an event, its series is also created at this time
                     * since there is only a single series to an event
                     */
                    values = new ContentValues();
                    values.put(SeriesEntry.COLUMN_SERIES_DATE, currentDate);
                    values.put(SeriesEntry.COLUMN_LEAGUE_ID, league[0].getLeagueEventId());
                    long seriesId = db.insert(SeriesEntry.TABLE_NAME, null, values);

                    for (int i = 0; i < league[0].getLeagueEventNumberOfGames(); i++) {
                        values = new ContentValues();
                        values.put(GameEntry.COLUMN_GAME_NUMBER, i + 1);
                        values.put(GameEntry.COLUMN_SCORE, 0);
                        values.put(GameEntry.COLUMN_SERIES_ID, seriesId);
                        long gameId = db.insert(GameEntry.TABLE_NAME, null, values);

                        for (int j = 0; j < Constants.NUMBER_OF_FRAMES; j++) {
                            values = new ContentValues();
                            values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1);
                            values.put(FrameEntry.COLUMN_GAME_ID, gameId);
                            db.insert(FrameEntry.TABLE_NAME, null, values);
                        }
                    }
                }
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, "Error adding new league", ex);
            } finally {
                db.endTransaction();
            }

            return league[0];
        }

        @Override
        protected void onPostExecute(LeagueEvent result) {
            LeagueEventFragment fragment = mFragment.get();
            if (result != null && fragment != null && result.getLeagueEventId() != -1) {
                fragment.mListLeaguesEvents.add(0, result);
                fragment.mAdapterLeagueEvents.notifyItemInserted(0);
                fragment.mRecyclerViewLeagueEvents.scrollToPosition(0);
            }
        }
    }

    /**
     * Container Activity must implement this interface to allow SeriesFragment/GameFragment to be loaded when a
     * league/event is selected.
     */
    public interface LeagueEventCallback {

        /**
         * Should be overridden to create a SeriesFragment with the series belonging to the league represented by
         * leagueId.
         *
         * @param leagueEvent league / event whose series will be displayed
         * @param openSeriesFragment indicates if series fragment should be opened
         */
        void onLeagueSelected(LeagueEvent leagueEvent, boolean openSeriesFragment);

        /**
         * Used to open StatsFragment to display bowler stats.
         */
        void onBowlerStatsOpened();
    }
}
