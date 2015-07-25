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
 * tracked by the application, and offers a callback interface {@link SeriesFragment.SeriesListener}
 * for handling interactions.
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
    private SeriesListener mSeriesListener;

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
            mainActivity.setFloatingActionButtonIcon(R.drawable.ic_add_black_24dp);
            mainActivity.setCurrentFragment(this);
            mainActivity.setDrawerState(false);
        }

        mListSeries.clear();
        mAdapterSeries.notifyDataSetChanged();

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
        mSeriesListener.onSeriesSelected(mListSeries.get(position), false);
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
        }
    }

    /**
     * Container Activity must implement this interface to allow GameFragment/StatsFragment to be
     * loaded when a series is selected.
     */
    public interface SeriesListener
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
