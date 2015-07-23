package ca.josephroque.bowlingcompanion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ca.josephroque.bowlingcompanion.DividerItemDecoration;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.SeriesAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.ChangeDateDialog;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;

/**
 * Created by Joseph Roque on 15-03-17.
 * <p/>
 * Manages the UI to display information about the series being tracked by the application,
 * and offers a callback interface {@link SeriesFragment.SeriesListener} for
 * handling interactions.
 */
@SuppressWarnings("Convert2Lambda")
public class SeriesFragment extends Fragment
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
    private SeriesListener mSeriesListener;

    /** List of series ids from "series" table in database to uniquely identify series. */
    private List<Long> mListSeriesIds;
    /** List of series dates which will be displayed by RecyclerView. */
    private List<String> mListSeriesDates;
    /** List of scores in each series which will be displayed by RecyclerView. */
    private List<List<Short>> mListSeriesGames;

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
            mSeriesListener = (SeriesListener) activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement SeriesListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mListSeriesIds = new ArrayList<>();
        mListSeriesDates = new ArrayList<>();
        mListSeriesGames = new ArrayList<>();

        /* View to display series dates and games to user. */
        RecyclerView recyclerViewSeries = (RecyclerView) rootView.findViewById(R.id.rv_names);
        recyclerViewSeries.setHasFixedSize(true);
        recyclerViewSeries.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction)
            {
                final int position = viewHolder.getAdapterPosition();
                final long deletedId = mListSeriesIds.remove(position);
                final String deletedName = mListSeriesDates.remove(position);
                final List<Short> deletedScores = mListSeriesGames.remove(position);
                mAdapterSeries.notifyItemRemoved(position);

                final Handler handler = new Handler(Looper.getMainLooper());
                final Runnable deleteSeries = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        deleteSeries(deletedId);
                    }
                };
                handler.postDelayed(deleteSeries, 4000);
                Snackbar.make(rootView, deletedName + " deleted", Snackbar.LENGTH_LONG)
                        .setAction(R.string.text_undo, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                handler.removeCallbacks(deleteSeries);
                                mListSeriesIds.add(position, deletedId);
                                mListSeriesDates.add(position, deletedName);
                                mListSeriesGames.add(position, deletedScores);
                                mAdapterSeries.notifyItemInserted(position);
                            }
                        })
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewSeries);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewSeries.setLayoutManager(layoutManager);

        mAdapterSeries = new SeriesAdapter(getActivity(), this, mListSeriesDates, mListSeriesGames);
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
            mainActivity.setFloatingActionButtonIcon(R.drawable.ic_add_black_24dp);
            mainActivity.setCurrentFragment(this);
            mainActivity.setDrawerState(false);
        }

        mListSeriesIds.clear();
        mListSeriesDates.clear();
        mListSeriesGames.clear();
        mAdapterSeries.notifyDataSetChanged();

        updateTheme();

        //Creates AsyncTask to load data from database
        new LoadSeriesTask().execute();
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
                mSeriesListener.onLeagueStatsOpened();
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
        mSeriesListener.onSeriesSelected(
                mListSeriesIds.get(position), mListSeriesDates.get(position), false);
    }

    @Override
    public void onEditClick(final int position)
    {
        DialogFragment dateDialog = ChangeDateDialog.newInstance(
                this, mListSeriesDates.get(position), mListSeriesIds.get(position));
        dateDialog.show(getFragmentManager(), "ChangeDateDialog");
    }

    @Override
    public void onChangeDate(final long seriesId, int year, int month, int day)
    {
        final int index = mListSeriesIds.indexOf(seriesId);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        final SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        final String formattedDate = dateFormat.format(c.getTime());
        mListSeriesDates.set(index,
                DataFormatter.formattedDateToPrettyCompact(formattedDate.substring(0, 10)));

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mAdapterSeries.notifyItemChanged(index);
            }
        });

        ((MainActivity) getActivity()).addSavingThread(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SQLiteDatabase database =
                                DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(SeriesEntry.COLUMN_SERIES_DATE, formattedDate);

                        database.beginTransaction();
                        try {
                            database.update(SeriesEntry.TABLE_NAME,
                                    values,
                                    SeriesEntry._ID + "=?",
                                    new String[]{String.valueOf(seriesId)});
                            database.setTransactionSuccessful();
                        } catch (Exception ex) {
                            //TODO: does nothing - date in database for series was not changed
                        } finally {
                            database.endTransaction();
                        }
                    }
                }));
    }

    @Override
    public void onFabClick()
    {
        mSeriesListener.onCreateNewSeries(false);
    }

    /**
     * Informs user of how to change series dates.
     */
    private void showEditDateDialog()
    {
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_edit_date)
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
                    //TODO: does nothing - series was not deleted
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
     * Loads series relevant to the current bowler and league,
     * and displays them in the recycler view.
     */
    private class LoadSeriesTask extends AsyncTask<Void, Void, List<?>[]>
    {
        @Override
        protected List<?>[] doInBackground(Void... params)
        {
            MainActivity.waitForSaveThreads((MainActivity) getActivity());

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
            List<Long> listSeriesIds = new ArrayList<>();
            List<String> listSeriesDates = new ArrayList<>();
            List<List<Short>> listSeriesGames = new ArrayList<>();

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
            String[] rawSeriesArgs = {String.valueOf(((MainActivity) getActivity()).getLeagueId())};

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

                    if (listSeriesIds.size() == 0
                            || !listSeriesIds.get(listSeriesIds.size() - 1).equals(seriesId))
                    {
                        listSeriesIds.add(seriesId);
                        listSeriesDates.add(DataFormatter.formattedDateToPrettyCompact(seriesDate));
                        listSeriesGames.add(new ArrayList<Short>());
                    }

                    listSeriesGames.get(listSeriesGames.size() - 1).add(gameScore);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return new List<?>[]{listSeriesIds, listSeriesDates, listSeriesGames};
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(List<?>[] lists)
        {
            mListSeriesIds.addAll((List<Long>) lists[0]);
            mListSeriesDates.addAll((List<String>) lists[1]);
            mListSeriesGames.addAll((List<List<Short>>) lists[2]);
            mAdapterSeries.notifyDataSetChanged();
        }
    }

    /**
     * Container Activity must implement this interface to allow
     * GameFragment/StatsFragment to be loaded when a series is selected.
     */
    public interface SeriesListener
    {
        /**
         * Should be overridden to created a GameFragment with the games
         * belonging to the series represented by seriesId.
         *
         * @param seriesId id of the series whose games will be displayed
         * @param seriesDate date of the series corresponding to seriesId
         * @param isEvent indicates if an event series is being displayed or not
         */
        void onSeriesSelected(long seriesId, String seriesDate, boolean isEvent);

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
