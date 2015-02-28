package ca.josephroque.bowlingcompanion;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.SeriesAdapter;
import ca.josephroque.bowlingcompanion.data.ConvertValue;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class SeriesActivity extends ActionBarActivity
    implements ChangeableTheme
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "SeriesActivity";

    /** List of series ids from "series" table in database to uniquely identify series */
    private List<Long> mListSeriesId;
    /** List of series dates which will be displayed by RecyclerView */
    private List<String> mListSeriesDate;
    /** List of scores in each series which will be displayed by RecyclerView */
    private List<List<Short>> mListSeriesGames;

    /** View to display series dates and games */
    private RecyclerView mSeriesRecycler;
    /** Adapter to manage data displayed in mSeriesRecycler */
    private RecyclerView.Adapter mSeriesAdapter;
    /** TextView to display instructions to the user */
    private TextView mSeriesInstructionsTextView;

    /** Unique id of bowler selected by the user */
    private long mBowlerId = -1;
    /** Unique id of league selected by the user */
    private long mLeagueId = -1;
    /** Number of games in a series in the league selected by the user */
    private byte mNumberOfGames = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        updateTheme();

        mListSeriesId = new ArrayList<Long>();
        mListSeriesDate = new ArrayList<String>();
        mListSeriesGames = new ArrayList<>();

        mSeriesRecycler = (RecyclerView)findViewById(R.id.recyclerView_series);
        mSeriesRecycler.setHasFixedSize(true);
        mSeriesRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        RecyclerView.LayoutManager seriesLayoutManager = new LinearLayoutManager(this);
        mSeriesRecycler.setLayoutManager(seriesLayoutManager);

        mSeriesAdapter = new SeriesAdapter(this, mListSeriesId, mListSeriesDate, mListSeriesGames);
        mSeriesRecycler.setAdapter(mSeriesAdapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_series);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewLeagueSeries();
            }
        });

        mSeriesInstructionsTextView = (TextView)findViewById(R.id.textView_new_series_instructions);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mBowlerId = preferences.getLong(Constants.PREFERENCE_ID_BOWLER, -1);
        mLeagueId = preferences.getLong(Constants.PREFERENCE_ID_LEAGUE, -1);
        mNumberOfGames = getIntent().getByteExtra(Constants.EXTRA_NUMBER_OF_GAMES, (byte)-1);

        if (Theme.getSeriesActivityThemeInvalidated())
        {
            updateTheme();
        }

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.action_stats:
                showLeagueStats();
                return true;
            case R.id.action_settings:
                showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new settings activity and displays it to the user
     */
    private void showSettingsMenu()
    {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra(Constants.EXTRA_SETTINGS_SOURCE, TAG);
        startActivity(settingsIntent);
    }

    /**
     * Creates a StatsActivity to displays the stats corresponding to the current league
     */
    private void showLeagueStats()
    {
        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCE_ID_SERIES, -1)
                .putLong(Constants.PREFERENCE_ID_GAME, -1)
                .apply();

        Intent statsIntent = new Intent(SeriesActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

    /**
     * Loads the game ids from the database corresponding to a series id in mListSeriesIds
     * at index position
     *
     * @param position index of series id to load
     */
    public void openSeries(int position)
    {
        new OpenSeriesTask().execute(this, mListSeriesId.get(position), mNumberOfGames);
    }

    /**
     * Loads the game ids from the database corresponding to the provided series id
     * @param srcActivity activity which called the method
     * @param seriesId series id of which game ids should be loaded
     * @param numberOfGames number of games in the series to be loaded
     */
    public static void openEventSeries(Activity srcActivity, long seriesId, byte numberOfGames)
    {
        new OpenSeriesTask().execute(srcActivity, seriesId, numberOfGames);
    }

    /**
     * Creates a new entry in the database for a series and its games,
     * using the currently selected bowler id, league id and number of games
     */
    public void addNewLeagueSeries()
    {
        new AddSeriesTask().execute(this, mBowlerId, mLeagueId, mNumberOfGames);
    }

    /**
     * Creates a new entry in the database for a series and its games from parameters
     *
     * @param srcActivity activity which called the method
     * @param bowlerId id of bowler to add series to
     * @param leagueId id of league to add series to
     * @param numberOfGames number of games in the series to be created
     */
    public static void addNewEventSeries(Activity srcActivity, long bowlerId, long leagueId, byte numberOfGames)
    {
        new AddSeriesTask().execute(srcActivity, bowlerId, leagueId, numberOfGames);
    }

    /**
     * Loads the series ids, dates and scores from the database into the
     * member variables
     */
    private class LoadSeriesTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
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
            String[] rawQueryArgs = {String.valueOf(mLeagueId)};

            Cursor cursor = database.rawQuery(rawSeriesQuery, rawQueryArgs);
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    long seriesId = cursor.getLong(cursor.getColumnIndex("sid"));
                    String seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_NAME_DATE_CREATED)).substring(0,10);
                    short finalGameScore = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE));

                    if (!mListSeriesId.contains(seriesId))
                    {
                        mListSeriesId.add(seriesId);
                        mListSeriesGames.add(new ArrayList<Short>());
                        mListSeriesDate.add(ConvertValue.formattedDateToPrettyCompact(seriesDate));
                    }

                    mListSeriesGames.get(mListSeriesGames.size() - 1).add(finalGameScore);
                    cursor.moveToNext();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mSeriesAdapter.notifyDataSetChanged();
            if (mListSeriesId.size() > 0)
            {
                //If at least one item was loaded the instructions text is removed
                mSeriesInstructionsTextView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Creates a GameActivity to displays the leagues and events corresponding
     * to a series id selected by the user from the list displayed
     */
    private static class OpenSeriesTask extends AsyncTask<Object, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Object... params)
        {
            Activity srcActivity = (Activity)params[0];
            long seriesIdSelected = (Long)params[1];
            byte numberOfGames = (Byte)params[2];

            srcActivity.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCE_ID_SERIES, seriesIdSelected)
                    .apply();

            long[] gameId = new long[numberOfGames];
            long[] frameId = new long[numberOfGames * 10];
            SQLiteDatabase database = DatabaseHelper.getInstance(srcActivity).getReadableDatabase();

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
            String[] rawSeriesArgs = {String.valueOf(seriesIdSelected)};

            //Stores the frame ids and game ids from the database in frameId and gameId arrays
            int currentGame = -1;
            long currentGameId = -1;
            int currentFrame = -1;
            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
            if (cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    long newGameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    if (newGameId == currentGameId)
                    {
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                    }
                    else
                    {
                        currentGameId = newGameId;
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                        gameId[++currentGame] = currentGameId;
                    }
                    cursor.moveToNext();
                }
            }

            return new Object[]{srcActivity, gameId, frameId};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            //Creates the activity and passes frameIds and gameIds as extras
            Activity srcActivity = (Activity)params[0];
            long[] gameIds = (long[])params[1];
            long[] frameIds = (long[])params[2];

            Intent gameIntent = new Intent(srcActivity, GameActivity.class);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_GAME_IDS, gameIds);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_FRAME_IDS, frameIds);
            srcActivity.startActivity(gameIntent);
        }
    }

    /**
     * Creates a new series entry in the database and creates a new GameActivity
     * to display the series to the user
     */
    private static class AddSeriesTask extends AsyncTask<Object, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Object... params)
        {
            Activity srcActivity = (Activity)params[0];
            long bowlerId = (Long)params[1];
            long leagueId = (Long)params[2];
            byte numberOfGames = (Byte)params[3];

            long seriesId = -1;
            long[] gameId = new long[numberOfGames], frameId = new long[10 * numberOfGames];
            SQLiteDatabase database = DatabaseHelper.getInstance(srcActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            database.beginTransaction();

            /*
             * Inserts new games and frames into the database, storing the ids from the games
             * and frames as the rows in the database are created
             */
            try
            {
                ContentValues values = new ContentValues();
                values.put(SeriesEntry.COLUMN_NAME_DATE_CREATED, dateFormat.format(date));
                values.put(SeriesEntry.COLUMN_NAME_LEAGUE_ID, leagueId);
                values.put(SeriesEntry.COLUMN_NAME_BOWLER_ID, bowlerId);
                seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values);

                for (int i = 0; i < numberOfGames; i++)
                {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_NAME_GAME_NUMBER, i + 1);
                    values.put(GameEntry.COLUMN_NAME_GAME_FINAL_SCORE, (short)0);
                    values.put(GameEntry.COLUMN_NAME_LEAGUE_ID, leagueId);
                    values.put(GameEntry.COLUMN_NAME_BOWLER_ID, bowlerId);
                    values.put(GameEntry.COLUMN_NAME_SERIES_ID, seriesId);
                    gameId[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                    for (int j = 0; j < 10; j++)
                    {
                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_NAME_FRAME_NUMBER, j + 1);
                        values.put(FrameEntry.COLUMN_NAME_BOWLER_ID, bowlerId);
                        values.put(FrameEntry.COLUMN_NAME_LEAGUE_ID, leagueId);
                        values.put(FrameEntry.COLUMN_NAME_GAME_ID, gameId[i]);
                        frameId[j + 10 * i] = database.insert(FrameEntry.TABLE_NAME, null, values);
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

            srcActivity.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCE_ID_SERIES, seriesId)
                    .apply();

            return new Object[]{srcActivity, gameId, frameId};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            //Creates the activity and passes frameIds and gameIds as extras
            Activity srcActivity = (Activity)params[0];
            long[] gameIds = (long[])params[1];
            long[] frameIds = (long[])params[2];

            Intent gameIntent = new Intent(srcActivity, GameActivity.class);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_GAME_IDS, gameIds);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_FRAME_IDS, frameIds);
            srcActivity.startActivity(gameIntent);
        }
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_series);
        floatingActionButton.setColorNormal(Theme.getActionButtonThemeColor());
        floatingActionButton.setColorPressed(Theme.getActionButtonThemeColor());
        floatingActionButton.setColorRipple(Theme.getActionButtonRippleThemeColor());
        Theme.validateSeriesActivityTheme();
    }
}
