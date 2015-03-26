package ca.josephroque.bowlingcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.fragment.BowlerFragment;
import ca.josephroque.bowlingcompanion.fragment.GameFragment;
import ca.josephroque.bowlingcompanion.fragment.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.fragment.SeriesFragment;
import ca.josephroque.bowlingcompanion.fragment.StatsFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.AppRater;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;


public class MainActivity extends ActionBarActivity
    implements
        FragmentManager.OnBackStackChangedListener,
        Theme.ChangeableTheme,
        BowlerFragment.OnBowlerSelectedListener,
        LeagueEventFragment.OnLeagueSelectedListener,
        SeriesFragment.SeriesListener,
        GameFragment.OnGameOrSeriesStatsOpenedListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "MainActivity";

    /** Id of current bowler being used in fragments */
    private long mBowlerId = -1;
    /** Id of current league being used in fragments */
    private long mLeagueId = -1;
    /** Id of current series being used in fragments */
    private long mSeriesId = -1;
    /** Id of current game being used in fragments */
    private long mGameId = -1;
    /** Number of games in current league/event in fragments */
    private byte mNumberOfGames = -1;
    /** Name of current bowler being used in fragments */
    private String mBowlerName;
    /** Name of current league being used in fragments */
    private String mLeagueName;
    /** Date of current series being used in fragments */
    private String mSeriesDate;
    /** Game number in series */
    private byte mGameNumber;

    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Theme.loadTheme(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        if (savedInstanceState == null)
        {
            //Creates new BowlerFragment to display data, if no other fragment exists
            BowlerFragment bowlerFragment = BowlerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_main_fragment_container, bowlerFragment)
                    .commit();
        }
        else
        {
            //Loads member variables from bundle
            mBowlerId = savedInstanceState.getLong(Constants.EXTRA_ID_BOWLER, -1);
            mLeagueId = savedInstanceState.getLong(Constants.EXTRA_ID_LEAGUE, -1);
            mSeriesId = savedInstanceState.getLong(Constants.EXTRA_ID_SERIES, -1);
            mGameId = savedInstanceState.getLong(Constants.EXTRA_ID_GAME, -1);
            mGameNumber = savedInstanceState.getByte(Constants.EXTRA_GAME_NUMBER, (byte)-1);
            mBowlerName = savedInstanceState.getString(Constants.EXTRA_NAME_BOWLER);
            mLeagueName = savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE);
            mSeriesDate = savedInstanceState.getString(Constants.EXTRA_NAME_SERIES);
            mNumberOfGames = savedInstanceState.getByte(Constants.EXTRA_NUMBER_OF_GAMES, (byte)-1);
        }

        /*mAdView = (AdView)findViewById(R.id.av_main);
        try
        {
            mAdView.loadAd(new AdRequest.Builder().build());
        }
        catch (Exception e)
        {
            mAdView = null;
        }*/

        AppRater.appLaunched(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Saves member variables to bundle
        outState.putLong(Constants.EXTRA_ID_BOWLER, mBowlerId);
        outState.putLong(Constants.EXTRA_ID_LEAGUE, mLeagueId);
        outState.putLong(Constants.EXTRA_ID_SERIES, mSeriesId);
        outState.putLong(Constants.EXTRA_ID_GAME, mGameId);
        outState.putString(Constants.EXTRA_NAME_BOWLER, mBowlerName);
        outState.putString(Constants.EXTRA_NAME_LEAGUE, mLeagueName);
        outState.putString(Constants.EXTRA_NAME_SERIES, mSeriesDate);
        outState.putByte(Constants.EXTRA_NUMBER_OF_GAMES, mNumberOfGames);
        outState.putByte(Constants.EXTRA_GAME_NUMBER, mGameNumber);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //if (mAdView != null)
        //    mAdView.resume();

        updateTheme();
    }

    @Override
    protected void onPause()
    {
        //if (mAdView != null)
        //    mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        //if (mAdView != null)
        //    mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                //Returns to fragment on back stack, if there is one
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0)
                {
                    String backStackEntryName =
                            fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
                    fm.popBackStack();

                    switch(backStackEntryName)
                    {
                        case Constants.FRAGMENT_BOWLERS:
                            mBowlerId = -1;
                            mBowlerName = null;
                        case Constants.FRAGMENT_LEAGUES:
                            mLeagueId = -1;
                            mLeagueName = null;
                            mNumberOfGames = -1;
                        case Constants.FRAGMENT_SERIES:
                            mSeriesDate = null;
                            mSeriesId = -1;
                        case Constants.FRAGMENT_GAME:
                            mGameId = -1;
                            mGameNumber = -1;
                            break;
                        default:
                            Log.w(TAG, "Invalid back stack name: " + backStackEntryName);
                    }
                }
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    @Override
    public void updateTheme()
    {
        //Updates colors and sets theme for MainActivity valid
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getPrimaryThemeColor()));
    }

    @Override
    public void onBowlerSelected(long bowlerId, String bowlerName, boolean openLeagueFragment)
    {
        mBowlerId = bowlerId;
        mBowlerName = bowlerName;

        mLeagueId = -1;
        mSeriesId = -1;
        mGameId = -1;
        mNumberOfGames = -1;
        mLeagueName = null;
        mSeriesDate = null;
        mGameNumber = -1;

        if (openLeagueFragment)
        {
            LeagueEventFragment leagueEventFragment = LeagueEventFragment.newInstance();
            startFragmentTransaction(leagueEventFragment, Constants.FRAGMENT_BOWLERS);
        }
    }

    @Override
    public void onLeagueSelected(long leagueId, String leagueName, byte numberOfGames, boolean openSeriesFragment)
    {
        mLeagueId = leagueId;
        mLeagueName = leagueName;
        mNumberOfGames = numberOfGames;

        mSeriesId = -1;
        mGameId = -1;
        mSeriesDate = null;
        mGameNumber = -1;

        if (openSeriesFragment)
        {
            SeriesFragment seriesFragment = SeriesFragment.newInstance();
            startFragmentTransaction(seriesFragment, Constants.FRAGMENT_LEAGUES);
        }
    }

    @Override
    public void onSeriesSelected(long seriesId, String seriesDate, boolean isEvent)
    {
        mSeriesId = seriesId;
        mSeriesDate = seriesDate;

        mGameId = -1;
        mGameNumber = -1;

        new OpenSeriesTask().execute(isEvent);
    }

    @Override
    public void onCreateNewSeries(boolean isEvent)
    {
        new AddSeriesTask().execute();
    }

    private void startFragmentTransaction(Fragment fragment, String tag)
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fl_main_fragment_container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onBowlerStatsOpened()
    {
        openStatsFragment(Constants.FRAGMENT_LEAGUES);
    }

    @Override
    public void onLeagueStatsOpened()
    {
        openStatsFragment(Constants.FRAGMENT_SERIES);
    }

    @Override
    public void onSeriesStatsOpened()
    {
        mGameId = -1;
        mGameNumber = -1;

        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    @Override
    public void onGameStatsOpened(long gameId, byte gameNumber)
    {
        mGameId = gameId;
        mGameNumber = gameNumber;

        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    /**
     * Gets a new instance of StatsFragment and displays it
     * @param tag represents fragment which should be returned to when backstack is popped
     */
    private void openStatsFragment(String tag)
    {
        StatsFragment statsFragment = StatsFragment.newInstance();
        startFragmentTransaction(statsFragment, tag);
    }

    /**
     * Sets DisplayHomeAsUpEnabled if any fragments are on back stack
     */
    public void shouldDisplayHomeUp()
    {
        boolean canBack = (getSupportFragmentManager().getBackStackEntryCount() > 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
    }

    /**
     * Sets title of action bar to string pointed to by resId
     * @param resId id of string to be set at title
     */
    public void setActionBarTitle(int resId)
    {
        getSupportActionBar().setTitle(getResources().getString(resId));
    }

    /**
     * Returns id of current bowler being used in fragments
     * @return value of mBowlerId
     */
    public long getBowlerId(){return mBowlerId;}

    /**
     * Returns id of current league being used in fragments
     * @return value of mLeagueId
     */
    public long getLeagueId(){return mLeagueId;}

    /**
     * Returns id of current series being used in fragments
     * @return value of mSeriesId
     */
    public long getSeriesId(){return mSeriesId;}

    /**
     * Returns current number of games being used in fragments
     * @return value of mNumberOfGames
     */
    public byte getNumberOfGames(){return mNumberOfGames;}

    /**
     * Returns name of current bowler being used in fragments
     * @return value of mBowlerName
     */
    public String getBowlerName(){return mBowlerName;}

    /**
     * Returns name of current league being used in fragments
     * @return value of mLeagueName
     */
    public String getLeagueName(){return mLeagueName;}

    /**
     * Returns id of current game being used in fragments
     * @return value of mGameId
     */
    public long getGameId(){return mGameId;}

    /**
     * Returns game number in current series
     * @return value of mGameNumber
     */
    public byte getGameNumber(){return mGameNumber;}

    /**
     * Returns name of current series being used in fragments
     * @return value of mSeriesId
     */
    public String getSeriesDate(){return mSeriesDate;}

    /**
     * Loads game data related to seriesId and displays it in a
     * new GameFragment instance
     */
    private class OpenSeriesTask extends AsyncTask<Boolean, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Boolean... isEvent)
        {
            long[] gameId = new long[mNumberOfGames];
            long[] frameId = new long[mNumberOfGames * 10];
            boolean[] gameLocked = new boolean[mNumberOfGames];
            boolean[] manualScore = new boolean[mNumberOfGames];

            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            String rawSeriesQuery = "SELECT "
                    + "game." + GameEntry._ID + " AS gid, "
                    + GameEntry.COLUMN_IS_LOCKED + ", "
                    + GameEntry.COLUMN_IS_MANUAL + ", "
                    + "frame." + FrameEntry._ID + " AS fid"
                    + " FROM " + GameEntry.TABLE_NAME + " AS game"
                    + " INNER JOIN " + FrameEntry.TABLE_NAME + " AS frame"
                    + " ON gid=" + FrameEntry.COLUMN_GAME_ID
                    + " WHERE " + GameEntry.COLUMN_SERIES_ID + "=?"
                    + " ORDER BY gid, fid";
            String[] rawSeriesArgs = {String.valueOf(mSeriesId)};

            int currentGame = -1;
            long currentGameId = -1;
            int currentFrame = -1;
            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
            if (cursor.moveToFirst())
            {
                while(!cursor.isAfterLast())
                {
                    long newGameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    if (newGameId == currentGameId)
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                    else
                    {
                        currentGameId = newGameId;
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                        gameId[++currentGame] = currentGameId;
                        gameLocked[currentGame] =
                                (cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_LOCKED)) == 1);
                        manualScore[currentGame] =
                                (cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return new Object[]{gameId, frameId, gameLocked, manualScore, isEvent[0]};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            long[] gameIds = (long[])params[0];
            long[] frameIds = (long[])params[1];
            boolean[] gameLocked = (boolean[])params[2];
            boolean[] manualScore = (boolean[])params[3];
            boolean isEvent = (Boolean)params[4];

            GameFragment gameFragment = GameFragment.newInstance(isEvent, gameIds, frameIds, gameLocked, manualScore);
            startFragmentTransaction(gameFragment, (isEvent ? Constants.FRAGMENT_LEAGUES : Constants.FRAGMENT_SERIES));
        }
    }

    /**
     * Creates a new series in the database and displays it in
     * a new instance of GameFragment
     */
    private class AddSeriesTask extends AsyncTask<Void, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Void... params)
        {
            long seriesId = -1;
            long[] gameId = new long[mNumberOfGames];
            long[] frameId = new long[mNumberOfGames * 10];

            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String seriesDate = dateFormat.format(new Date());

            database.beginTransaction();
            try
            {
                ContentValues values = new ContentValues();
                values.put(SeriesEntry.COLUMN_SERIES_DATE, seriesDate);
                values.put(SeriesEntry.COLUMN_LEAGUE_ID, mLeagueId);
                seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values);

                for (byte i = 0; i < mNumberOfGames; i++)
                {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_GAME_NUMBER, i + 1);
                    values.put(GameEntry.COLUMN_SCORE, (short)0);
                    values.put(GameEntry.COLUMN_SERIES_ID, seriesId);
                    gameId[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                    for (byte j = 0; j < Constants.NUMBER_OF_FRAMES; j++)
                    {
                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1);
                        values.put(FrameEntry.COLUMN_GAME_ID, gameId[i]);
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

            mSeriesId = seriesId;
            mSeriesDate = DataFormatter.formattedDateToPrettyCompact(seriesDate);
            return new Object[]{gameId, frameId};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            long[] gameIds = (long[])params[0];
            long[] frameIds = (long[])params[1];

            GameFragment gameFragment = GameFragment.newInstance(false, gameIds, frameIds, new boolean[mNumberOfGames], new boolean[mNumberOfGames]);
            startFragmentTransaction(gameFragment, Constants.FRAGMENT_SERIES);
        }
    }
}
