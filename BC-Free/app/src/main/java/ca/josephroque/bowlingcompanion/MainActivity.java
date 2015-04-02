package ca.josephroque.bowlingcompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.josephroque.bowlingcompanion.adapter.DrawerAdapter;
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
        GameFragment.OnGameOrSeriesStatsOpenedListener,
        DrawerAdapter.OnDrawerClickListener
{
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
    /** Indicates if the fragments are in event mode or not */
    private boolean mIsEventMode;
    /** Indicates if a quick series is being created */
    private boolean mIsQuickSeries;

    /** Queue of threads which are waiting to save data to the database */
    private ConcurrentLinkedQueue<Thread> mQueueSavingThreads;
    /** Current thread saving to the database */
    private Thread runningSaveThread;
    /** Indicates if the app is running and should continue to check for threads trying to save */
    private AtomicBoolean appIsRunning = new AtomicBoolean(false);

    /** Navigation drawer layout */
    private DrawerLayout mDrawerLayout;
    /** ListView to display items in navigation drawer */
    private ListView mDrawerList;
    /** Adapter to manage items in navigation drawer */
    private DrawerAdapter mDrawerAdapter;
    /** Toggle for the navigation drawer */
    private ActionBarDrawerToggle mDrawerToggle;

    /** Title of the navigation drawer */
    private int mDrawerTitle;
    /** Title of the activity for when navigation drawer is closed */
    private int mTitle;
    /** Items in the navigation drawer */
    private ArrayList<String> mListDrawerOptions;

    /** AdView to display ads to user */
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Theme.loadTheme(this);

        mTitle = R.string.app_name;
        mDrawerTitle = R.string.title_drawer;
        mListDrawerOptions = new ArrayList<>();
        mListDrawerOptions.add(Constants.NAV_OPTION_HOME);
        mListDrawerOptions.add(Constants.NAV_OPTION_BOWLERS);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.main_drawer_list);

        mDrawerAdapter = new DrawerAdapter(this, mListDrawerOptions);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(mDrawerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.text_open_drawer, R.string.text_close_drawer) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setActionBarTitle(mTitle, false);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                else
                    invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setActionBarTitle(mDrawerTitle, false);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                else
                    invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mQueueSavingThreads = new ConcurrentLinkedQueue<>();
        runningSaveThread = null;

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            //Creates new BowlerFragment to display data, if no other fragment exists
            BowlerFragment bowlerFragment = BowlerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_main_fragment_container, bowlerFragment, Constants.FRAGMENT_BOWLERS)
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
            mIsEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mIsQuickSeries = savedInstanceState.getBoolean(Constants.EXTRA_QUICK_SERIES);
            List<String> listNavOptions = savedInstanceState.getStringArrayList(Constants.EXTRA_NAV_OPTIONS);
            byte navCurrentGameNumber = savedInstanceState.getByte(Constants.EXTRA_NAV_CURRENT_GAME);
            mDrawerAdapter.setCurrentGame(navCurrentGameNumber);
            if (listNavOptions != null)
            {
                for (int i = 2; i < listNavOptions.size(); i++)
                {
                    mListDrawerOptions.add(listNavOptions.get(i));
                }
            }
            mDrawerAdapter.notifyDataSetChanged();
        }

        //Sets the adview to display an ad to the user
        mAdView = (AdView)findViewById(R.id.av_main);
        mAdView.setAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                //If ad fails to load, hides this adview
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdView.destroy();
                        mAdView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAdLoaded()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        mAdView.loadAd(new AdRequest.Builder().build());

        //Checks if the user should be prompted to rate the app
        AppRater.appLaunched(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        outState.putBoolean(Constants.EXTRA_QUICK_SERIES, mIsQuickSeries);
        outState.putBoolean(Constants.EXTRA_EVENT_MODE, mIsEventMode);
        outState.putStringArrayList(Constants.EXTRA_NAV_OPTIONS, mListDrawerOptions);
        outState.putByte(Constants.EXTRA_NAV_CURRENT_GAME, mDrawerAdapter.getCurrentGame());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        appIsRunning.set(true);

        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.resume();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (appIsRunning.get() || mQueueSavingThreads.peek() != null)
                {
                    runningSaveThread = mQueueSavingThreads.peek();
                    if (runningSaveThread != null)
                    {
                        runningSaveThread.start();
                        try
                        {
                            runningSaveThread.join();
                            mQueueSavingThreads.poll();
                        }
                        catch (InterruptedException ex)
                        {
                            throw new RuntimeException("Error saving game: " + ex.getMessage());
                        }
                    }
                    else
                    {
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex)
                        {
                            throw new RuntimeException("Error while saving thread sleeping: " + ex.getMessage());
                        }
                    }
                }
            }
        }).start();

        updateTheme();
    }

    @Override
    protected void onPause()
    {
        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.pause();
        super.onPause();
        appIsRunning.set(false);
    }

    @Override
    protected void onDestroy()
    {
        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.destroy();
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //Sets menu items visibility depending on if navigation drawer is open
        boolean drawerOpen = isDrawerOpen();
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch(item.getItemId())
        {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (mListDrawerOptions.size() > 2)
            mListDrawerOptions.subList(2, mListDrawerOptions.size()).clear();

        //Checks which fragment is now visible and performs some actions
        //and updates items in the navigation drawer
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment: fragments)
        {
            if (fragment == null || !fragment.isVisible() || fragment.getTag() == null)
                continue;

            if (fragment.getTag().equals(Constants.FRAGMENT_BOWLERS))
            {
                mBowlerId = -1;
                mLeagueId = -1;
                mSeriesId = -1;
                mGameId = -1;
                mGameNumber = -1;
                mBowlerName = null;
                mLeagueName = null;
                mSeriesDate = null;
                mNumberOfGames = -1;
                mIsQuickSeries = false;
            }
            if (fragment.getTag().equals(Constants.FRAGMENT_LEAGUES))
            {
                mListDrawerOptions.add(Constants.NAV_OPTION_LEAGUES_EVENTS);

                mLeagueId = -1;
                mSeriesId = -1;
                mGameId = -1;
                mGameNumber = -1;
                mLeagueName = null;
                mSeriesDate = null;
                mNumberOfGames = -1;
            }
            else if (fragment.getTag().equals(Constants.FRAGMENT_SERIES))
            {
                mListDrawerOptions.add(Constants.NAV_OPTION_LEAGUES_EVENTS);
                mListDrawerOptions.add(Constants.NAV_OPTION_SERIES);

                mSeriesId = -1;
                mGameId = -1;
                mGameNumber = -1;
                mSeriesDate = null;
            }
            else if (fragment.getTag().equals(Constants.FRAGMENT_GAME))
            {
                GameFragment gameFragment = (GameFragment)fragment;

                if (!isQuickSeries())
                    mListDrawerOptions.add(Constants.NAV_OPTION_LEAGUES_EVENTS);
                if (!isEventMode() && !isQuickSeries())
                    mListDrawerOptions.add(Constants.NAV_OPTION_SERIES);
                mListDrawerOptions.add(Constants.NAV_OPTION_GAME_DETAILS);
                for (byte i = 0; i < mNumberOfGames; i++)
                    mListDrawerOptions.add("Game " + (i + 1));

                mDrawerAdapter.setCurrentGame(gameFragment.getCurrentGame());
                mGameId = -1;
                mGameNumber = -1;
            }
            else if (fragment.getTag().equals(Constants.FRAGMENT_STATS))
            {
                if (mLeagueId >= 0 && !isQuickSeries())
                    mListDrawerOptions.add(Constants.NAV_OPTION_LEAGUES_EVENTS);
                if (mSeriesId >= 0 && !isEventMode() && !isQuickSeries())
                    mListDrawerOptions.add(Constants.NAV_OPTION_SERIES);
                if (mSeriesId >= 0)
                    mListDrawerOptions.add(Constants.NAV_OPTION_GAME_DETAILS);
                mListDrawerOptions.add(Constants.NAV_OPTION_STATS);
            }
            break;
        }
        mDrawerAdapter.notifyDataSetChanged();
        //shouldDisplayHomeUp();
    }

    @Override
    public void onBackPressed()
    {
        if (mDrawerLayout.isDrawerOpen(mDrawerList))
            mDrawerLayout.closeDrawer(mDrawerList);
        else
            super.onBackPressed();
    }

    @Override
    public void updateTheme()
    {
        //Updates colors and sets theme for MainActivity valid
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getPrimaryThemeColor()));
        mDrawerList.setBackgroundColor(Theme.getSecondaryThemeColor());
        mDrawerList.setDivider(new ColorDrawable(Theme.getTertiaryThemeColor()));
        mDrawerList.setDividerHeight(2);
    }

    @Override
    public void onBowlerSelected(long bowlerId, String bowlerName, boolean openLeagueFragment, boolean isQuickSeries)
    {
        mBowlerId = bowlerId;
        mBowlerName = bowlerName;
        mIsQuickSeries = isQuickSeries;

        if (openLeagueFragment)
        {
            LeagueEventFragment leagueEventFragment = LeagueEventFragment.newInstance();
            startFragmentTransaction(leagueEventFragment, Constants.FRAGMENT_BOWLERS, Constants.FRAGMENT_LEAGUES);
        }
    }

    @Override
    public void onLeagueSelected(long leagueId, String leagueName, byte numberOfGames, boolean openSeriesFragment)
    {
        mLeagueId = leagueId;
        mLeagueName = leagueName;
        mNumberOfGames = numberOfGames;

        if (openSeriesFragment)
        {
            SeriesFragment seriesFragment = SeriesFragment.newInstance();
            startFragmentTransaction(seriesFragment, Constants.FRAGMENT_LEAGUES, Constants.FRAGMENT_SERIES);
        }
    }

    @Override
    public void onSeriesSelected(long seriesId, String seriesDate, boolean isEvent)
    {
        mSeriesId = seriesId;
        mSeriesDate = seriesDate;

        new OpenSeriesTask().execute(isEvent);
    }

    @Override
    public void onCreateNewSeries(boolean isEvent)
    {
        new AddSeriesTask().execute();
    }

    private void startFragmentTransaction(Fragment fragment, String backStackTag, String fragmentTag)
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fl_main_fragment_container, fragment, fragmentTag)
                .addToBackStack(backStackTag)
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
        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    @Override
    public void onGameStatsOpened(long gameId, byte gameNumber)
    {
        mGameId = gameId;
        mGameNumber = gameNumber;

        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    @Override
    public void onGameChanged(final byte newGameNumber)
    {
        byte currentAdapterGame = mDrawerAdapter.getCurrentGame();
        if (currentAdapterGame == newGameNumber)
            return;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mDrawerAdapter.setCurrentGame(newGameNumber);
                mDrawerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGameItemClicked(byte gameNumber)
    {
        mDrawerLayout.closeDrawer(mDrawerList);
        GameFragment gameFragment = (GameFragment)getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_GAME);
        if (gameFragment == null || !gameFragment.isVisible())
            return;

        gameFragment.switchGame(gameNumber);
    }

    @SuppressWarnings({"IfCanBeSwitch", "StringEquality"})  //May need to compile for 1.6. Also,
                                                            //constant strings are added to list so
                                                            //they can be compared directly
    @Override
    public void onFragmentItemClicked(String fragmentItem)
    {
        mDrawerLayout.closeDrawer(mDrawerList);
        if (fragmentItem == Constants.NAV_OPTION_HOME || fragmentItem == Constants.NAV_OPTION_BOWLERS)
        {
            getSupportFragmentManager().popBackStack(Constants.FRAGMENT_BOWLERS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else if (fragmentItem == Constants.NAV_OPTION_LEAGUES_EVENTS)
        {
            getSupportFragmentManager().popBackStack(Constants.FRAGMENT_LEAGUES, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else if (fragmentItem == Constants.NAV_OPTION_SERIES)
        {
            getSupportFragmentManager().popBackStack(Constants.FRAGMENT_SERIES, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else if (fragmentItem == Constants.NAV_OPTION_GAME_DETAILS)
        {
            getSupportFragmentManager().popBackStack(Constants.FRAGMENT_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * Gets a new instance of StatsFragment and displays it
     * @param tag represents fragment which should be returned to when backstack is popped
     */
    private void openStatsFragment(String tag)
    {
        StatsFragment statsFragment = StatsFragment.newInstance();
        startFragmentTransaction(statsFragment, tag, Constants.FRAGMENT_STATS);
    }

    /**
     * Sets title of action bar to string pointed to by resId
     * @param resId id of string to be set at title
     * @param override indicates if reference to resId title should be saved in mTitle
     */
    public void setActionBarTitle(int resId, boolean override)
    {
        getSupportActionBar().setTitle(getResources().getString(resId));
        if (override)
            mTitle = resId;
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
     * Returns true if the navigation drawer is currently open, false otherwise
     * @return true if the drawer is open, false otherwise
     */
    public boolean isDrawerOpen() {return mDrawerLayout.isDrawerOpen(mDrawerList);}

    /**
     * Returns true if the fragments are in event mode, false otherwise
     * @return the value of mIsEventMode
     */
    public boolean isEventMode() {return mIsEventMode;}

    /**
     * Returns true if a quick series is being created, false otherwise
     * @return the value of mIsQuickSeries
     */
    public boolean isQuickSeries() {return mIsQuickSeries;}

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
            byte[] matchPlay = new byte[mNumberOfGames];

            SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            String rawSeriesQuery = "SELECT "
                    + "game." + GameEntry._ID + " AS gid, "
                    + GameEntry.COLUMN_IS_LOCKED + ", "
                    + GameEntry.COLUMN_IS_MANUAL + ", "
                    + GameEntry.COLUMN_MATCH_PLAY + ", "
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
                        matchPlay[currentGame] = (byte)cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return new Object[]{gameId, frameId, gameLocked, manualScore, matchPlay};
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            long[] gameIds = (long[])params[0];
            long[] frameIds = (long[])params[1];
            boolean[] gameLocked = (boolean[])params[2];
            boolean[] manualScore = (boolean[])params[3];
            byte[] matchPlay = (byte[])params[4];

            GameFragment gameFragment = GameFragment.newInstance(gameIds, frameIds, gameLocked, manualScore, matchPlay);
            startFragmentTransaction(gameFragment, (isEventMode() ? Constants.FRAGMENT_LEAGUES : Constants.FRAGMENT_SERIES), Constants.FRAGMENT_GAME);
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
                throw new RuntimeException("Could not create new series entry in database: " + ex.getMessage());
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
            mIsEventMode = false;

            GameFragment gameFragment = GameFragment.newInstance(gameIds, frameIds, new boolean[mNumberOfGames], new boolean[mNumberOfGames], new byte[mNumberOfGames]);
            startFragmentTransaction(gameFragment, (isQuickSeries() ? Constants.FRAGMENT_BOWLERS : Constants.FRAGMENT_SERIES), Constants.FRAGMENT_GAME);
        }
    }

    public void addSavingThread(Thread thread)
    {
        mQueueSavingThreads.add(thread);
    }

    public static void waitForSaveThreads(MainActivity activity)
    {
        //Waits for saving to database to finish, before loading from database
        while (activity.mQueueSavingThreads.peek() != null)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException ex)
            {
                throw new RuntimeException("Could not wait for threads to finish saving: " + ex.getMessage());
            }
            //wait for saving threads to finish
        }
    }
}
