package ca.josephroque.bowlingcompanion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import ca.josephroque.bowlingcompanion.DividerItemDecoration;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.NameAverageAdapter;
import ca.josephroque.bowlingcompanion.data.Bowler;
import ca.josephroque.bowlingcompanion.data.LeagueEvent;
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewBowlerDialog;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-13. <p/> Manages the UI to display information about the bowlers
 * being tracked by the application, and offers a callback interface {@link
 * BowlerFragment.OnBowlerSelectedListener} for handling interactions.
 */
@SuppressWarnings("Convert2Lambda")
public class BowlerFragment
        extends Fragment
        implements NameAverageAdapter.NameAverageEventHandler,
        NewBowlerDialog.NewBowlerDialogListener,
        FloatingActionButtonHandler
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BowlerFragment";

    /** View to display bowler names and averages to user. */
    private RecyclerView mRecyclerViewBowlers;
    /** Adapter to manage data displayed in mRecyclerViewBowlers. */
    private NameAverageAdapter<Bowler> mAdapterBowlers;

    /** Callback listener for user events related to bowlers. */
    private OnBowlerSelectedListener mBowlerSelectedListener;
    /** Callback listener for user events related to leagues. */
    private LeagueEventFragment.OnLeagueSelectedListener mLeagueSelectedListener;
    /** Callback listener for user events related to series. */
    private SeriesFragment.SeriesListener mSeriesListener;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        /*
         * This makes sure the container Activity has implemented
         * the callback interface. If not, an exception is thrown
         */
        try
        {
            mBowlerSelectedListener = (OnBowlerSelectedListener) activity;
            mLeagueSelectedListener = (LeagueEventFragment.OnLeagueSelectedListener) activity;
            mSeriesListener = (SeriesFragment.SeriesListener) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBowlerSelectedListener, OnLeagueSelectedListener,"
                    + " SeriesListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListBowlers = new ArrayList<>();

        mRecyclerViewBowlers = (RecyclerView) rootView.findViewById(R.id.rv_names);
        mRecyclerViewBowlers.setHasFixedSize(true);
        mRecyclerViewBowlers.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
            {
                final int position = viewHolder.getAdapterPosition();
                mListBowlers.get(position).setIsDeleted(!mListBowlers.get(position).wasDeleted());
                mAdapterBowlers.notifyItemChanged(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerViewBowlers);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewBowlers.setLayoutManager(layoutManager);
        mAdapterBowlers = new NameAverageAdapter<>(this,
                mListBowlers,
                NameAverageAdapter.DATA_BOWLERS);
        mRecyclerViewBowlers.setAdapter(mAdapterBowlers);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getActivity() != null)
        {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.app_name, true);
            mainActivity.setFloatingActionButtonIcon(R.drawable.ic_person_add_black_24dp);
            mainActivity.setCurrentFragment(this);
            mainActivity.setDrawerState(false);

            //Loads values for member variables from preferences, if they exist
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
            mRecentBowlerId = prefs.getLong(Constants.PREF_RECENT_BOWLER_ID, -1);
            mRecentLeagueId = prefs.getLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
            mQuickBowlerId = prefs.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);
            mQuickLeagueId = prefs.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);
        }

        mListBowlers.clear();
        mAdapterBowlers.notifyDataSetChanged();

        //Creates AsyncTask to load data from database
        new LoadBowlerAndRecentTask(this).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_bowlers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_quick_series).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
        {
            drawable.mutate();
            drawable.setColorFilter(0x000000, PorterDuff.Mode.SRC_ATOP);
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
            case R.id.action_quick_series:
                showQuickSeriesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNAItemClick(final int position)
    {
        //When bowler name is clicked, their leagues are displayed in new fragment
        new OpenBowlerLeaguesTask(this).execute(position);
    }

    @Override
    public void onNAItemDelete(long id)
    {
        for (int i = 0; i < mListBowlers.size(); i++) {
            if (mListBowlers.get(i).getBowlerId() == id)
            {
                Bowler bowler = mListBowlers.remove(i);
                mAdapterBowlers.notifyItemRemoved(i);
                deleteBowler(bowler.getBowlerId());
            }
        }
    }

    @Override
    public void onNAItemUndoDelete(long id)
    {
        for (int i = 0; i < mListBowlers.size(); i++) {
            if (mListBowlers.get(i).getBowlerId() == id)
            {
                mListBowlers.get(i).setIsDeleted(false);
                mAdapterBowlers.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean validInput = true;
        int invalidInputMessage = -1;
        Bowler newBowler = new Bowler(0, bowlerName, (short) 0);

        if (mListBowlers.contains(newBowler))
        {
            //Bowler name already exists in the list
            validInput = false;
            invalidInputMessage = R.string.dialog_name_exists;
        }
        else if (!bowlerName.matches(Constants.REGEX_NAME))
        {
            //Name is not made up of letters and spaces
            validInput = false;
            invalidInputMessage = R.string.dialog_name_letters_spaces;
        }

        /*
         * If the input was invalid for any reason, a dialog is shown
         * to the user and the method does not continue
         */
        if (!validInput)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
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

        /*
         * Creates a new database entry for the bowler whose name
         * was received by input via the dialog
         */
        new NewBowlerTask(this).execute(newBowler);
    }

    @Override
    public void onFabClick()
    {
        showNewBowlerDialog();
    }

    /**
     * Prompts user to create a new series with mRecentBowlerId and mRecentLeagueId or
     * mQuickBowlerId and mQuickLeagueId.
     */
    private void showQuickSeriesDialog()
    {
        if ((mQuickBowlerId > -1 && mQuickLeagueId > -1)
                || (mRecentBowlerId > -1 && mRecentLeagueId > -1))
        {
            /*
             * If a quick bowler was set, or a bowler has been previously selected,
             * a dialog is displayed to prompt user to create a new series
             */
            final boolean quickOrRecent;
            AlertDialog.Builder quickSeriesBuilder = new AlertDialog.Builder(getActivity());
            if (mQuickBowlerId == -1 || mQuickLeagueId == -1)
            {
                quickSeriesBuilder.setMessage("Create a new series with these settings?"
                        + "\nBowler: " + mRecentBowlerName
                        + "\nLeague: " + mRecentLeagueName);
                quickOrRecent = false;
            }
            else
            {
                quickSeriesBuilder.setMessage("Create a new series with these settings?"
                        + "\nBowler: " + mQuickBowlerName
                        + "\nLeague: " + mQuickLeagueName);
                quickOrRecent = true;
            }

            quickSeriesBuilder.setTitle("Quick Series")
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (quickOrRecent)
                            {
                                Bowler quickBowler = new Bowler(mQuickBowlerId,
                                        mQuickBowlerName,
                                        (short) 0);
                                mBowlerSelectedListener.onBowlerSelected(quickBowler, false, true);
                                mLeagueSelectedListener.onLeagueSelected(new LeagueEvent(
                                                mQuickLeagueId,
                                                mQuickLeagueName,
                                                (short) 0,
                                                mQuickNumberOfGames),
                                        false);
                                mSeriesListener.onCreateNewSeries(false);
                            }
                            else
                            {
                                Bowler recentBowler = new Bowler(mRecentBowlerId,
                                        mRecentBowlerName,
                                        (short) 0);
                                mBowlerSelectedListener.onBowlerSelected(recentBowler, false, true);
                                mLeagueSelectedListener.onLeagueSelected(new LeagueEvent(
                                                mRecentLeagueId,
                                                mRecentLeagueName,
                                                (short) 0,
                                                mRecentNumberOfGames),
                                        false);
                                mSeriesListener.onCreateNewSeries(false);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
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
        else
        {
            //If no recent/quick bowler, dialog is displayed to inform user of options
            AlertDialog.Builder quickSeriesDisabledBuilder = new AlertDialog.Builder(getActivity());
            quickSeriesDisabledBuilder.setTitle("Quick Series")
                    .setMessage(R.string.dialog_quick_series_instructions)
                    .setPositiveButton(R.string.dialog_okay, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    /**
     * Opens an instance of NewBowlerDialog and displays it to the user.
     */
    private void showNewBowlerDialog()
    {
        DialogFragment dialogFragment = NewBowlerDialog.newInstance(this);
        dialogFragment.show(getFragmentManager(), "NewBowlerDialog");
    }

    /**
     * Deletes all data regarding a certain bowler id in the database.
     *
     * @param bowlerId id of bowler whose data will be deleted
     */
    private void deleteBowler(final long bowlerId)
    {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        long recentId = prefs.getLong(Constants.PREF_RECENT_BOWLER_ID, -1);
        long quickId = prefs.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);

        //Clears recent/quick ids if they match the deleted bowler
        if (recentId == bowlerId)
        {
            prefsEditor.putLong(Constants.PREF_RECENT_BOWLER_ID, -1)
                    .putLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
        }
        if (quickId == bowlerId)
        {
            prefsEditor.putLong(Constants.PREF_QUICK_BOWLER_ID, -1)
                    .putLong(Constants.PREF_QUICK_LEAGUE_ID, -1);
        }
        prefsEditor.apply();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                String[] whereArgs = {String.valueOf(bowlerId)};
                database.beginTransaction();
                try
                {
                    database.delete(BowlerEntry.TABLE_NAME,
                            BowlerEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    //does nothing
                }
                finally
                {
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
    public static BowlerFragment newInstance()
    {
        return new BowlerFragment();
    }

    /**
     * Loads names of bowlers, along with other relevant data, and adds them to recycler view.
     */
    private static class LoadBowlerAndRecentTask
            extends AsyncTask<Void, Void, List<Bowler>>
    {

        /** Weak reference to the parent fragment. */
        private WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadBowlerAndRecentTask(BowlerFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected List<Bowler> doInBackground(Void... params)
        {
            //Method exits if fragment gets detached before reaching this call
            BowlerFragment fragment = mFragment.get();
            if (fragment == null)
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            List<Bowler> listBowlers = new ArrayList<>();

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(mainActivity);
            boolean includeEvents = preferences.getBoolean(Constants.KEY_INCLUDE_EVENTS, true);
            boolean includeOpen = preferences.getBoolean(Constants.KEY_INCLUDE_OPEN, true);

            String rawInnerQuery = "SELECT "
                    + "league2." + LeagueEntry._ID + " AS lid2, "
                    + "SUM(game2." + GameEntry.COLUMN_SCORE + ") AS gameSum, "
                    + "COUNT(game2." + GameEntry._ID + ") AS gameCount"
                    + " FROM " + LeagueEntry.TABLE_NAME + " AS league2"
                    + " INNER JOIN " + SeriesEntry.TABLE_NAME + " AS series2"
                    + " ON lid2=" + SeriesEntry.COLUMN_LEAGUE_ID
                    + " INNER JOIN " + GameEntry.TABLE_NAME + " AS game2"
                    + " ON series2." + SeriesEntry._ID + "=" + GameEntry.COLUMN_SERIES_ID
                    + " WHERE "
                    + (!includeEvents
                    ? LeagueEntry.COLUMN_IS_EVENT
                    : "'0'") + "=?"
                    + " AND "
                    + (!includeOpen
                    ? LeagueEntry.COLUMN_LEAGUE_NAME + "!"
                    : "'0'") + "=?"
                    + " GROUP BY league2." + LeagueEntry._ID;

            //Query to retrieve bowler names and averages from database
            String rawBowlerQuery = "SELECT "
                    + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                    + "bowler." + BowlerEntry._ID + " AS bid, "
                    + "SUM(t.gameSum) AS totalSum, "
                    + "SUM(t.gameCount) AS totalCount"
                    + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                    + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                    + " ON bowler." + BowlerEntry._ID + "=" + LeagueEntry.COLUMN_BOWLER_ID
                    + " LEFT JOIN (" + rawInnerQuery + ") AS t"
                    + " ON t.lid2=league." + LeagueEntry._ID
                    + " GROUP BY bowler." + BowlerEntry._ID
                    + " ORDER BY bowler." + BowlerEntry.COLUMN_DATE_MODIFIED + " DESC";
            String[] rawBowlerArgs = {
                    String.valueOf(0), (!includeOpen
                    ? Constants.NAME_OPEN_LEAGUE
                    : String.valueOf(0))
            };

            //Adds loaded bowler names and averages to lists to display
            Cursor cursor = database.rawQuery(rawBowlerQuery, rawBowlerArgs);
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    int totalSum = cursor.getInt(cursor.getColumnIndex("totalSum"));
                    int totalCount = cursor.getInt(cursor.getColumnIndex("totalCount"));
                    Bowler bowler = new Bowler(cursor.getLong(cursor.getColumnIndex("bid")),
                            cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)),
                            (short) ((totalCount > 0)
                                    ? totalSum / totalCount
                                    : 0));
                    listBowlers.add(bowler);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            //If a recent bowler exists, their name and league is loaded to be used for quick series
            if (fragment.mRecentBowlerId > -1 && fragment.mRecentLeagueId > -1)
            {
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
                if (cursor.moveToFirst())
                {
                    fragment.mRecentBowlerName = cursor.getString(
                            cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    fragment.mRecentLeagueName = cursor.getString(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    fragment.mRecentNumberOfGames = (byte) cursor.getInt(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                }
                else
                {
                    fragment.mRecentBowlerId = -1;
                    fragment.mRecentLeagueId = -1;
                }
                cursor.close();
            }

            //If a custom bowler is set, their name and league is loaded to be used for quick series
            if (fragment.mQuickBowlerId > -1 && fragment.mQuickLeagueId > -1)
            {
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
                if (cursor.moveToFirst())
                {
                    fragment.mQuickBowlerName = cursor.getString(
                            cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    fragment.mQuickLeagueName = cursor.getString(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    fragment.mQuickNumberOfGames = (byte) cursor.getInt(
                            cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                }
                else
                {
                    fragment.mQuickBowlerId = -1;
                    fragment.mQuickLeagueId = -1;
                }
                cursor.close();
            }

            return listBowlers;
        }

        @Override
        protected void onPostExecute(List<Bowler> listBowlers)
        {
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
    private static class OpenBowlerLeaguesTask
            extends AsyncTask<Integer, Void, Bowler>
    {

        /** Weak reference to the parent fragment. */
        private WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private OpenBowlerLeaguesTask(BowlerFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Bowler doInBackground(Integer... position)
        {
            BowlerFragment fragment = mFragment.get();
            if (fragment == null)
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            Bowler bowler = fragment.mListBowlers.get(position[0]);

            //Updates date which bowler was last accessed in database
            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getWritableDatabase();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_DATE_MODIFIED, dateFormat.format(new Date()));

            database.beginTransaction();
            try
            {
                database.update(BowlerEntry.TABLE_NAME,
                        values,
                        BowlerEntry._ID + "=?",
                        new String[]{String.valueOf(bowler.getBowlerId())});
                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                //does nothing
            }
            finally
            {
                database.endTransaction();
            }

            return bowler;
        }

        @Override
        protected void onPostExecute(Bowler result)
        {
            BowlerFragment fragment = mFragment.get();
            if (result == null || fragment == null)
                return;

            //Creates new instance of LeagueEventFragment for bowler
            fragment.mBowlerSelectedListener.onBowlerSelected(result, true, false);
        }
    }

    /**
     * Creates new bowler in the database and adds them to the recycler view.
     */
    private static class NewBowlerTask
            extends AsyncTask<Bowler, Void, Bowler>
    {

        /** Weak reference to the parent fragment. */
        private WeakReference<BowlerFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private NewBowlerTask(BowlerFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected Bowler doInBackground(Bowler... bowler)
        {
            BowlerFragment fragment = mFragment.get();
            if (fragment == null)
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
            try
            {
                bowler[0].setBowlerId(database.insert(BowlerEntry.TABLE_NAME, null, values));

                /*
                 * Creates an entry in the 'league' table for a default league
                 * for the new bowler being added
                 */
                if (bowler[0].getBowlerId() != -1)
                {
                    values = new ContentValues();
                    values.put(LeagueEntry.COLUMN_LEAGUE_NAME, Constants.NAME_OPEN_LEAGUE);
                    values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);
                    values.put(LeagueEntry.COLUMN_BOWLER_ID, bowler[0].getBowlerId());
                    values.put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, 1);
                    database.insert(LeagueEntry.TABLE_NAME, null, values);
                }

                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                //does nothing
            }
            finally
            {
                database.endTransaction();
            }

            return bowler[0];
        }

        @Override
        protected void onPostExecute(Bowler newBowler)
        {
            BowlerFragment fragment = mFragment.get();

            /*
             * Adds the new bowler information to the corresponding lists
             * and displays them in the recycler view
             */
            if (newBowler != null && fragment != null && newBowler.getBowlerId() != -1)
            {
                fragment.mListBowlers.add(0, newBowler);
                fragment.mAdapterBowlers.notifyItemInserted(0);
                fragment.mRecyclerViewBowlers.scrollToPosition(0);
            }
        }
    }

    /**
     * Container Activity must implement this interface to allow LeagueEventFragment to be loaded
     * when a bowler is selected.
     */
    public interface OnBowlerSelectedListener
    {

        /**
         * Should be overridden to create a LeagueEventFragment with the leagues belonging to the
         * bowler represented by bowlerId.
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
