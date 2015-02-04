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
import ca.josephroque.bowlingcompanion.GameActivity;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.adapter.LeagueAverageListAdapter;
import ca.josephroque.bowlingcompanion.database.BowlingContract;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-01-28.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class TournamentFragment extends Fragment
{
    /** TAG identifier for output to log */
    private static final String TAG = "TournamentFragment";

    /** Adapter for the ListView of leagues */
    private LeagueAverageListAdapter tournamentAdapter = null;

    /** ID of the selected bowler */
    private long bowlerID = -1;
    /** List of the names of the leagues belonging to the selected bowler */
    private List<String> tournamentNamesList = null;
    /** List of the averages of the leagues, relative to order of leagueNamesList */
    private List<Integer> tournamentAverageList = null;
    /** List of the number of games in the leagues, relative to order of leagueNamesList */
    private List<Integer> tournamentNumberOfGamesList = null;
    /** List of the IDs of the leagues, relative to the order of leagueNamesList */
    private List<Long> tournamentIDList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View rootView = inflater.inflate(R.layout.fragment_tournaments, container, false);
        final ListView tournamentListView = (ListView)rootView.findViewById(R.id.list_tournament_names);

        //Loads data from the above query into lists
        tournamentNamesList = new ArrayList<String>();
        tournamentAverageList = new ArrayList<Integer>();
        tournamentIDList = new ArrayList<Long>();
        tournamentNumberOfGamesList = new ArrayList<Integer>();

        tournamentAdapter = new LeagueAverageListAdapter(getActivity(), tournamentIDList, tournamentNamesList, tournamentAverageList, tournamentNumberOfGamesList);
        tournamentListView.setAdapter(tournamentAdapter);
        tournamentListView.setLongClickable(true);
        tournamentListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long tournamentIDSelected = (Long)tournamentListView.getItemAtPosition(position);

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
                            new String[]{String.valueOf(tournamentIDSelected)});
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Error updating league: " + ex.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }

                getActivity().getSharedPreferences(Constants.MY_PREFS, Activity.MODE_PRIVATE)
                        .edit()
                        .putString(Constants.PREFERENCES_NAME_LEAGUE, tournamentNamesList.get(tournamentIDList.indexOf(tournamentIDSelected)))
                        .putLong(Constants.PREFERENCES_ID_LEAGUE, tournamentIDSelected)
                        .putInt(Constants.PREFERENCES_NUMBER_OF_GAMES, tournamentNumberOfGamesList.get(tournamentIDList.indexOf(tournamentIDSelected)))
                        .putBoolean(Constants.PREFERENCES_TOURNAMENT_MODE, true)
                        .apply();

                String rawSeriesQuery = "SELECT "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                        + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid"
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                        + " LEFT JOIN " + SeriesEntry.TABLE_NAME
                        + " ON league." + LeagueEntry._ID + "=" + SeriesEntry.COLUMN_NAME_LEAGUE_ID
                        + " WHERE league." + LeagueEntry._ID + "=?";
                String[] rawSeriesArgs = {String.valueOf(tournamentIDSelected)};

                Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
                cursor.moveToFirst();
                int numberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                long seriesID = cursor.getLong(cursor.getColumnIndex("sid"));

                long[] gameID = new long[numberOfGames];
                long[] frameID = new long[numberOfGames * 10];

                //Loads relevant game and frame IDs from database and stores them in Intent
                //for next activity
                rawSeriesQuery = "SELECT "
                        + GameEntry.TABLE_NAME + "." + GameEntry._ID + " AS gid, "
                        + FrameEntry.TABLE_NAME + "." + FrameEntry._ID + " AS fid"
                        + " FROM " + GameEntry.TABLE_NAME
                        + " LEFT JOIN " + FrameEntry.TABLE_NAME
                        + " ON gid=" + FrameEntry.COLUMN_NAME_GAME_ID
                        + " WHERE " + GameEntry.COLUMN_NAME_SERIES_ID + "=?"
                        + " ORDER BY gid, fid";
                rawSeriesArgs = new String[]{String.valueOf(seriesID)};

                int currentGame = -1;
                long currentGameID = -1;
                int currentFrame = -1;
                cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
                if (cursor.moveToFirst())
                {
                    while (!cursor.isAfterLast())
                    {
                        long newGameID = cursor.getLong(cursor.getColumnIndex("gid"));
                        if (newGameID == currentGameID)
                        {
                            frameID[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                        }
                        else
                        {
                            currentGameID = newGameID;
                            frameID[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                            gameID[++currentGame] = currentGameID;
                        }
                        cursor.moveToNext();
                    }
                }

                Intent gameIntent = new Intent(getActivity(), GameActivity.class);
                gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, gameID);
                gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, frameID);
                startActivity(gameIntent);
            }
        });
        tournamentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                showDeleteTournamentDialog(position);
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

        tournamentNamesList.clear();
        tournamentAverageList.clear();
        tournamentIDList.clear();
        tournamentNumberOfGamesList.clear();
        List<Integer> tournamentTotalNumberOfGamesList = new ArrayList<Integer>();

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
        String[] rawLeagueArgs ={String.valueOf(bowlerID), String.valueOf(1)};

        Cursor cursor = database.rawQuery(rawLeagueQuery, rawLeagueArgs);

        if (cursor.moveToFirst())
        {
            int tournamentTotalPinfall = 0;
            int totalNumberOfTournamentGames = 0;
            while(!cursor.isAfterLast())
            {
                String leagueName = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                long leagueID = cursor.getLong(cursor.getColumnIndex("lid"));
                int numberOfGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                int finalScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                if (tournamentIDList.size() == 0)
                {
                    tournamentNamesList.add(leagueName);
                    tournamentIDList.add(leagueID);
                    tournamentNumberOfGamesList.add(numberOfGames);
                }
                else if (!tournamentIDList.contains(leagueID))
                {
                    if (tournamentIDList.size() > 0)
                    {
                        tournamentTotalNumberOfGamesList.add(totalNumberOfTournamentGames);
                        tournamentAverageList.add((totalNumberOfTournamentGames > 0) ? tournamentTotalPinfall / totalNumberOfTournamentGames:0);
                    }

                    tournamentTotalPinfall = 0;
                    totalNumberOfTournamentGames = 0;
                    tournamentNamesList.add(leagueName);
                    tournamentIDList.add(leagueID);
                    tournamentNumberOfGamesList.add(numberOfGames);
                }

                totalNumberOfTournamentGames++;
                tournamentTotalPinfall += finalScore;

                cursor.moveToNext();
            }

            if (tournamentIDList.size() > 0)
            {
                tournamentTotalNumberOfGamesList.add(totalNumberOfTournamentGames);
                tournamentAverageList.add((totalNumberOfTournamentGames > 0) ? tournamentTotalPinfall / totalNumberOfTournamentGames:0);
            }
        }

        tournamentAdapter.update(tournamentNamesList, tournamentAverageList, tournamentNumberOfGamesList);
        tournamentAdapter.notifyDataSetChanged();
    }

    public void addNewTournament(String tournamentName, int numberOfGames)
    {
        boolean validInput = true;
        String invalidInputMessage = null;

        if (numberOfGames < 1 || numberOfGames > Constants.MAX_NUMBER_OF_TOURNAMENT_GAMES)
        {
            validInput = false;
            invalidInputMessage = "The number of games must be between 1 and " + Constants.MAX_NUMBER_OF_TOURNAMENT_GAMES + " (inclusive).";
        }
        else if (tournamentNamesList.contains(tournamentName))
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
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        long newTournamentID = -1;
        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, tournamentName);
        values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(date));
        values.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
        values.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames);
        values.put(LeagueEntry.COLUMN_NAME_IS_TOURNAMENT, 1);

        database.beginTransaction();
        try
        {
            newTournamentID = database.insert(LeagueEntry.TABLE_NAME, null, values);
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

        long seriesID = -1;
        long[] gameID = new long[numberOfGames], frameID = new long[10 * numberOfGames];
        Intent gameIntent = new Intent(getActivity(), GameActivity.class);

        database.beginTransaction();
        try
        {
            values = new ContentValues();
            values.put(SeriesEntry.COLUMN_NAME_DATE_CREATED, dateFormat.format(date));
            values.put(SeriesEntry.COLUMN_NAME_LEAGUE_ID, newTournamentID);
            values.put(SeriesEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
            seriesID = database.insert(SeriesEntry.TABLE_NAME, null, values);

            for (int i = 0; i < numberOfGames; i++)
            {
                values = new ContentValues();
                values.put(GameEntry.COLUMN_NAME_GAME_NUMBER, i + 1);
                values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, 0);
                values.put(GameEntry.COLUMN_NAME_LEAGUE_ID, newTournamentID);
                values.put(GameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                values.put(GameEntry.COLUMN_NAME_SERIES_ID, seriesID);
                gameID[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                for (int j = 0; j < 10; j++)
                {
                    values = new ContentValues();
                    values.put(FrameEntry.COLUMN_NAME_FRAME_NUMBER, j + 1);
                    values.put(FrameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                    values.put(FrameEntry.COLUMN_NAME_LEAGUE_ID, newTournamentID);
                    values.put(FrameEntry.COLUMN_NAME_GAME_ID, gameID[i]);
                    frameID[j + 10 * i] = database.insert(FrameEntry.TABLE_NAME, null, values);
                }
            }
            database.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w(TAG, "Error adding new series: " + ex.getMessage());
        }
        finally
        {
            database.endTransaction();
        }

        getActivity().getSharedPreferences(Constants.MY_PREFS, Activity.MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_LEAGUE, newTournamentID)
                .putLong(Constants.PREFERENCES_ID_SERIES, seriesID)
                .putInt(Constants.PREFERENCES_NUMBER_OF_GAMES, numberOfGames)
                .putBoolean(Constants.PREFERENCES_TOURNAMENT_MODE, true)
                .apply();
        gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, gameID);
        gameIntent.putExtra(FrameEntry.  TABLE_NAME + "." + FrameEntry._ID, frameID);
        getActivity().startActivity(gameIntent);
    }

    /**
     * Shows a dialog to delete data relevant to a tournament
     *
     * @param position position of selected item to delete
     */
    private void showDeleteTournamentDialog(final int position)
    {
        final String tournamentName = tournamentNamesList.get(position);
        final long tournamentID = tournamentIDList.get(position);

        DatabaseHelper.deleteData(getActivity(),
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteTournament(tournamentID);
                    }
                },
                false,
                tournamentName);
    }

    /**
     * Deletes all data in database corresponding to a single tournament ID
     *
     * @param selectedTournamentID tournament ID to delete data of
     */
    private boolean deleteTournament(long selectedTournamentID)
    {
        int index = tournamentIDList.indexOf(selectedTournamentID);
        String tournamentName = tournamentNamesList.remove(index);
        tournamentAverageList.remove(index);
        tournamentNumberOfGamesList.remove(index);
        tournamentIDList.remove(index);
        tournamentAdapter.update(tournamentNamesList, tournamentAverageList, tournamentNumberOfGamesList);
        tournamentAdapter.notifyDataSetChanged();

        SQLiteDatabase database = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        String[] whereArgs = {String.valueOf(selectedTournamentID)};
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
            database.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            Log.w(TAG, "Error deleting tournament: " + tournamentName);
            return false;
        }
        finally
        {
            database.endTransaction();
        }

        return true;
    }
}
