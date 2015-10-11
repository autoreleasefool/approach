package ca.josephroque.bowlingcompanion.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.SeriesAdapter;
import ca.josephroque.bowlingcompanion.bowling.Series;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ChangeDateDialog;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DateUtils;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-17. Manages the UI to display information about the series being tracked by the
 * application, and offers a callback interface {@code SeriesFragment.SeriesCallback} for handling interactions.
 */
public class SeriesFragment
        extends Fragment
        implements
        Theme.ChangeableTheme,
        SeriesAdapter.SeriesEventHandler,
        ChangeDateDialog.ChangeDateDialogListener,
        FloatingActionButtonHandler {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SeriesFragment";

    /** Adapter to manage data displayed in mRecyclerViewSeries. */
    private SeriesAdapter mAdapterSeries;

    /** Callback listener for user events related to series. */
    private SeriesCallback mSeriesCallback;

    /** List to store series data from series table in database. */
    private List<Series> mListSeries;

    /** Indicates if the dialog to display the combine dialog has already been shown. */
    private boolean mCombineDialogShown;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
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
            mSeriesCallback = (SeriesCallback) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement SeriesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSeriesCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListSeries = new ArrayList<>();

        /* View to display series dates and games to user. */
        RecyclerView recyclerViewSeries = (RecyclerView) rootView.findViewById(R.id.rv_names);
        recyclerViewSeries.setHasFixedSize(true);

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
                mListSeries.get(position).setIsDeleted(!mListSeries.get(position).wasDeleted());
                mAdapterSeries.notifyItemChanged(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewSeries);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewSeries.setLayoutManager(layoutManager);

        mAdapterSeries = new SeriesAdapter(getActivity(), this, mListSeries);
        recyclerViewSeries.setAdapter(mAdapterSeries);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setActionBarTitle(R.string.title_fragment_series, true);
            mainActivity.setFloatingActionButtonState(R.drawable.ic_add_black_24dp);
            mainActivity.setDrawerState(false);
        }

        updateTheme();

        //Creates AsyncTask to load data from database
        new LoadSeriesTask(this).execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCombineDialogShown = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_series, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_stats).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
            drawable.setAlpha(DisplayUtils.BLACK_ICON_ALPHA);
        menu.findItem(R.id.action_combine_series).setVisible(!drawerOpen
                && ((MainActivity) getActivity()).getLeagueName().substring(1).equals(
                Constants.NAME_OPEN_LEAGUE));
        menu.findItem(R.id.action_edit_date).setVisible(!drawerOpen);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_date:
                showEditDateDialog();
                return true;
            case R.id.action_stats:
                if (mSeriesCallback != null)
                    mSeriesCallback.onLeagueStatsOpened();
                return true;
            case R.id.action_combine_series:
                showCombineSeriesDialog(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme() {
        mAdapterSeries.updateTheme();
    }

    @Override
    public void onSItemClick(final int position) {
        //When series is clicked, its games are displayed in a new GameFragment
        if (mSeriesCallback != null)
            mSeriesCallback.onSeriesSelected(mListSeries.get(position), false);
    }

    @Override
    public void onSItemDelete(long id) {
        for (int i = 0; i < mListSeries.size(); i++) {
            if (mListSeries.get(i).getSeriesId() == id) {
                Series series = mListSeries.remove(i);
                mAdapterSeries.notifyItemRemoved(i);
                deleteSeries(series.getSeriesId());
            }
        }
    }

    @Override
    public void onSItemUndoDelete(long id) {
        for (int i = 0; i < mListSeries.size(); i++) {
            if (mListSeries.get(i).getSeriesId() == id) {
                mListSeries.get(i).setIsDeleted(false);
                mAdapterSeries.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onEditClick(final int position) {
        DialogFragment dateDialog = ChangeDateDialog.newInstance(this, mListSeries.get(position));
        dateDialog.show(getFragmentManager(), "ChangeDateDialog");
    }

    @Override
    public void onChangeDate(final Series series, int year, int month, int day) {
        final int index = mListSeries.indexOf(series);
        final Series seriesInList = mListSeries.get(index);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        final SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        final String formattedDate = dateFormat.format(c.getTime());
        seriesInList.setSeriesDate(DateUtils.formattedDateToPrettyCompact(formattedDate.substring(
                0,
                DateUtils.LENGTH_OF_DATE_MINUS_TIME)));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterSeries.notifyItemChanged(index);
            }
        });

        ((MainActivity) getActivity()).addSavingThread(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase database =
                                DatabaseHelper.getInstance(getContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(SeriesEntry.COLUMN_SERIES_DATE, formattedDate);

                        database.beginTransaction();
                        try {
                            database.update(SeriesEntry.TABLE_NAME,
                                    values,
                                    SeriesEntry._ID + "=?",
                                    new String[]{String.valueOf(seriesInList.getSeriesId())});
                            database.setTransactionSuccessful();
                        } catch (Exception ex) {
                            Log.e(TAG, "Series date was not updated.", ex);
                        } finally {
                            database.endTransaction();
                        }
                    }
                }));
    }

    @Override
    public void onFabClick() {
        if (mSeriesCallback != null)
            mSeriesCallback.onCreateNewSeries(false);
    }

    /**
     * Prompts user to combine series in the league into one. Only shown if the user has not disabled the option in the
     * preferences.
     *
     * @param manuallyOpened if true, the dialog will be shown regardless of their settings, or if they have seen it
     * before.
     */
    private void showCombineSeriesDialog(boolean manuallyOpened) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null || (mCombineDialogShown && !manuallyOpened)
                || !mainActivity.getLeagueName().substring(1).equals(Constants.NAME_OPEN_LEAGUE))
            return;

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!preferences.getBoolean(Constants.KEY_ASK_COMBINE, true) && !manuallyOpened)
            return;

        mCombineDialogShown = true;
        boolean showDialog = false;
        Series prevSeries = null;
        for (Series series : mListSeries) {
            if (prevSeries != null && series.getSeriesDate().equals(prevSeries.getSeriesDate())) {
                showDialog = true;
                break;
            }
            prevSeries = series;
        }

        if (showDialog) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            View rootView = View.inflate(getContext(), R.layout.dialog_combine_series, null);

            dialog.setView(rootView);
            final AlertDialog alertDialog = dialog.create();

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    switch (v.getId()) {
                        case R.id.btn_combine:
                            startCombineSimilarSeries();
                            break;
                        case R.id.btn_do_not_ask:
                            preferences.edit().putBoolean(Constants.KEY_ASK_COMBINE, false).apply();
                            break;
                        default:
                            // do nothing
                    }
                }
            };

            rootView.findViewById(R.id.btn_combine).setOnClickListener(listener);
            rootView.findViewById(R.id.btn_do_not_combine).setOnClickListener(listener);
            rootView.findViewById(R.id.btn_do_not_ask).setOnClickListener(listener);

            alertDialog.show();
        }
    }

    /**
     * Displays a modal dialog to the user while similar series in the league are combined.
     */
    private void startCombineSimilarSeries() {
        if (getContext() == null)
            return;

        new CombineSimilarSeriesTask(this).execute();
    }

    /**
     * Informs user of how to change series dates.
     */
    private void showEditDateDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.dialog_edit_date)
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
     * Deletes all data regarding a certain series id in the database.
     *
     * @param seriesId id of series whose data will be deleted
     */
    private void deleteSeries(final long seriesId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] whereArgs = {String.valueOf(seriesId)};
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(getContext()).getWritableDatabase();

                database.beginTransaction();
                try {
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.i(TAG, "Unable to delete series", ex);
                } finally {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Creates a new instance of this fragment to display.
     *
     * @return a new instance of SeriesFragment
     */
    public static SeriesFragment newInstance() {
        return new SeriesFragment();
    }

    /**
     * Loads series relevant to the current bowler and league, and displays them in the recycler view.
     */
    private static final class LoadSeriesTask
            extends AsyncTask<Void, Void, List<Series>> {

        /** Weak reference to the parent fragment. */
        private final WeakReference<SeriesFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadSeriesTask(SeriesFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mListSeries.clear();
            fragment.mAdapterSeries.notifyDataSetChanged();
        }

        @Override
        protected List<Series> doInBackground(Void... params) {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded() || mFragment.get().getActivity() == null)
                return null;
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
                return null;

            MainActivity.waitForSaveThreads(new WeakReference<>(mainActivity));

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            List<Series> listSeries = new ArrayList<>();

            String rawSeriesQuery = "SELECT "
                    + "series." + SeriesEntry._ID + " AS sid, "
                    + SeriesEntry.COLUMN_SERIES_DATE + ", "
                    + GameEntry.COLUMN_SCORE + ", "
                    + GameEntry.COLUMN_GAME_NUMBER + ", "
                    + GameEntry.COLUMN_MATCH_PLAY
                    + " FROM " + SeriesEntry.TABLE_NAME + " AS series"
                    + " INNER JOIN " + GameEntry.TABLE_NAME
                    + " ON sid=" + GameEntry.COLUMN_SERIES_ID
                    + " WHERE " + SeriesEntry.COLUMN_LEAGUE_ID + "=?"
                    + " ORDER BY " + SeriesEntry.COLUMN_SERIES_DATE + " DESC, "
                    + GameEntry.COLUMN_GAME_NUMBER;
            String[] rawSeriesArgs = {
                    String.valueOf(mainActivity.getLeagueId())
            };

            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long seriesId = cursor.getLong(cursor.getColumnIndex("sid"));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE));
                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));
                    byte matchPlay = (byte) cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));

                    if (listSeries.size() == 0 || listSeries.get(listSeries.size() - 1).getSeriesId() != seriesId) {
                        listSeries.add(new Series(seriesId,
                                DateUtils.formattedDateToPrettyCompact(seriesDate),
                                new ArrayList<Short>(),
                                new ArrayList<Byte>()));
                    }

                    listSeries.get(listSeries.size() - 1).getSeriesMatchPlayResults().add(matchPlay);
                    listSeries.get(listSeries.size() - 1).getSeriesGames().add(gameScore);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return listSeries;
        }

        @Override
        protected void onPostExecute(List<Series> listSeries) {
            SeriesFragment fragment = mFragment.get();
            if (listSeries == null || fragment == null)
                return;

            fragment.mListSeries.addAll(listSeries);
            fragment.mAdapterSeries.notifyDataSetChanged();
            fragment.showCombineSeriesDialog(false);
        }
    }

    /**
     * Combines series with similar dates in the database into singular series, with maximum 5 games. If a combined
     * series would have more than 5 series, extra games are moved into a new series.
     */
    private static final class CombineSimilarSeriesTask
            extends AsyncTask<Void, Void, Void> {

        /** Progress dialog. */
        private WeakReference<ProgressDialog> mProgressDialog;

        /** Weak reference to the parent fragment. */
        private final WeakReference<SeriesFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private CombineSimilarSeriesTask(SeriesFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null || fragment.getContext() == null)
                return;

            ProgressDialog pd = new ProgressDialog(fragment.getContext());
            pd.setTitle("Combining series...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            mProgressDialog = new WeakReference<>(pd);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final SeriesFragment fragment = mFragment.get();
            if (fragment == null || !fragment.isAdded()) {
                dismissDialog();
                return null;
            }
            final MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null) {
                dismissDialog();
                return null;
            }

            mainActivity.addSavingThread(new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            SQLiteDatabase db = DatabaseHelper.getInstance(mainActivity)
                                    .getReadableDatabase();
                            String seriesQuery = "SELECT "
                                    + "s." + SeriesEntry._ID + " AS sid, "
                                    + SeriesEntry.COLUMN_SERIES_DATE + ", "
                                    + "g." + GameEntry._ID + " AS gid, "
                                    + GameEntry.COLUMN_SERIES_ID + ", "
                                    + GameEntry.COLUMN_GAME_NUMBER
                                    + " FROM " + SeriesEntry.TABLE_NAME + " AS s"
                                    + " INNER JOIN " + GameEntry.TABLE_NAME + " AS g"
                                    + " ON g." + GameEntry.COLUMN_SERIES_ID + "=sid"
                                    + " WHERE " + SeriesEntry.COLUMN_LEAGUE_ID + "=?"
                                    + " ORDER BY " + SeriesEntry.COLUMN_SERIES_DATE + ", "
                                    + GameEntry.COLUMN_GAME_NUMBER;
                            Cursor cursor = db.rawQuery(seriesQuery,
                                    new String[]{String.valueOf(mainActivity.getLeagueId())});

                            searchForSeriesToCombine(db, cursor);
                            if (!cursor.isClosed())
                                cursor.close();

                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    new LoadSeriesTask(fragment).execute();
                                }
                            });
                        }
                    }));

            return null;
        }

        /**
         * Attempts to combine series with less than 5 games each into 1 series with 5 games.
         *
         * @param db app database
         * @param cursor cursor containing series from database
         */
        private void searchForSeriesToCombine(SQLiteDatabase db, Cursor cursor) {
            String lastSeriesDate = null;
            int startOfLastSeries = -1;
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int gameNumber = cursor.getInt(cursor.getColumnIndex(
                            GameEntry.COLUMN_GAME_NUMBER));
                    if (gameNumber == 1) {
                        String seriesDate = cursor.getString(cursor.getColumnIndex(
                                SeriesEntry.COLUMN_SERIES_DATE));
                        String dateFormatted = DateUtils.formattedDateToPrettyCompact(seriesDate);

                        if (dateFormatted.equals(lastSeriesDate)) {
                            int startOfCurrentSeries = cursor.getPosition();
                            combineSeries(db,
                                    cursor,
                                    startOfLastSeries,
                                    startOfCurrentSeries);
                            cursor.moveToPosition(startOfCurrentSeries);
                        }

                        startOfLastSeries = cursor.getPosition();
                        lastSeriesDate = dateFormatted;
                    }

                    cursor.moveToNext();
                }
            }
        }

        /**
         * Combines two series into one.
         *
         * @param db database
         * @param cursor cursor with series date
         * @param startOfFirstSeries position in cursor of game 1 of the first series
         * @param startOfSecondSeries position in cursor of game 1 of the second series
         */
        private void combineSeries(SQLiteDatabase db,
                                   Cursor cursor,
                                   int startOfFirstSeries,
                                   int startOfSecondSeries) {
            int firstSeriesNumGames = startOfSecondSeries - startOfFirstSeries;
            int secondSeriesNumGames = 0;
            cursor.moveToPosition(startOfFirstSeries);
            long firstSeriesId = cursor.getLong(cursor.getColumnIndex("sid"));
            cursor.moveToPosition(startOfSecondSeries);
            long secondSeriesId = cursor.getLong(cursor.getColumnIndex("sid"));

            while (!cursor.isAfterLast()
                    && cursor.getLong(cursor.getColumnIndex("sid")) == secondSeriesId) {
                secondSeriesNumGames++;
                cursor.moveToNext();
            }

            try {
                db.beginTransaction();

                while (secondSeriesNumGames > 0
                        && firstSeriesNumGames < Constants.MAX_NUMBER_LEAGUE_GAMES) {
                    cursor.moveToPosition(startOfSecondSeries);
                    long gameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    ContentValues values = new ContentValues();
                    values.put(GameEntry.COLUMN_GAME_NUMBER, firstSeriesNumGames + 1);
                    values.put(GameEntry.COLUMN_SERIES_ID, firstSeriesId);
                    db.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{Long.toString(gameId)});

                    firstSeriesNumGames++;
                    secondSeriesNumGames--;
                    startOfSecondSeries++;
                }

                int secondSeriesNewNumGames = 0;
                while (secondSeriesNumGames > 0) {
                    cursor.moveToPosition(startOfSecondSeries);
                    long gameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    ContentValues values = new ContentValues();
                    values.put(GameEntry.COLUMN_GAME_NUMBER, secondSeriesNewNumGames + 1);
                    db.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{Long.toString(gameId)});

                    startOfSecondSeries++;
                    secondSeriesNewNumGames++;
                    secondSeriesNumGames--;
                }

                db.setTransactionSuccessful();
            } catch (Exception ex) {
                Log.e(TAG, "Error combining series", ex);
            } finally {
                db.endTransaction();
            }
        }

        /**
         * Dismisses the progress dialog if it is still showing.
         */
        private void dismissDialog() {
            ProgressDialog pd = mProgressDialog.get();
            if (pd != null)
                pd.dismiss();
        }
    }

    /**
     * Container Activity must implement this interface to allow GameFragment/StatsFragment to be loaded when a series
     * is selected.
     */
    public interface SeriesCallback {

        /**
         * Should be overridden to created a GameFragment with the games belonging to the series represented by
         * seriesId.
         *
         * @param series series whose games will be displayed
         * @param isEvent indicates if an event series is being displayed or not
         */
        void onSeriesSelected(Series series, boolean isEvent);

        /**
         * Called when user opts to create a new series.
         *
         * @param isEvent indicates if the new series will belong to an event
         */
        void onCreateNewSeries(boolean isEvent);

        /**
         * Displays the stats of the current league in a new StatsFragment.
         */
        void onLeagueStatsOpened();
    }
}
