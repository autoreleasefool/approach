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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import ca.josephroque.bowlingcompanion.wrapper.Bowler;
import ca.josephroque.bowlingcompanion.wrapper.LeagueEvent;
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NameBowlerDialog;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FacebookUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-13. Manages the UI to display information about the bowlers being tracked by the
 * application, and offers a callback interface {@code BowlerFragment.BowlerCallback} for handling interactions.
 */
@SuppressWarnings("Convert2Lambda")
public class BowlerFragment
        extends Fragment
        implements NameAverageAdapter.NameAverageEventHandler,
        NameBowlerDialog.NameBowlerDialogListener,
        FloatingActionButtonHandler {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BowlerFragment";

    /** View to display bowler names and averages to user. */
    private RecyclerView mRecyclerViewBowlers;
    /** Adapter to manage data displayed in mRecyclerViewBowlers. */
    private NameAverageAdapter<Bowler> mAdapterBowlers;

    /** Callback listener for user events related to bowlers. */
    private BowlerCallback mBowlerCallback;
    /** Callback listener for user events related to leagues. */
    private LeagueEventFragment.LeagueEventCallback mLeagueSelectedCallback;
    /** Callback listener for user events related to series. */
    private SeriesFragment.SeriesCallback mSeriesCallback;

    /** List to store bowler data from bowler table in database. */
    private List<Bowler> mListBowlers;

    /** Id from 'bowler' database which represents the most recently used bowler. */
    private long mRecentBowlerId = -1;
    /** Id from 'league' database which represents the most recently edited league. */
    private long mRecentLeagueId = -1;
    /** Number of games in the most recently edited league. */
    private byte mRecentNumberOfGames = -1;
    /** Name of most recently edited bowler. */
    private String mRecentBowlerName;
    /** Name of most recently edited league. */
    private String mRecentLeagueName;

    /** Id from 'bowler' database which represents the preferred bowler for a quick series. */
    private long mQuickBowlerId = -1;
    /** Id from 'league' database which represents the preferred league for quick series. */
    private long mQuickLeagueId = -1;
    /** Number of games in the preferred league. */
    private byte mQuickNumberOfGames = -1;
    /** Name of preferred bowler. */
    private String mQuickBowlerName;
    /** Name of preferred league. */
    private String mQuickLeagueName;


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
            mBowlerCallback = (BowlerCallback) context;
            mLeagueSelectedCallback = (LeagueEventFragment.LeagueEventCallback) context;
            mSeriesCallback = (SeriesFragment.SeriesCallback) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBowlerSelectedListener, OnLeagueSelectedListener,"
                    + " SeriesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBowlerCallback = null;
        mLeagueSelectedCallback = null;
        mSeriesCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListBowlers = new ArrayList<>();

        mRecyclerViewBowlers = (RecyclerView) rootView.findViewById(R.id.rv_names);
        mRecyclerViewBowlers.setHasFixedSize(true);

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
                mListBowlers.get(position).setIsDeleted(!mListBowlers.get(position).wasDeleted());
                mAdapterBowlers.notifyItemChanged(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewBowlers);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewBowlers.setLayoutManager(layoutManager);
        mAdapterBowlers = new NameAverageAdapter<>(this,
                mListBowlers,
                NameAverageAdapter.DATA_BOWLERS);
        mRecyclerViewBowlers.setAdapter(mAdapterBowlers);

        displayFacebookPagePromotion(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.app_name, true);
            mainActivity.setDrawerState(false);
            mainActivity.setFloatingActionButtonState(R.drawable.ic_person_add_black_24dp, 0);

            // Loads values for member variables from preferences, if they exist
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
            mRecentBowlerId = prefs.getLong(Constants.PREF_RECENT_BOWLER_ID, -1);
            mRecentLeagueId = prefs.getLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
            mQuickBowlerId = prefs.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);
            mQuickLeagueId = prefs.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);
        }

        // Creates AsyncTask to load data from database
        new LoadBowlerAndRecentTask(this).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bowlers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            super.onPrepareOptionsMenu(menu);
            return;
        }
        boolean drawerOpen = mainActivity.isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_quick_series).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_quick_series:
                showQuickSeriesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNAItemClick(final int position) {
        // When bowler name is clicked, their leagues are displayed in new fragment
        new OpenBowlerLeaguesTask(this).execute(position);
    }

    @Override
    public void onNAItemLongClick(final int position) {
        // When bowler name is long clicked, a dialog is opened to change or delete the item
        showLongClickBowlerDialog(position);
    }

    @Override
    public void onNAItemDelete(long id) {
        for (int i = 0; i < mListBowlers.size(); i++) {
            if (mListBowlers.get(i).getBowlerId() == id) {
                Bowler bowler = mListBowlers.remove(i);
                mAdapterBowlers.notifyItemRemoved(i);
                deleteBowler(bowler.getBowlerId());
            }
        }
    }

    @Override
    public void onNAItemUndoDelete(long id) {
        for (int i = 0; i < mListBowlers.size(); i++) {
            if (mListBowlers.get(i).getBowlerId() == id) {
                mListBowlers.get(i).setIsDeleted(false);
                mAdapterBowlers.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onChangeBowlerName(final Bowler bowlerToChange, String name) {
        boolean validInput = true;
        int invalidInputMessage = -1;
        final Bowler bowlerWithNewName = new Bowler(bowlerToChange.getId(),
                name,
                bowlerToChange.getAverage());

        if (mListBowlers.contains(bowlerWithNewName)) {
            // Bowler name already exists in the list
            validInput = false;
            invalidInputMessage = R.string.dialog_name_exists;
        } else if (!name.matches(Constants.REGEX_NAME)) {
            // Name is not made up of letters and spaces
            validInput = false;
            invalidInputMessage = R.string.dialog_name_letters_spaces;
        }

        if (!validInput) {
            // Displays an error dialog if the input was not valid and exits the method
            new AlertDialog.Builder(getContext())
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
            int position = mListBowlers.indexOf(bowlerToChange);
            mListBowlers.set(position, bowlerWithNewName);
            mAdapterBowlers.notifyItemChanged(position);

            ((MainActivity) getActivity()).addSavingThread(new Thread(new Runnable() {
                @Override
                public void run() {
                    SQLiteDatabase database = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
                    String[] whereArgs = {String.valueOf(bowlerWithNewName.getId())};
                    ContentValues values = new ContentValues();
                    values.put(BowlerEntry.COLUMN_BOWLER_NAME, bowlerWithNewName.getBowlerName());
                    database.beginTransaction();
                    try {
                        database.update(BowlerEntry.TABLE_NAME, values, BowlerEntry._ID + "=?", whereArgs);
                        database.setTransactionSuccessful();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error updating bowler name.", ex);
                    } finally {
                        database.endTransaction();
                    }
                }
            }));
        }
    }

    @Override
    public void onAddNewBowler(String bowlerName) {
        boolean validInput = true;
        int invalidInputMessage = -1;
        Bowler newBowler = new Bowler(0, bowlerName, (short) 0);

        if (mListBowlers.contains(newBowler)) {
            // Bowler name already exists in the list
            validInput = false;
            invalidInputMessage = R.string.dialog_name_exists;
        } else if (!bowlerName.matches(Constants.REGEX_NAME)) {
            // Name is not made up of letters and spaces
            validInput = false;
            invalidInputMessage = R.string.dialog_name_letters_spaces;
        }

        /*
         * If the input was invalid for any reason, a dialog is shown
         * to the user and the method does not continue
         */
        if (!validInput) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(invalidInputMessage)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        /*
         * Creates a new database entry for the bowler whose name
         * was received by input via the dialog
         */
        new NewBowlerTask(this).execute(newBowler);
    }

    @Override
    public void onFabClick() {
        showNewBowlerDialog();
    }

    @Override
    public void onSecondaryFabClick() {
        // does nothing
    }

    /**
     * Invoked when a bowler item is long clicked by the user. Displays a dialog with events relevant to the item.
     *
     * @param position position of the long clicked item
     */
    private void showLongClickBowlerDialog(final int position) {
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

        final Bowler bowler = mListBowlers.get(position);
        new AlertDialog.Builder(getContext())
                .setTitle(bowler.getBowlerName())
                .setSingleChoiceItems(R.array.arr_long_click_name_average, 0, null)
                .setPositiveButton(R.string.dialog_okay, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener)
                .create()
                .show();
    }

    /**
     * Prompts user to change the name of a bowler.
     *
     * @param position position of the bowler to change the name of
     */
    private void promptChangeItemName(final int position) {
        DialogFragment dialogFragment = NameBowlerDialog.newInstance(this,
                true,
                mListBowlers.get(position));
        dialogFragment.show(getFragmentManager(), "ChangeNameBowlerDialog");
    }

    /**
     * Prompts the user to delete a bowler.
     *
     * @param position position of the item to delete
     */
    private void promptDeleteItem(final int position) {
        final Bowler bowler = mListBowlers.get(position);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    mListBowlers.remove(position);
                    mAdapterBowlers.notifyItemRemoved(position);
                    deleteBowler(bowler.getId());
                }
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete " + bowler.getBowlerName() + "?"
                        + " This cannot be undone!")
                .setPositiveButton(R.string.dialog_delete, onClickListener)
                .setNegativeButton(R.string.dialog_cancel, onClickListener)
                .create()
                .show();
    }

    /**
     * Prompts user to create a new series with mRecentBowlerId and mRecentLeagueId or mQuickBowlerId and
     * mQuickLeagueId.
     */
    private void showQuickSeriesDialog() {
        if ((mQuickBowlerId > -1 && mQuickLeagueId > -1)
                || (mRecentBowlerId > -1 && mRecentLeagueId > -1)) {
            /*
             * If a quick bowler was set, or a bowler has been previously selected,
             * a dialog is displayed to prompt user to create a new series
             */
            final boolean quickOrRecent;
            AlertDialog.Builder quickSeriesBuilder = new AlertDialog.Builder(getContext());
            if (mQuickBowlerId == -1 || mQuickLeagueId == -1) {
                quickSeriesBuilder.setMessage("Create a new series with these settings?"
                        + "\nBowler: " + mRecentBowlerName
                        + "\nLeague: " + mRecentLeagueName);
                quickOrRecent = false;
            } else {
                quickSeriesBuilder.setMessage("Create a new series with these settings?"
                        + "\nBowler: " + mQuickBowlerName
                        + "\nLeague: " + mQuickLeagueName);
                quickOrRecent = true;
            }

            quickSeriesBuilder.setTitle("Quick Series")
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (quickOrRecent) {
                                Bowler quickBowler = new Bowler(mQuickBowlerId, mQuickBowlerName, (short) 0);
                                if (mBowlerCallback != null && mLeagueSelectedCallback != null
                                        && mSeriesCallback != null) {
                                    mBowlerCallback.onBowlerSelected(quickBowler, false, true);
                                    mLeagueSelectedCallback.onLeagueSelected(new LeagueEvent(
                                                    mQuickLeagueId,
                                                    mQuickLeagueName,
                                                    true,
                                                    (short) 0,
                                                    (short) -1,
                                                    -1,
                                                    mQuickNumberOfGames),
                                            false);
                                    mSeriesCallback.onCreateNewSeries(false);
                                }
                            } else {
                                Bowler recentBowler = new Bowler(mRecentBowlerId, mRecentBowlerName, (short) 0);
                                if (mBowlerCallback != null && mLeagueSelectedCallback != null
                                        && mSeriesCallback != null) {
                                    mBowlerCallback.onBowlerSelected(recentBowler, false, true);
                                    mLeagueSelectedCallback.onLeagueSelected(new LeagueEvent(
                                                    mRecentLeagueId,
                                                    mRecentLeagueName,
                                                    true,
                                                    (short) 0,
                                                    (short) -1,
                                                    -1,
                                                    mRecentNumberOfGames),
                                            false);
                                    mSeriesCallback.onCreateNewSeries(false);
                                }
                            }
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
        } else {
            // If no recent/quick bowler, dialog is displayed to inform user of options
            AlertDialog.Builder quickSeriesDisabledBuilder = new AlertDialog.Builder(getContext());
            quickSeriesDisabledBuilder.setTitle("Quick Series")
                    .setMessage(R.string.dialog_quick_series_instructions)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    /**
     * Prepares and shows promotional content for Facebook page, if the user has not visited the page before.
     *
     * @param rootView fragment root view
     */
    private void displayFacebookPagePromotion(View rootView) {
        if (getContext() == null)
            return;

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (preferences.getBoolean(Constants.PREF_FACEBOOK_PAGE_OPENED, false)
                || preferences.getBoolean(Constants.PREF_FACEBOOK_CLOSED, false))
            return;

        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_facebook_promotion);
        linearLayout.setVisibility(View.VISIBLE);

        View.OnClickListener openFacebookListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                preferences.edit().putBoolean(Constants.PREF_FACEBOOK_PAGE_OPENED, true).apply();
                startActivity(FacebookUtils.newFacebookIntent(getContext().getPackageManager()));
            }
        };

        linearLayout.findViewById(R.id.iv_facebook).setOnClickListener(openFacebookListener);
        linearLayout.findViewById(R.id.tv_facebook).setOnClickListener(openFacebookListener);
        linearLayout.findViewById(R.id.iv_facebook_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putBoolean(Constants.PREF_FACEBOOK_CLOSED, true).apply();
                linearLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Opens an instance of NewBowlerDialog and displays it to the user.
     */
    private void showNewBowlerDialog() {
        DialogFragment dialogFragment = NameBowlerDialog.newInstance(this, false, null);
        dialogFragment.show(getFragmentManager(), "NewBowlerDialog");
    }

    /**
     * Deletes all data regarding a certain bowler id in the database.
     *
     * @param bowlerId id of bowler whose data will be deleted
     */
    private void deleteBowler(final long bowlerId) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        long recentId = prefs.getLong(Constants.PREF_RECENT_BOWLER_ID, -1);
        long quickId = prefs.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);

        // Clears recent/quick ids if they match the deleted bowler
        if (recentId == bowlerId) {
            prefsEditor.putLong(Constants.PREF_RECENT_BOWLER_ID, -1)
                    .putLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
        }
        if (quickId == bowlerId) {
            prefsEditor.putLong(Constants.PREF_QUICK_BOWLER_ID, -1)
                    .putLong(Constants.PREF_QUICK_LEAGUE_ID, -1);
        }
        prefsEditor.apply();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
                String[] whereArgs = {String.valueOf(bowlerId)};
                database.beginTransaction();
                try {
                    database.delete(BowlerEntry.TABLE_NAME,
                            BowlerEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    // does nothing
                } finally {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Creates a new instance of this fragment to display.
     *
     * @return a new instance of BowlerFragment
     */
    public static BowlerFragment newInstance() {
        return new BowlerFragment();
    }

    /**
     * Loads names of bowlers, along with other relevant data, and adds them to recycler view.
     */
    private static final class LoadBowlerAndRecentTask
            extends AsyncTask<Void, Void, List<Bowler>> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadBowlerAndRecentTask(BowlerFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            BowlerFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mListBowlers.clear();
            fragment.mAdapterBowlers.notifyDataSetChanged();
        }

        @Override
        protected List<Bowler> doInBackground(Void... params) {
            // Method exits if fragment gets detached before reaching this call
            BowlerFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            SQLiteDatabase database = DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            List<Bowler> listBowlers = new ArrayList<>();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mainActivity);
            boolean includeEvents = preferences.getBoolean(Constants.KEY_INCLUDE_EVENTS, true);
            boolean includeOpen = preferences.getBoolean(Constants.KEY_INCLUDE_OPEN, true);

            String gameSumAndCountQuery = "SELECT "
                    + "league2." + LeagueEntry._ID + " AS lid2, "
                    + "SUM(game2." + GameEntry.COLUMN_SCORE + ") AS gameSum, "
                    + "COUNT(game2." + GameEntry._ID + ") AS gameCount"
                    + " FROM " + LeagueEntry.TABLE_NAME + " AS league2"
                    + " INNER JOIN " + SeriesEntry.TABLE_NAME + " AS series2"
                    + " ON lid2=" + SeriesEntry.COLUMN_LEAGUE_ID
                    + " INNER JOIN " + GameEntry.TABLE_NAME + " AS game2"
                    + " ON series2." + SeriesEntry._ID + "=" + GameEntry.COLUMN_SERIES_ID
                    + " WHERE "
                    + " game2." + GameEntry.COLUMN_SCORE + ">?"
                    + " AND "
                    + (!includeEvents
                    ? LeagueEntry.COLUMN_IS_EVENT
                    : "'0'") + "=?"
                    + " AND "
                    + (!includeOpen
                    ? LeagueEntry.COLUMN_LEAGUE_NAME + "!"
                    : "'0'") + "=?"
                    + " GROUP BY league2." + LeagueEntry._ID;

            String baseAverageAndGamesQuery = "SELECT "
                    + "league3." + LeagueEntry._ID + " AS lid3, "
                    + LeagueEntry.COLUMN_BASE_AVERAGE + " * " + LeagueEntry.COLUMN_BASE_GAMES + " AS baseSum, "
                    + LeagueEntry.COLUMN_BASE_GAMES + " AS baseGames"
                    + " FROM " + LeagueEntry.TABLE_NAME + " AS league3"
                    + " WHERE "
                    + " league3." + LeagueEntry.COLUMN_BASE_AVERAGE + ">?";

            // Query to retrieve bowler names and averages from database
            String rawBowlerQuery = "SELECT "
                    + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                    + "bowler." + BowlerEntry._ID + " AS bid, "
                    + "SUM(t.gameSum) AS totalSum, "
                    + "SUM(t.gameCount) AS totalCount, "
                    + "SUM(u.baseSum) AS totalBaseSum, "
                    + "SUM(u.baseGames) AS totalBaseGames"
                    + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                    + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                    + " ON bowler." + BowlerEntry._ID + "=" + LeagueEntry.COLUMN_BOWLER_ID
                    + " LEFT JOIN (" + gameSumAndCountQuery + ") AS t"
                    + " ON t.lid2=league." + LeagueEntry._ID
                    + " LEFT JOIN (" + baseAverageAndGamesQuery + ") AS u"
                    + " ON u.lid3=league." + LeagueEntry._ID
                    + " GROUP BY bowler." + BowlerEntry._ID
                    + " ORDER BY bowler." + BowlerEntry.COLUMN_DATE_MODIFIED + " DESC";
            String[] rawBowlerArgs = {
                    String.valueOf(0),
                    String.valueOf(0),
                    (!includeOpen
                            ? Constants.NAME_OPEN_LEAGUE
                            : String.valueOf(0)),
                    String.valueOf(0)
            };

            // Adds loaded bowler names and averages to lists to display
            Cursor cursor = database.rawQuery(rawBowlerQuery, rawBowlerArgs);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int totalSum = cursor.getInt(cursor.getColumnIndex("totalSum"));
                    int totalCount = cursor.getInt(cursor.getColumnIndex("totalCount"));
                    int totalBaseSum = cursor.getInt(cursor.getColumnIndex("totalBaseSum"));
                    int totalBaseGames = cursor.getInt(cursor.getColumnIndex("totalBaseGames"));
                    short bowlerAverage = (short) ((totalCount + totalBaseGames > 0)
                            ? (totalSum + totalBaseSum) / (totalCount + totalBaseGames)
                            : 0);
                    Bowler bowler = new Bowler(cursor.getLong(cursor.getColumnIndex("bid")),
                            cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                            bowlerAverage);
                    listBowlers.add(bowler);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            // If a recent bowler exists, their name and league is loaded to be used for quick series
            if (fragment.mRecentBowlerId > -1 && fragment.mRecentLeagueId > -1) {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID
                        + "=league." + LeagueEntry.COLUMN_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? "
                        + "AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{
                        String.valueOf(fragment.mRecentBowlerId),
                        String.valueOf(fragment.mRecentLeagueId)
                };

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                if (cursor.moveToFirst()) {
                    fragment.mRecentBowlerName = cursor.getString(
                            cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    fragment.mRecentLeagueName = cursor.getString(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    fragment.mRecentNumberOfGames = (byte) cursor.getInt(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                } else {
                    fragment.mRecentBowlerId = -1;
                    fragment.mRecentLeagueId = -1;
                }
                cursor.close();
            }

            // If a custom bowler is set, their name and league is loaded to be used for quick series
            if (fragment.mQuickBowlerId > -1 && fragment.mQuickLeagueId > -1) {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID
                        + "=league." + LeagueEntry.COLUMN_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=?"
                        + "AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{
                        String.valueOf(fragment.mQuickBowlerId),
                        String.valueOf(fragment.mQuickLeagueId)
                };

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                if (cursor.moveToFirst()) {
                    fragment.mQuickBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    fragment.mQuickLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    fragment.mQuickNumberOfGames
                            = (byte) cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                } else {
                    fragment.mQuickBowlerId = -1;
                    fragment.mQuickLeagueId = -1;
                }
                cursor.close();
            }

            return listBowlers;
        }

        @Override
        protected void onPostExecute(List<Bowler> listBowlers) {
            BowlerFragment fragment = mFragment.get();
            if (listBowlers == null || fragment == null)
                return;

            fragment.mListBowlers.addAll(listBowlers);
            fragment.mAdapterBowlers.notifyDataSetChanged();
        }
    }

    /**
     * Sets data to be displayed in new instance of LeagueEventFragment.
     */
    private static final class OpenBowlerLeaguesTask
            extends AsyncTask<Integer, Void, Bowler> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private OpenBowlerLeaguesTask(BowlerFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Bowler doInBackground(Integer... position) {
            BowlerFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            Bowler bowler = fragment.mListBowlers.get(position[0]);

            // Updates date which bowler was last accessed in database
            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getWritableDatabase();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_DATE_MODIFIED, dateFormat.format(new Date()));

            database.beginTransaction();
            try {
                database.update(BowlerEntry.TABLE_NAME,
                        values,
                        BowlerEntry._ID + "=?",
                        new String[]{String.valueOf(bowler.getBowlerId())});
                database.setTransactionSuccessful();
            } catch (Exception ex) {
                // does nothing
            } finally {
                database.endTransaction();
            }

            return bowler;
        }

        @Override
        protected void onPostExecute(Bowler result) {
            BowlerFragment fragment = mFragment.get();
            if (result == null || fragment == null)
                return;

            // Creates new instance of LeagueEventFragment for bowler
            if (fragment.mBowlerCallback != null)
                fragment.mBowlerCallback.onBowlerSelected(result, true, false);
        }
    }

    /**
     * Creates new bowler in the database and adds them to the recycler view.
     */
    private static final class NewBowlerTask
            extends AsyncTask<Bowler, Void, Bowler> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private NewBowlerTask(BowlerFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Bowler doInBackground(Bowler... bowler) {
            BowlerFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            bowler[0].setBowlerId(-1);
            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getWritableDatabase();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            String currentDate = dateFormat.format(new Date());

            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_BOWLER_NAME, bowler[0].getBowlerName());
            values.put(BowlerEntry.COLUMN_DATE_MODIFIED, currentDate);

            database.beginTransaction();
            try {
                bowler[0].setBowlerId(database.insert(BowlerEntry.TABLE_NAME, null, values));

                /*
                 * Creates an entry in the 'league' table for a default league
                 * for the new bowler being added
                 */
                if (bowler[0].getBowlerId() != -1) {
                    values = new ContentValues();
                    values.put(LeagueEntry.COLUMN_LEAGUE_NAME, Constants.NAME_OPEN_LEAGUE);
                    values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);
                    values.put(LeagueEntry.COLUMN_BOWLER_ID, bowler[0].getBowlerId());
                    values.put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, 1);
                    database.insert(LeagueEntry.TABLE_NAME, null, values);
                }

                database.setTransactionSuccessful();
            } catch (Exception ex) {
                // does nothing
            } finally {
                database.endTransaction();
            }

            return bowler[0];
        }

        @Override
        protected void onPostExecute(Bowler newBowler) {
            BowlerFragment fragment = mFragment.get();

            /*
             * Adds the new bowler information to the corresponding lists
             * and displays them in the recycler view
             */
            if (newBowler != null && fragment != null && newBowler.getBowlerId() != -1) {
                fragment.mListBowlers.add(0, newBowler);
                fragment.mAdapterBowlers.notifyItemInserted(0);
                fragment.mRecyclerViewBowlers.scrollToPosition(0);
            }
        }
    }

    /**
     * Container Activity must implement this interface to allow LeagueEventFragment to be loaded when a bowler is
     * selected.
     */
    public interface BowlerCallback {

        /**
         * Should be overridden to create a LeagueEventFragment with the leagues belonging to the bowler represented by
         * bowlerId.
         *
         * @param bowler bowler whose leagues / events will be displayed
         * @param openLeagueFragment if new fragment should be opened
         * @param isQuickSeries if a quick series is being created
         */
        void onBowlerSelected(Bowler bowler,
                              boolean openLeagueFragment,
                              boolean isQuickSeries);
    }
}
