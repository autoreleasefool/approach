package ca.josephroque.bowlingcompanion.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import ca.josephroque.bowlingcompanion.dialog.NewBowlerDialog;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-13.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragment
 * in project Bowling Companion
 */
public class BowlerFragment extends Fragment
    implements Theme.ChangeableTheme, NameAverageAdapter.NameAverageEventHandler, NewBowlerDialog.NewBowlerDialogListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "BowlerFragment";

    /** List to store ids from bowler table in database */
    private List<Long> mListBowlerIds;
    /** List to store names of bowlers, relevant to order of mListBowlerIds */
    private List<String> mListBowlerNames;
    /** List to store averages of bowlers, relevant to order of mListBowlerIds */
    private List<Short> mListBowlerAverages;

    /** View to display bowler names and averages to user */
    private RecyclerView mRecyclerViewBowlers;
    /** Adapter to manage data displayed in mRecyclerViewBowlers */
    private NameAverageAdapter mAdapterBowlers;

    /** Id from 'bowler' database which represents the most recently used bowler */
    private long mRecentBowlerId = -1;
    /** Id from 'league' database whuch represents the most recently edited league */
    private long mRecentLeagueId = -1;
    /** Number of games in the most recently edited league */
    private byte mRecentNumberOfGames = -1;
    /** Name of most recently edited bowler */
    private String mRecentBowlerName;
    /** Name of most recently edited league */
    private String mRecentLeagueName;

    /** Id from 'bowler' database which represents the preferred bowler for a quick series */
    private long mQuickBowlerId = -1;
    /** Id from 'league' database which represents the preferred league for quick series */
    private long mQuickLeagueId = -1;
    /** Number of games in the preferred league */
    private byte mQuickNumberOfGames = -1;
    /** Name of preferred bowler */
    private String mQuickBowlerName;
    /** Name of preferred league */
    private String mQuickLeagueName;

    /** Listener for user events */
    private OnBowlerSelectedListener mBowlerSelectedListener;
    private LeagueEventFragment.OnLeagueSelectedListener mLeagueSelectedListener;
    private SeriesFragment.SeriesListener mSeriesListener;

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
            mBowlerSelectedListener = (OnBowlerSelectedListener)activity;
            mLeagueSelectedListener = (LeagueEventFragment.OnLeagueSelectedListener)activity;
            mSeriesListener = (SeriesFragment.SeriesListener)activity;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBowlerSelectedListener, OnLeagueSelectedListener, SeriesListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_fab_list, container, false);

        mListBowlerIds = new ArrayList<>();
        mListBowlerNames = new ArrayList<>();
        mListBowlerAverages = new ArrayList<>();

        mRecyclerViewBowlers = (RecyclerView)rootView.findViewById(R.id.rv_names);
        mRecyclerViewBowlers.setHasFixedSize(true);
        mRecyclerViewBowlers.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewBowlers.setLayoutManager(layoutManager);
        mAdapterBowlers = new NameAverageAdapter(this,
                mListBowlerNames,
                mListBowlerAverages,
                NameAverageAdapter.DATA_BOWLERS);
        mRecyclerViewBowlers.setAdapter(mAdapterBowlers);

        FloatingActionButton floatingActionButton =
                (FloatingActionButton)rootView.findViewById(R.id.fab_new_list_item);
        floatingActionButton.setImageResource(R.drawable.ic_action_add_person);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showNewBowlerDialog();
            }
        });

        ((TextView)rootView.findViewById(R.id.tv_new_list_item)).setText(R.string.text_new_bowler);
        ((TextView)rootView.findViewById(R.id.tv_delete_list_item)).setText(R.string.text_delete_bowler);

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle(R.string.app_name);

        //Loads values for member variables from preferences, if they exist
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
        mRecentBowlerId = prefs.getLong(Constants.PREF_RECENT_BOWLER_ID, -1);
        mRecentLeagueId = prefs.getLong(Constants.PREF_RECENT_LEAGUE_ID, -1);
        mQuickBowlerId = prefs.getLong(Constants.PREF_QUICK_BOWLER_ID, -1);
        mQuickLeagueId = prefs.getLong(Constants.PREF_QUICK_LEAGUE_ID, -1);

        mListBowlerIds.clear();
        mListBowlerNames.clear();
        mListBowlerAverages.clear();
        mAdapterBowlers.notifyDataSetChanged();

        //Updates theme if invalid
        if (Theme.getBowlerFragmentThemeInvalidated())
        {
            updateTheme();
        }

        //Creates AsyncTask to load data from database
        new LoadBowlerAndRecentTask().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_bowlers, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_quick_series:
                showQuickSeriesDialog();
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
        Theme.validateBowlerFragmentTheme();
    }

    @Override
    public void onNAItemClick(final int position)
    {
        new OpenBowlerLeaguesTask().execute(position);
    }

    @Override
    public void onNALongClick(final int position)
    {
        showDeleteBowlerDialog(position);
    }

    @Override
    public int getNAViewPositionInRecyclerView(View v)
    {
        //Gets position of view in mRecyclerViewBowlers
        return mRecyclerViewBowlers.getChildPosition(v);
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean validInput = true;
        int invalidInputMessage = -1;

        if (mListBowlerNames.contains(bowlerName))
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
        new NewBowlerTask().execute(bowlerName);
    }

    /**
     * Prompts user to create a new series with mRecentBowlerId
     * and mRecentLeagueId or mQuickBowlerId and mQuickLeagueId
     */
    private void showQuickSeriesDialog()
    {
        if ((mQuickBowlerId > -1 && mQuickLeagueId > -1) || (mRecentBowlerId > -1 && mRecentLeagueId > -1))
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
                                Log.w(TAG, "Quick series created");
                                mBowlerSelectedListener.onBowlerSelected(mQuickBowlerId, mQuickBowlerName, false);
                                mLeagueSelectedListener.onLeagueSelected(mQuickLeagueId, mQuickLeagueName, mQuickNumberOfGames, false);
                                mSeriesListener.onCreateNewSeries(false);
                            }
                            else
                            {
                                Log.w(TAG, "Recent series created");
                                mBowlerSelectedListener.onBowlerSelected(mRecentBowlerId, mRecentBowlerName, false);
                                mLeagueSelectedListener.onLeagueSelected(mRecentLeagueId, mRecentLeagueName, mRecentNumberOfGames, false);
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
     * Opens an instance of NewBowlerDialog and displays
     * it to the user
     */
    private void showNewBowlerDialog()
    {
        DialogFragment dialogFragment = NewBowlerDialog.newInstance(this);
        dialogFragment.show(getFragmentManager(), "NewBowlerDialog");
    }

    /**
     * Prompts user with a dialog to delete all data regarding a certain
     * bowler in the database
     *
     * @param position position of bowler id in mListBowlerIds
     */
    private void showDeleteBowlerDialog(final int position)
    {
        final String bowlerName = mListBowlerNames.get(position);
        final long bowlerId = mListBowlerIds.get(position);
        DatabaseHelper.deleteData(getActivity(),
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteBowler(bowlerId);
                    }
                },
                bowlerName);
    }

    /**
     * Deletes all data regarding a certain bowler id in the database
     * @param bowlerId id of bowler whose data will be deleted
     */
    private void deleteBowler(final long bowlerId)
    {
        final int index = mListBowlerIds.indexOf(bowlerId);
        final String bowlerName = mListBowlerNames.remove(index);
        mListBowlerIds.remove(index);
        mAdapterBowlers.notifyItemRemoved(index);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
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
                SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
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
                    Log.w(TAG, "Error deleting bowler: " + bowlerName);
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
     * @return a new instance of BowlerFragment
     */
    public static BowlerFragment newInstance()
    {
        return new BowlerFragment();
    }

    /**
     * Loads names of bowlers, along with other relevant data, and adds them to recycler view
     */
    private class LoadBowlerAndRecentTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

            String rawBowlerQuery = "SELECT "
                    + "bowler." + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                    + "bowler." + BowlerEntry._ID + ", "
                    + "AVG(game." + GameEntry.COLUMN_SCORE + ") AS avg"
                    + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                    + " LEFT JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                    + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry._ID
                    + " LEFT JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                    + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry.COLUMN_LEAGUE_ID
                    + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                    + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                    + " GROUP BY bowler." + BowlerEntry._ID
                    + " ORDER BY bowler." + BowlerEntry.COLUMN_DATE_MODIFIED + " DESC";

            Cursor cursor = database.rawQuery(rawBowlerQuery, null);
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    mListBowlerIds.add(cursor.getLong(cursor.getColumnIndex(BowlerEntry._ID)));
                    mListBowlerNames.add(cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME)));
                    mListBowlerAverages.add(cursor.getShort(cursor.getColumnIndex("avg")));
                    cursor.moveToNext();
                }
            }
            cursor.close();

            if (mRecentBowlerId > -1 && mRecentLeagueId > -1)
            {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{String.valueOf(mRecentBowlerId), String.valueOf(mRecentLeagueId)};

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                if (cursor.moveToFirst())
                {
                    mRecentBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    mRecentLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    mRecentNumberOfGames = (byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                }
                else
                {
                    mRecentBowlerId = mRecentLeagueId = -1;
                }
                cursor.close();
            }

            if (mQuickBowlerId > -1 && mQuickLeagueId > -1)
            {
                String rawRecentQuery = "SELECT "
                        + BowlerEntry.COLUMN_BOWLER_NAME + ", "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_NUMBER_OF_GAMES
                        + " FROM " + BowlerEntry.TABLE_NAME + " AS bowler"
                        + " INNER JOIN " + LeagueEntry.TABLE_NAME + " AS league"
                        + " ON bowler." + BowlerEntry._ID + "=league." + LeagueEntry.COLUMN_BOWLER_ID
                        + " WHERE bowler." + BowlerEntry._ID + "=? AND league." + LeagueEntry._ID + "=?";
                String[] rawRecentArgs = new String[]{String.valueOf(mQuickBowlerId), String.valueOf(mQuickLeagueId)};

                cursor = database.rawQuery(rawRecentQuery, rawRecentArgs);
                if (cursor.moveToFirst())
                {
                    mQuickBowlerName = cursor.getString(cursor.getColumnIndex(BowlerEntry.COLUMN_BOWLER_NAME));
                    mQuickLeagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME));
                    mQuickNumberOfGames = (byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES));
                }
                else
                {
                    mQuickBowlerId = mQuickLeagueId = -1;
                }
                cursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mAdapterBowlers.notifyDataSetChanged();
        }
    }

    /**
     * Sets data to be displayed in new instance of LeagueEventFragment
     */
    private class OpenBowlerLeaguesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            long bowlerId = mListBowlerIds.get(position[0]);

            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_DATE_MODIFIED, dateFormat.format(new Date()));

            database.beginTransaction();
            try
            {
                database.update(BowlerEntry.TABLE_NAME,
                        values,
                        BowlerEntry._ID + "=?",
                        new String[]{String.valueOf(bowlerId)});
                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error updating bowler: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            return new Object[]{bowlerId, position[0]};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            long bowlerId = (Long)params[0];
            int position = (Integer)params[1];

            mBowlerSelectedListener.onBowlerSelected(bowlerId, mListBowlerNames.get(position), true);
        }
    }

    /**
     * Creates new bowler in the database and adds them to the recycler view
     */
    private class NewBowlerTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... bowlerName)
        {
            long newId = -1;
            SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            ContentValues values = new ContentValues();
            values.put(BowlerEntry.COLUMN_BOWLER_NAME, bowlerName[0]);
            values.put(BowlerEntry.COLUMN_DATE_MODIFIED, currentDate);

            database.beginTransaction();
            try
            {
                newId = database.insert(BowlerEntry.TABLE_NAME, null, values);

                /*
                 * Creates an entry in the 'league' table for a default league
                 * for the new bowler being added
                 */
                values = new ContentValues();
                values.put(LeagueEntry.COLUMN_LEAGUE_NAME, Constants.NAME_OPEN_LEAGUE);
                values.put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate);
                values.put(LeagueEntry.COLUMN_BOWLER_ID, newId);
                values.put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, 1);
                database.insert(LeagueEntry.TABLE_NAME, null, values);

                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new bowler: " + ex.getMessage());
            }
            finally
            {
                database.endTransaction();
            }

            return String.valueOf(newId) + ":" + bowlerName[0];
        }

        @Override
        protected void onPostExecute(String bowlerIdAndName)
        {
            int indexOfColon = bowlerIdAndName.indexOf(":");
            long bowlerId = Long.parseLong(bowlerIdAndName.substring(0, indexOfColon));
            String bowlerName = bowlerIdAndName.substring(indexOfColon + 1);

            /*
             * Adds the new bowler information to the corresponding lists
             * and displays them in the recycler view
             */
            if (bowlerId != -1)
            {
                mListBowlerIds.add(0, bowlerId);
                mListBowlerNames.add(0, bowlerName);
                mListBowlerAverages.add(0, (short)0);
                mAdapterBowlers.notifyItemInserted(0);
                mRecyclerViewBowlers.scrollToPosition(0);
            }
        }
    }

    /**
     * Container Activity must implement this interface to allow
     * LeagueEventFragment to be loaded when a bowler is selected
     */
    public static interface OnBowlerSelectedListener
    {
        /**
         * Should be overridden to create a LeagueEventFragment with the leagues
         * belonging to the bowler represented by bowlerId
         * @param bowlerId id of the bowler whose leagues/events will be displayed
         * @param bowlerName name of the bowler corresponding to bowlerId
         * @param openLeagueFragment if new fragment should be opened
         */
        public void onBowlerSelected(long bowlerId, String bowlerName, boolean openLeagueFragment);
    }
}
