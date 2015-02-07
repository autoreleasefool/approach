package ca.josephroque.bowlingcompanion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;
import ca.josephroque.bowlingcompanion.adapter.SeriesListAdapter;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class SeriesActivity extends ActionBarActivity
{

    /** TAG identifier for output to log */
    private static final String TAG = "SeriesActivity";

    /** ID of the currently selected bowler */
    private long bowlerID = -1;
    /** ID of the currently selected league */
    private long leagueID = -1;
    /** Number of games per series for the league */
    private int numberOfGames = -1;

    /** List of IDs of series belonging to the current bowler and league */
    private List<Long> seriesIDList = null;
    /** List of dates which series were created, relative to order of seriesIDList */
    private List<String> seriesDateList = null;
    /** List of game scores in series, relative to order of seriesIDList */
    private List<List<Integer>> seriesGamesList = null;
    /** Adapter for the ListView of series */
    private SeriesListAdapter seriesAdapter = null;

    /** Layout which shows the tutorial first time */
    private RelativeLayout topLevelLayout = null;
    /** List of series in the league */
    private ListView seriesListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        seriesListView = (ListView)findViewById(R.id.list_series);

        seriesIDList = new ArrayList<Long>();
        seriesDateList = new ArrayList<String>();
        seriesGamesList = new ArrayList<List<Integer>>();

        seriesListView.setLongClickable(true);
        seriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    new OpenSeriesTask().execute(position);
                }
            });
        seriesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
                {
                    showDeleteSeriesDialog(position);
                    return true;
                }
            });

        topLevelLayout = (RelativeLayout)findViewById(R.id.series_top_layout);
        if (hasShownTutorial())
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        seriesIDList.clear();
        seriesDateList.clear();
        seriesGamesList.clear();

        new LoadSeriesTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_series, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (topLevelLayout.getVisibility() == View.VISIBLE)
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_league_stats:
                showLeagueStats();
                return true;
            case R.id.action_add_series:
                addNewSeries(this, bowlerID, leagueID, numberOfGames);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a StatsActivity to show the complete stats
     * of the selected bowler in the league
     */
    private void showLeagueStats()
    {
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_SERIES, -1)
                .putLong(Constants.PREFERENCES_ID_GAME, -1)
                .apply();

        Intent statsIntent = new Intent(SeriesActivity.this, StatsActivity.class);
        statsIntent.putExtra(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames);
        startActivity(statsIntent);
    }

    /**
     * Creates a new series and stores the relevant data in
     * the database, then starts a GameActivity
     */
    public static void addNewSeries(Activity srcActivity, long bowlerID, long leagueID, int numberOfGames)
    {
        new AddSeriesTask().execute(srcActivity, bowlerID, leagueID, numberOfGames);
    }

    /**
     * Shows a dialog to delete data relevant to a series
     *
     * @param position position of selected item to delete
     */
    private void showDeleteSeriesDialog(final int position)
    {
        final String seriesDate = seriesDateList.get(position);
        final long seriesID = seriesIDList.get(position);

        DatabaseHelper.deleteData(this,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteSeries(seriesID);
                    }
                },
                false,
                seriesDate);
    }

    /**
     * Deletes all data in database corresponding to a single series ID
     *
     * @param selectedSeriesID series ID to delete data of
     */
    private void deleteSeries(final long selectedSeriesID)
    {
        final int index = seriesIDList.indexOf(selectedSeriesID);
        final String seriesDate = seriesDateList.remove(index);
        seriesGamesList.remove(index);
        seriesIDList.remove(index);
        seriesAdapter.update(seriesDateList, seriesGamesList);
        seriesAdapter.notifyDataSetChanged();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String[] whereArgs = {String.valueOf(selectedSeriesID)};
                SQLiteDatabase database = DatabaseHelper.getInstance(SeriesActivity.this).getWritableDatabase();

                //Finds all IDs of games belonging to the series, adds them to a list
                List<Long> gameIDList = new ArrayList<Long>();
                Cursor cursor = database.query(GameEntry.TABLE_NAME,
                        new String[]{GameEntry._ID},
                        GameEntry.COLUMN_NAME_SERIES_ID + "=?",
                        whereArgs,
                        null,
                        null,
                        null);
                if (cursor.moveToFirst())
                {
                    while(!cursor.isAfterLast())
                    {
                        gameIDList.add(cursor.getLong(cursor.getColumnIndex(GameEntry._ID)));
                        cursor.moveToNext();
                    }
                }

                //Deletes all rows in frame table associated to game IDs found above,
                //along with rows in games and series table associated to series ID
                database.beginTransaction();
                try
                {
                    for (int i = 0; i < gameIDList.size(); i++)
                    {
                        database.delete(FrameEntry.TABLE_NAME,
                                FrameEntry.COLUMN_NAME_GAME_ID + "=?",
                                new String[]{String.valueOf(gameIDList.get(i))});
                    }
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_SERIES_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
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
     * Displays a tutorial overlay if one hasn't been shown to
     * the user yet
     *
     * @return true if the tutorial has already been shown, false otherwise
     */
    private boolean hasShownTutorial()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        boolean hasShownTutorial = preferences.getBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_SERIES, false);

        if (!hasShownTutorial)
        {
            preferences.edit()
                    .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_SERIES, true)
                    .apply();
            topLevelLayout.setVisibility(View.VISIBLE);
            topLevelLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    topLevelLayout.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
        }
        return hasShownTutorial;
    }

    private class OpenSeriesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            long seriesIDSelected = (Long)seriesListView.getItemAtPosition(position[0]);

            getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCES_ID_SERIES, seriesIDSelected)
                    .apply();

            long[] gameID = new long[numberOfGames];
            long[] frameID = new long[numberOfGames * 10];
            SQLiteDatabase database = DatabaseHelper.getInstance(SeriesActivity.this).getReadableDatabase();

            //Loads relevant game and frame IDs from database and stores them in Intent
            //for next activity
            String rawSeriesQuery = "SELECT "
                    + GameEntry.TABLE_NAME + "." + GameEntry._ID + " AS gid, "
                    + FrameEntry.TABLE_NAME + "." + FrameEntry._ID + " AS fid"
                    + " FROM " + GameEntry.TABLE_NAME
                    + " LEFT JOIN " + FrameEntry.TABLE_NAME
                    + " ON gid=" + FrameEntry.COLUMN_NAME_GAME_ID
                    + " WHERE " + GameEntry.COLUMN_NAME_SERIES_ID + "=?"
                    + " ORDER BY gid, fid";
            String[] rawSeriesArgs = {String.valueOf(seriesIDSelected)};

            int currentGame = -1;
            long currentGameID = -1;
            int currentFrame = -1;
            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
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

            return new Object[]{gameID, frameID};
        }

        @Override
        protected void onPostExecute(Object[] IDs)
        {
            Intent gameIntent = new Intent(SeriesActivity.this, GameActivity.class);
            gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, (long[])IDs[0]);
            gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, (long[])IDs[1]);
            startActivity(gameIntent);
        }
    }

    private class LoadSeriesTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {
            SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
            bowlerID = preferences.getLong(Constants.PREFERENCES_ID_BOWLER, -1);
            leagueID = preferences.getLong(Constants.PREFERENCES_ID_LEAGUE, -1);
            numberOfGames = preferences.getInt(Constants.PREFERENCES_NUMBER_OF_GAMES, -1);
            preferences.edit()
                    .putLong(Constants.PREFERENCES_ID_GAME, -1)
                    .putLong(Constants.PREFERENCES_ID_SERIES, -1)
                    .apply();

            SQLiteDatabase database = DatabaseHelper.getInstance(SeriesActivity.this).getReadableDatabase();
            String rawSeriesQuery = "SELECT "
                    + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid, "
                    + SeriesEntry.COLUMN_NAME_DATE_CREATED + ", "
                    + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ", "
                    + GameEntry.COLUMN_NAME_GAME_NUMBER
                    + " FROM " + SeriesEntry.TABLE_NAME
                    + " LEFT JOIN " + GameEntry.TABLE_NAME
                    + " ON sid=" + GameEntry.COLUMN_NAME_SERIES_ID
                    + " WHERE " + SeriesEntry.COLUMN_NAME_LEAGUE_ID + "=?"
                    + " ORDER BY " + SeriesEntry.COLUMN_NAME_DATE_CREATED + " DESC, "
                    + GameEntry.COLUMN_NAME_GAME_NUMBER;
            String[] rawQueryArgs = {String.valueOf(leagueID)};

            Cursor cursor = database.rawQuery(rawSeriesQuery, rawQueryArgs);
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    long seriesID = cursor.getLong(cursor.getColumnIndex("sid"));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_NAME_DATE_CREATED)).substring(0,10);
                    int finalGameScore = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                    if (!seriesIDList.contains(seriesID))
                    {
                        seriesIDList.add(seriesID);
                        seriesDateList.add(seriesDate);
                        seriesGamesList.add(new ArrayList<Integer>());
                    }

                    seriesGamesList.get(seriesGamesList.size() - 1).add(finalGameScore);
                    cursor.moveToNext();
                }
            }
            return null;
        }

        protected void onPostExecute(Void param)
        {
            seriesAdapter = new SeriesListAdapter(SeriesActivity.this, seriesIDList, seriesDateList, seriesGamesList);
            seriesListView.setAdapter(seriesAdapter);
        }
    }

    private static class AddSeriesTask extends AsyncTask<Object, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Object... params)
        {
            Activity srcActivity = (Activity)params[0];
            long bowlerID = (Long)params[1];
            long leagueID = (Long)params[2];
            int numberOfGames = (Integer)params[3];

            long seriesID = -1;
            long[] gameID = new long[numberOfGames], frameID = new long[10 * numberOfGames];
            SQLiteDatabase database = DatabaseHelper.getInstance(srcActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            database.beginTransaction();

            try
            {
                ContentValues values = new ContentValues();
                values.put(SeriesEntry.COLUMN_NAME_DATE_CREATED, dateFormat.format(date));
                values.put(SeriesEntry.COLUMN_NAME_LEAGUE_ID, leagueID);
                values.put(SeriesEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                seriesID = database.insert(SeriesEntry.TABLE_NAME, null, values);

                for (int i = 0; i < numberOfGames; i++)
                {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_NAME_GAME_NUMBER, i + 1);
                    values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, 0);
                    values.put(GameEntry.COLUMN_NAME_LEAGUE_ID, leagueID);
                    values.put(GameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                    values.put(GameEntry.COLUMN_NAME_SERIES_ID, seriesID);
                    gameID[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                    for (int j = 0; j < 10; j++)
                    {
                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_NAME_FRAME_NUMBER, j + 1);
                        values.put(FrameEntry.COLUMN_NAME_BOWLER_ID, bowlerID);
                        values.put(FrameEntry.COLUMN_NAME_LEAGUE_ID, leagueID);
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

            srcActivity.getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCES_ID_SERIES, seriesID)
                    .apply();

            return new Object[]{srcActivity, gameID, frameID};
        }

        @Override
        protected void onPostExecute(Object[] param)
        {
            Activity srcActivity = (Activity)param[0];
            Intent gameIntent = new Intent(srcActivity, GameActivity.class);
            gameIntent.putExtra(GameEntry.TABLE_NAME + "." + GameEntry._ID, (long[])param[1]);
            gameIntent.putExtra(FrameEntry.TABLE_NAME + "." + FrameEntry._ID, (long[])param[2]);
            srcActivity.startActivity(gameIntent);
        }
    }
}
