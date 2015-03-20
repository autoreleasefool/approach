package ca.josephroque.bowlingcompanion.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.DividerItemDecoration;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.SeriesAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-17.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class SeriesFragment extends Fragment
    implements
        Theme.ChangeableTheme,
        SeriesAdapter.SeriesEventHandler
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "SeriesFragment";

    /** View to display series dates and games to user */
    private RecyclerView mRecyclerViewSeries;
    /** Adapter to manage data displayed in mRecyclerViewSeries */
    private SeriesAdapter mAdapterSeries;

    /** List of series ids from "series" table in database to uniquely identify series */
    private List<Long> mListSeriesIds;
    /** List of series dates which will be displayed by RecyclerView */
    private List<String> mListSeriesDates;
    /** List of scores in each series which will be displayed by RecyclerView */
    private List<List<Short>> mListSeriesGames;

    @Override
    public void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fab_list, container, false);

        mListSeriesIds = new ArrayList<>();
        mListSeriesDates = new ArrayList<>();
        mListSeriesGames = new ArrayList<>();

        mRecyclerViewSeries = (RecyclerView)rootView.findViewById(R.id.rv_names);
        mRecyclerViewSeries.setHasFixedSize(true);
        mRecyclerViewSeries.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewSeries.setLayoutManager(layoutManager);

        mAdapterSeries = new SeriesAdapter(this, mListSeriesDates, mListSeriesGames);
        mRecyclerViewSeries.setAdapter(mAdapterSeries);

        FloatingActionButton floatingActionButton =
                (FloatingActionButton)rootView.findViewById(R.id.fab_new_list_item);
        floatingActionButton.setImageResource(R.drawable.ic_action_new);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: addNewLeagueSeries();
            }
        });

        ((TextView)rootView.findViewById(R.id.tv_new_list_item)).setText(R.string.text_new_series);
        ((TextView)rootView.findViewById(R.id.tv_delete_list_item)).setText(R.string.text_delete_series);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle(R.string.title_series);

        mListSeriesIds.clear();
        mListSeriesDates.clear();
        mListSeriesGames.clear();
        mAdapterSeries.notifyDataSetChanged();

        //Updates theme if invalid
        if (Theme.getSeriesFragmentThemeInvalidated())
        {
            updateTheme();
        }

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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_edit_date:
                //TODO: edit date
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateTheme()
    {
        //Updates colors of views and sets theme for this object to a 'valid' state
        View rootView = getView();
        if (rootView != null)
        {
            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_new_list_item);
            fab.setColorNormal(Theme.getPrimaryThemeColor());
            fab.setColorPressed(Theme.getPrimaryThemeColor());
            fab.setColorRipple(Theme.getTertiaryThemeColor());
        }
        mAdapterSeries.updateTheme();
        Theme.validateSeriesFragmentTheme();
    }

    @Override
    public void onSItemClick(final int position)
    {
        /*new OpenSeriesTask().execute(getActivity(),
                mListSeriesIds.get(position),
                mListSeriesGames.get(position).size());*/
    }

    @Override
    public void onSLongClick(final int position)
    {
        showDeleteSeriesDialog(position);
    }

    @Override
    public int getSeriesViewPositionInRecyclerView(View v)
    {
        //Gets position of view in mRecyclerViewSeries
        return mRecyclerViewSeries.getChildPosition(v);
    }

    /**
     * Prompts user with a dialog to delete all data regarding a certain
     * series in the database
     *
     * @param position position of series id in mListSeriesIds
     */
    private void showDeleteSeriesDialog(int position)
    {
        final String seriesDate = mListSeriesDates.get(position);
        final long seriesId = mListSeriesIds.get(position);

        DatabaseHelper.deleteData(getActivity(),
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteSeries(seriesId);
                    }
                },
                seriesDate);
    }

    /**
     * Deletes all data regarding a certain series id in the database
     * @param seriesId id of series whose data will be deleted
     */
    private void deleteSeries(final long seriesId)
    {
        final int index = mListSeriesIds.indexOf(seriesId);
        final String seriesDate = mListSeriesDates.remove(index);
        mListSeriesGames.remove(index);
        mListSeriesIds.remove(index);
        mAdapterSeries.notifyDataSetChanged();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String[] whereArgs = {String.valueOf(seriesId)};
                SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();

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
                    Log.w(TAG, "Error deleting series: " + seriesDate);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    /**
     * Creates a new instance of this fragment to display
     * @return a new instance of SeriesFragment
     */
    public static SeriesFragment newInstance()
    {
        return new SeriesFragment();
    }

    /**
     * Loads series relevant to the current bowler and league,
     * and displays them in the recycler view
     */
    private class LoadSeriesTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
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
            String[] rawSeriesArgs = {String.valueOf(((MainActivity)getActivity()).getLeagueId())};

            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    long seriesId = cursor.getLong(cursor.getColumnIndex("sid"));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE));
                    short gameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE));

                    if (mListSeriesIds.size() == 0 || !mListSeriesIds.get(mListSeriesIds.size() - 1).equals(seriesId))
                    {
                        mListSeriesIds.add(seriesId);
                        mListSeriesDates.add(DataFormatter.formattedDateToPrettyCompact(seriesDate));
                        mListSeriesGames.add(new ArrayList<Short>());
                    }

                    mListSeriesGames.get(mListSeriesGames.size() - 1).add(gameScore);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mAdapterSeries.notifyDataSetChanged();
        }
    }
}
