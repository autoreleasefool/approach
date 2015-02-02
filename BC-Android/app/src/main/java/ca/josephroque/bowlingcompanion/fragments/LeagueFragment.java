package ca.josephroque.bowlingcompanion.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.MainActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.adapter.LeagueAverageListAdapter;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-01-28.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class LeagueFragment extends Fragment
{

    /** TAG identifier for output to log */
    private static final String TAG = "LeagueFragment";

    /** Adapter for the ListView of leagues */
    private LeagueAverageListAdapter leagueAdapter = null;

    /** ID of the selected bowler */
    private long bowlerID = -1;
    /** List of the names of the leagues belonging to the selected bowler */
    private List<String> leagueNamesList = null;
    /** List of the averages of the leagues, relative to order of leagueNamesList */
    private List<Integer> leagueAverageList = null;
    /** List of the number of games in the leagues, relative to order of leagueNamesList */
    private List<Integer> leagueNumberOfGamesList = null;
    /** List of the IDs of the leagues, relative to the order of leagueNamesList */
    private List<Long> leagueIDList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View rootView = inflater.inflate(R.layout.fragment_leagues, container, false);
        final ListView leagueListView = (ListView)rootView.findViewById(R.id.list_league_names);

        //Loads data from the above query into lists
        leagueNamesList = new ArrayList<String>();
        leagueAverageList = new ArrayList<Integer>();
        leagueIDList = new ArrayList<Long>();
        leagueNumberOfGamesList = new ArrayList<Integer>();

        leagueAdapter = new LeagueAverageListAdapter(getActivity(), leagueIDList, leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueListView.setAdapter(leagueAdapter);
        leagueListView.setLongClickable(true);
        leagueListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    long leagueIDSelected = (Long) leagueListView.getItemAtPosition(position);

                    if (!leagueNamesList.get(leagueIDList.indexOf(leagueIDSelected)).equals(Constants.OPEN_LEAGUE))
                    {
                        //Updates the date modified in the database of the selected league
                        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        ContentValues values = new ContentValues();
                        values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));

                        database.beginTransaction();
                        try
                        {
                            database.update(LeagueEntry.TABLE_NAME,
                                    values,
                                    LeagueEntry._ID + "=?",
                                    new String[]{String.valueOf(leagueIDSelected)});
                            database.setTransactionSuccessful();
                        } catch (Exception ex)
                        {
                            Log.w(TAG, "Error updating league: " + ex.getMessage());
                        } finally
                        {
                            database.endTransaction();
                        }
                    }

                    SharedPreferences.Editor preferencesEditor = getActivity().getSharedPreferences(Constants.MY_PREFS, Activity.MODE_PRIVATE).edit();
                    preferencesEditor
                            .putString(Constants.PREFERENCES_NAME_LEAGUE, leagueNamesList.get(leagueIDList.indexOf(leagueIDSelected)))
                            .putLong(Constants.PREFERENCES_ID_LEAGUE, leagueIDSelected)
                            .putInt(Constants.PREFERENCES_NUMBER_OF_GAMES, leagueNumberOfGamesList.get(leagueIDList.indexOf(leagueIDSelected)))
                            .putBoolean(Constants.PREFERENCES_TOURNAMENT_MODE, false);
                    if (!leagueNamesList.get(leagueIDList.indexOf(leagueIDSelected)).equals(Constants.OPEN_LEAGUE))
                    {
                        preferencesEditor.putLong(Constants.PREFERENCES_ID_LEAGUE_RECENT, leagueIDSelected)
                                .putLong(Constants.PREFERENCES_ID_BOWLER_RECENT, bowlerID);
                    }
                    preferencesEditor.apply();

                    Intent seriesIntent = new Intent(getActivity(), SeriesActivity.class);
                    startActivity(seriesIntent);
                }
            });
        leagueListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    showDeleteLeagueDialog(position, false);
                    return true;
                }
            });

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();
        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.MY_PREFS, Activity.MODE_PRIVATE);
        bowlerID = preferences.getLong(BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID, -1);

        leagueNamesList.clear();
        leagueAverageList.clear();
        leagueIDList.clear();
        leagueNumberOfGamesList.clear();
        List<Integer> leagueTotalNumberOfGamesList = new ArrayList<Integer>();

        String rawLeagueQuery = "SELECT "
                + LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID + " AS lid, "
                + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE
                + " FROM " + LeagueEntry.TABLE_NAME
                + " LEFT JOIN " + GameEntry.TABLE_NAME
                + " ON " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=" + GameEntry.COLUMN_NAME_BOWLER_ID
                + " WHERE " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=? AND " + LeagueEntry.COLUMN_NAME_IS_TOURNAMENT + "=?"
                + " ORDER BY " + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " DESC";
        String[] rawLeagueArgs ={String.valueOf(bowlerID), String.valueOf(0)};

        Cursor cursor = database.rawQuery(rawLeagueQuery, rawLeagueArgs);

        if (cursor.moveToFirst())
        {
            int leagueTotalPinfall = 0;
            int totalNumberOfLeagueGames = 0;
            while(!cursor.isAfterLast())
            {
                String leagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                long leagueID = cursor.getLong(cursor.getColumnIndex("lid"));
                int numberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                int finalScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                if (leagueIDList.size() == 0)
                {
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                    leagueNumberOfGamesList.add(numberOfGames);
                }
                else if (!leagueIDList.contains(leagueID))
                {
                    if (leagueIDList.size() > 0)
                    {
                        leagueTotalNumberOfGamesList.add(totalNumberOfLeagueGames);
                        leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
                    }

                    leagueTotalPinfall = 0;
                    totalNumberOfLeagueGames = 0;
                    leagueNamesList.add(leagueName);
                    leagueIDList.add(leagueID);
                    leagueNumberOfGamesList.add(numberOfGames);
                }

                totalNumberOfLeagueGames++;
                leagueTotalPinfall += finalScore;

                cursor.moveToNext();
            }

            if (leagueIDList.size() > 0)
            {
                leagueTotalNumberOfGamesList.add(totalNumberOfLeagueGames);
                leagueAverageList.add((totalNumberOfLeagueGames > 0) ? leagueTotalPinfall / totalNumberOfLeagueGames:0);
            }
        }

        leagueAdapter.update(leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueAdapter.notifyDataSetChanged();
    }

    public void addNewLeague(String leagueName, int numberOfGames)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (numberOfGames < 1 || numberOfGames > Constants.MAX_NUMBER_OF_GAMES)
        {
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and " + Constants.MAX_NUMBER_OF_GAMES + " (inclusive).";
        }
        else if (leagueNamesList.contains(leagueName))
        {
            validInput = false;
            invalidInputMessage = "That name has already been used. You must choose another.";
        }

        //Displays an alert if input is invalid and does not create the new league
        if (!validInput)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(invalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        long newID = -1;
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, leagueName);
        values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));
        values.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
        values.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames);

        database.beginTransaction();
        try
        {
            newID = database.insert(LeagueEntry.TABLE_NAME, null, values);
            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Error adding new league: " + ex.getMessage());
        }
        finally
        {
            database.endTransaction();
        }

        //Adds the league to the top of the list (it is the most recent)
        leagueNamesList.add(0, leagueName);
        leagueAverageList.add(0, 0);
        leagueIDList.add(0, newID);
        leagueNumberOfGamesList.add(0, numberOfGames);
        leagueAdapter.update(leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueAdapter.notifyDataSetChanged();
    }

    /**
     * Displays a dialog to the user to delete data of a league
     *
     * @param position selected league from list view
     * @param secondChance if false, will show a second dialog to confirm option. If
     *                     true, selecting 'delete' will delete all data of league
     */
    private void showDeleteLeagueDialog(final int position, boolean secondChance)
    {
        final long leagueID = leagueIDList.get(position);
        final String leagueName = leagueNamesList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (secondChance)
        {
            builder.setMessage("WARNING: This action cannot be undone! Still delete all data for " + leagueName + "?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            deleteLeague(leagueID);
                        }
                    });
        }
        else
        {
            builder.setMessage("Delete all data for " + leagueName + "?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            showDeleteLeagueDialog(position, true);
                        }
                    });
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //do nothing
            }
        })
                .create()
                .show();
    }

    /**
     * Deletes all data in database corresponding to a single league ID
     *
     * @param selectedLeagueID league ID to delete data of
     */
    private boolean deleteLeague(long selectedLeagueID)
    {
        int index = leagueIDList.indexOf(selectedLeagueID);
        String leagueName = leagueNamesList.remove(index);
        leagueAverageList.remove(index);
        leagueNumberOfGamesList.remove(index);
        leagueIDList.remove(index);
        leagueAdapter.update(leagueNamesList, leagueAverageList, leagueNumberOfGamesList);
        leagueAdapter.notifyDataSetChanged();

        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        String[] whereArgs = {String.valueOf(selectedLeagueID)};
        database.beginTransaction();
        try
        {
            database.delete(FrameEntry.TABLE_NAME,
                    FrameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                    whereArgs);
            database.delete(GameEntry.TABLE_NAME,
                    GameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                    whereArgs);
            database.delete(SeriesEntry.TABLE_NAME,
                    SeriesEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                    whereArgs);
            database.delete(LeagueEntry.TABLE_NAME,
                    LeagueEntry._ID + "=?",
                    whereArgs);
        }
        catch (Exception e)
        {
            Log.w(TAG, "Error deleting league: " + leagueName);
            return false;
        }
        finally
        {
            database.endTransaction();
        }

        return true;
    }
}
