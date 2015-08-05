package ca.josephroque.bowlingcompanion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import ca.josephroque.bowlingcompanion.data.Series;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ChangeDateDialog;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-17. Manages the UI to display information about the series being
 * tracked by the application, and offers a callback interface {@link
 * ca.josephroque.bowlingcompanion.fragment.SeriesFragment.SeriesCallback} for handling
 * interactions.
 */
@SuppressWarnings("Convert2Lambda")
public class SeriesFragment
        extends Fragment
        implements
        Theme.ChangeableTheme,
        SeriesAdapter.SeriesEventHandler,
        ChangeDateDialog.ChangeDateDialogListener,
        FloatingActionButtonHandler
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SeriesFragment";

    /** Adapter to manage data displayed in mRecyclerViewSeries. */
    private SeriesAdapter mAdapterSeries;

    /** Callback listener for user events related to series. */
    private SeriesCallback mSeriesCallback;

    /** List to store series data from series table in database. */
    private List<Series> mListSeries;

    @Override
    public void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
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
            mSeriesCallback = (SeriesCallback) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement SeriesListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mSeriesCallback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListSeries = new ArrayList<>();

        /* View to display series dates and games to user. */
        RecyclerView recyclerViewSeries = (RecyclerView) rootView.findViewById(R.id.rv_names);
        recyclerViewSeries.setHasFixedSize(true);

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
                mListSeries.get(position).setIsDeleted(!mListSeries.get(position).wasDeleted());
                mAdapterSeries.notifyItemChanged(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewSeries);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewSeries.setLayoutManager(layoutManager);

        mAdapterSeries = new SeriesAdapter(getActivity(), this, mListSeries);
        recyclerViewSeries.setAdapter(mAdapterSeries);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getActivity() != null)
        {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_series, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = ((MainActivity) getActivity()).isDrawerOpen();
        MenuItem menuItem = menu.findItem(R.id.action_stats).setVisible(!drawerOpen);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null)
        {
            drawable.mutate();
            //noinspection CheckStyle
            drawable.setAlpha(0x8A);
        }
        menu.findItem(R.id.action_edit_date).setVisible(!drawerOpen);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_edit_date:
                showEditDateDialog();
                return true;
            case R.id.action_stats:
                if (mSeriesCallback != null)
                    mSeriesCallback.onLeagueStatsOpened();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme()
    {
        mAdapterSeries.updateTheme();
    }

    @Override
    public void onSItemClick(final int position)
    {
        //When series is clicked, its games are displayed in a new GameFragment
        if (mSeriesCallback != null)
            mSeriesCallback.onSeriesSelected(mListSeries.get(position), false);
    }

    @Override
    public void onSItemDelete(long id)
    {
        for (int i = 0; i < mListSeries.size(); i++)
        {
            if (mListSeries.get(i).getSeriesId() == id)
            {
                Series series = mListSeries.remove(i);
                mAdapterSeries.notifyItemRemoved(i);
                deleteSeries(series.getSeriesId());
            }
        }
    }

    @Override
    public void onSItemUndoDelete(long id)
    {
        for (int i = 0; i < mListSeries.size(); i++)
        {
            if (mListSeries.get(i).getSeriesId() == id)
            {
                mListSeries.get(i).setIsDeleted(false);
                mAdapterSeries.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onEditClick(final int position)
    {
        DialogFragment dateDialog = ChangeDateDialog.newInstance(this, mListSeries.get(position));
        dateDialog.show(getFragmentManager(), "ChangeDateDialog");
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public void onChangeDate(final Series series, int year, int month, int day)
    {
        final int index = mListSeries.indexOf(series);
        final Series seriesInList = mListSeries.get(index);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        final SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        final String formattedDate = dateFormat.format(c.getTime());
        seriesInList.setSeriesDate(DataFormatter.formattedDateToPrettyCompact(formattedDate.substring(
                0,
                10)));

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mAdapterSeries.notifyItemChanged(index);
            }
        });

        ((MainActivity) getActivity()).addSavingThread(
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        SQLiteDatabase database =
                                DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(SeriesEntry.COLUMN_SERIES_DATE, formattedDate);

                        database.beginTransaction();
                        try
                        {
                            database.update(SeriesEntry.TABLE_NAME,
                                    values,
                                    SeriesEntry._ID + "=?",
                                    new String[]{String.valueOf(seriesInList.getSeriesId())});
                            database.setTransactionSuccessful();
                        }
                        catch (Exception ex)
                        {
                            Log.e(TAG, "Series date was not updated", ex);
                        }
                        finally
                        {
                            database.endTransaction();
                        }
                    }
                }));
    }

    @Override
    public void onFabClick()
    {
        if (mSeriesCallback != null)
            mSeriesCallback.onCreateNewSeries(false);
    }

    /**
     * Prompts user to combine series in the league into one. Only shown if the user has not
     * disabled the option in the preferences.
     */
    private void showCombineSeriesDialog()
    {
        if (getActivity() == null)
            return;

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        if (!preferences.getBoolean(Constants.KEY_ASK_COMBINE, true))
            return;

        boolean showDialog = false;
        Series prevSeries = null;
        for (Series series : mListSeries)
        {
            if (prevSeries != null && series.getSeriesDate().equals(prevSeries.getSeriesDate()))
            {
                showDialog = true;
                break;
            }
            prevSeries = series;
        }

        if (showDialog)
        {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            View rootView = View.inflate(getActivity(), R.layout.dialog_combine_series, null);

            dialog.setView(rootView);
            final AlertDialog alertDialog = dialog.create();

            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    alertDialog.dismiss();
                    switch (v.getId())
                    {
                        case R.id.btn_combine:
                            startCombineSimilarSeries();
                            break;
                        case R.id.btn_do_not_ask:
                            preferences.edit().putBoolean(Constants.KEY_ASK_COMBINE, false)
                                    .apply();
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
    private void startCombineSimilarSeries()
    {
        if (getActivity() == null)
            return;

        new CombineSimilarSeriesTask(this).execute();
    }

    /**
     * Informs user of how to change series dates.
     */
    private void showEditDateDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_edit_date)
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
    }

    /**
     * Deletes all data regarding a certain series id in the database.
     *
     * @param seriesId id of series whose data will be deleted
     */
    private void deleteSeries(final long seriesId)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String[] whereArgs = {String.valueOf(seriesId)};
                SQLiteDatabase database =
                        DatabaseHelper.getInstance(getActivity()).getWritableDatabase();

                database.beginTransaction();
                try
                {
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.i(TAG, "Unable to delete series", ex);
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
     * @return a new instance of SeriesFragment
     */
    public static SeriesFragment newInstance()
    {
        return new SeriesFragment();
    }

    /**
     * Loads series relevant to the current bowler and league, and displays them in the recycler
     * view.
     */
    private static final class LoadSeriesTask
            extends AsyncTask<Void, Void, List<Series>>
    {

        /** Weak reference to the parent fragment. */
        private WeakReference<SeriesFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private LoadSeriesTask(SeriesFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute()
        {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            fragment.mListSeries.clear();
            fragment.mAdapterSeries.notifyDataSetChanged();
        }

        @Override
        protected List<Series> doInBackground(Void... params)
        {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null || mFragment.get().getActivity() == null)
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
                    + GameEntry.COLUMN_GAME_NUMBER
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
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    long seriesId = cursor.getLong(cursor.getColumnIndex("sid"));
                    String seriesDate = cursor.getString(
                            cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE));
                    short gameScore = cursor.getShort(
                            cursor.getColumnIndex(GameEntry.COLUMN_SCORE));

                    if (listSeries.size() == 0
                            || listSeries.get(listSeries.size() - 1).getSeriesId() != seriesId)
                        listSeries.add(new Series(seriesId,
                                DataFormatter.formattedDateToPrettyCompact(seriesDate),
                                new ArrayList<Short>()));

                    listSeries.get(listSeries.size() - 1).getSeriesGames().add(gameScore);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return listSeries;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(List<Series> listSeries)
        {
            SeriesFragment fragment = mFragment.get();
            if (listSeries == null || fragment == null)
                return;

            fragment.mListSeries.addAll(listSeries);
            fragment.mAdapterSeries.notifyDataSetChanged();
            fragment.showCombineSeriesDialog();
        }
    }

    /**
     * Combines series with similar dates in the database into singular series, with maximum 5
     * games. If a combined series would have more than 5 series, extra games are moved into a new
     * series.
     */
    private static final class CombineSimilarSeriesTask
            extends AsyncTask<Void, Void, Void>
    {

        /** Progress dialog. */
        private WeakReference<ProgressDialog> mProgressDialog;

        /** Weak reference to the parent fragment. */
        private WeakReference<SeriesFragment> mFragment;

        /**
         * Assigns a weak reference to the parent fragment.
         *
         * @param fragment parent fragment
         */
        private CombineSimilarSeriesTask(SeriesFragment fragment)
        {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute()
        {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null || fragment.getActivity() == null)
                return;

            ProgressDialog pd = new ProgressDialog(fragment.getActivity());
            pd.setTitle("Combining series...");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            mProgressDialog = new WeakReference<>(pd);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            SeriesFragment fragment = mFragment.get();
            if (fragment == null)
            {
                dismissDialog();
                return null;
            }
            MainActivity mainActivity = (MainActivity) fragment.getActivity();
            if (mainActivity == null)
            {
                dismissDialog();
                return null;
            }

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
                    + " ORDER BY " + SeriesEntry.COLUMN_SERIES_DATE + " DESC, "
                    + GameEntry.COLUMN_GAME_NUMBER;
            Cursor cursor = db.rawQuery(seriesQuery,
                    new String[]{String.valueOf(mainActivity.getLeagueId())});

            String lastSeriesDate = null;
            int startOfLastSeries = -1;
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    int gameNumber = cursor.getInt(cursor.getColumnIndex(
                            GameEntry.COLUMN_GAME_NUMBER));
                    if (gameNumber == 1)
                    {
                        String seriesDate = cursor.getString(cursor.getColumnIndex(
                                SeriesEntry.COLUMN_SERIES_DATE));
                        String dateFormatted = DataFormatter.formattedDateToPrettyCompact(
                                seriesDate);

                        if (dateFormatted.equals(lastSeriesDate))
                        {
                            int startOfCurrentSeries = cursor.getPosition();
                            combineSeries(db, cursor, startOfLastSeries, startOfCurrentSeries);
                            cursor.moveToPosition(startOfCurrentSeries);
                        }

                        startOfLastSeries = cursor.getPosition();
                        lastSeriesDate = dateFormatted;
                    }

                    cursor.moveToNext();
                }
            }
            if (!cursor.isClosed())
                cursor.close();

            return null;
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
                                   int startOfSecondSeries)
        {
            int firstSeriesNumGames = startOfSecondSeries - startOfFirstSeries;
            int secondSeriesNumGames = 0;
            cursor.moveToPosition(startOfFirstSeries);
            long firstSeriesId = cursor.getLong(cursor.getColumnIndex("sid"));
            cursor.moveToPosition(startOfSecondSeries);
            long secondSeriesId = cursor.getLong(cursor.getColumnIndex("sid"));

            while (!cursor.isAfterLast()
                    && cursor.getLong(cursor.getColumnIndex("sid")) == secondSeriesId)
            {
                secondSeriesNumGames++;
                cursor.moveToNext();
            }

            try
            {
                db.beginTransaction();

                while (secondSeriesNumGames > 0
                        && firstSeriesNumGames <= Constants.MAX_NUMBER_LEAGUE_GAMES)
                {
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
                while (secondSeriesNumGames > 0)
                {
                    cursor.moveToPosition(startOfSecondSeries);
                    long gameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    ContentValues values = new ContentValues();
                    values.put(GameEntry.COLUMN_GAME_NUMBER, secondSeriesNewNumGames + 1);
                    db.update(GameEntry.TABLE_NAME,
                            values,
                            GameEntry._ID + "=?",
                            new String[]{Long.toString(gameId)});

                    secondSeriesNewNumGames++;
                    secondSeriesNumGames--;
                }

                db.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Error combining series", ex);
            }
            finally
            {
                db.endTransaction();
            }
        }

        /**
         * Dismisses the progress dialog if it is still showing.
         */
        private void dismissDialog()
        {
            ProgressDialog pd = mProgressDialog.get();
            if (pd != null)
                pd.dismiss();
        }

        @Override
        protected void onPostExecute(Void result)
        {
            dismissDialog();
            SeriesFragment fragment = mFragment.get();
            if (fragment == null)
                return;

            new LoadSeriesTask(fragment).execute();
        }
    }

    /**
     * Container Activity must implement this interface to allow GameFragment/StatsFragment to be
     * loaded when a series is selected.
     */
    public interface SeriesCallback
    {

        /**
         * Should be overridden to created a GameFragment with the games belonging to the series
         * represented by seriesId.
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
