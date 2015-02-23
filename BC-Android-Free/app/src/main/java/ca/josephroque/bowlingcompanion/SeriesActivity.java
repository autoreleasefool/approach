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
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;


public class SeriesActivity extends ActionBarActivity
{

    private static final String TAG = "SeriesActivity";

    private List<Long> mListSeriesId;
    private List<String> mListSeriesDate;
    private List<List<Short>> mListSeriesGames;

    /** View to display series dates and games */
    private RecyclerView mSeriesRecycler;
    /** Adapter to manage data displayed in mSeriesRecycler */
    private RecyclerView.Adapter mSeriesAdapter;
    /** TextView to display instructions to the user */
    private TextView mSeriesInstructionsTextView;

    private long mBowlerId = -1;
    private long mLeagueId = -1;
    private byte mNumberOfGames = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.primary_green)));

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(getResources().getColor(R.color.primary_background));

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openSeries(int position)
    {
        new OpenSeriesTask().execute(position);
    }

    public void addNewLeagueSeries()
    {
        new AddSeriesTask().execute(this, mBowlerId, mLeagueId, mNumberOfGames);
    }

    public static void addNewEventSeries(Activity srcActivity, long bowlerId, long leagueId, byte numberOfGames)
    {
        new AddSeriesTask().execute(srcActivity, bowlerId, leagueId, numberOfGames);
    }

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
                        mListSeriesDate.add(seriesDate);
                        mListSeriesGames.add(new ArrayList<Short>());
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
                mSeriesInstructionsTextView.setVisibility(View.GONE);
            }
        }
    }

    public class OpenSeriesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            long seriesIdSelected = mListSeriesId.get(position[0]);

            getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                    .edit()
                    .putLong(Constants.PREFERENCE_ID_SERIES, seriesIdSelected)
                    .apply();

            long[] gameId = new long[mNumberOfGames];
            long[] frameId = new long[mNumberOfGames * 10];
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
            String[] rawSeriesArgs = {String.valueOf(seriesIdSelected)};

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

            return new Object[]{gameId, frameId};
        }

        @Override
        protected void onPostExecute(Object[] IDs)
        {
            /*Intent gameIntent = new Intent(SeriesActivity.this, GameActivity.class);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_GAME_IDS, (long[])IDs[0]);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_FRAME_IDS, (long[])IDs[1]);
            startActivity(gameIntent);*/
        }
    }

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
            /*Activity srcActivity = (Activity)params[0];
            Intent gameIntent = new Intent(srcActivity, GameActivity.class);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_GAME_IDS, (long[])params[1]);
            gameIntent.putExtra(Constants.EXTRA_ARRAY_FRAME_IDS, (long[])params[2]);
            srcActivity.startActivity(gameIntent);*/
        }
    }
}
