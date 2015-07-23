package ca.josephroque.bowlingcompanion;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ca.josephroque.bowlingcompanion.adapter.NavigationDrawerAdapter;
import ca.josephroque.bowlingcompanion.data.Bowler;
import ca.josephroque.bowlingcompanion.data.LeagueEvent;
import ca.josephroque.bowlingcompanion.data.Series;
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.fragment.BowlerFragment;
import ca.josephroque.bowlingcompanion.fragment.GameFragment;
import ca.josephroque.bowlingcompanion.fragment.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.fragment.SeriesFragment;
import ca.josephroque.bowlingcompanion.fragment.StatsFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.AppRater;
import ca.josephroque.bowlingcompanion.utilities.DataFormatter;
import ca.josephroque.bowlingcompanion.utilities.EmailUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;
import ca.josephroque.bowlingcompanion.utilities.NavigationUtils;

/**
 * Created by Joseph Roque <p/> The main activity which handles most interaction with the
 * application.
 */
@SuppressWarnings("Convert2Lambda")
public class MainActivity
        extends AppCompatActivity
        implements
        FragmentManager.OnBackStackChangedListener,
        Theme.ChangeableTheme,
        BowlerFragment.OnBowlerSelectedListener,
        LeagueEventFragment.OnLeagueSelectedListener,
        SeriesFragment.SeriesListener,
        GameFragment.GameFragmentCallbacks,
        NavigationDrawerAdapter.NavigationCallback
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    /** Relative to center of views. */
    private static final float CENTER_PIVOT = 0.5f;

    /** Id of current bowler being used in fragments. */
    private long mBowlerId = -1;
    /** Id of current league being used in fragments. */
    private long mLeagueId = -1;
    /** Id of current series being used in fragments. */
    private long mSeriesId = -1;
    /** Id of current game being used in fragments. */
    private long mGameId = -1;
    /** Number of games in current league/event in fragments. */
    private byte mNumberOfGames = -1;
    /** Name of current bowler being used in fragments. */
    private String mBowlerName;
    /** Name of current league being used in fragments. */
    private String mLeagueName;
    /** Date of current series being used in fragments. */
    private String mSeriesDate;
    /** Game number in series. */
    private byte mGameNumber;
    /** Indicates if the fragments are in event mode or not. */
    private boolean mIsEventMode;
    /** Indicates if a quick series is being created. */
    private boolean mIsQuickSeries;
    /** Indicates the current fragment on screen. */
    private String mCurrentFragmentTitle = Constants.FRAGMENT_BOWLERS;

    /** View which, on click, advances the frame. */
    private View mViewAutoAdvance;
    /** Displays time until auto advance. */
    private TextView mTextViewAutoAdvanceStatus;
    /** Indicates if auto advance has been enabled. */
    private boolean mAutoAdvanceEnabled;
    /** Time to delay auto advance. */
    private int mAutoAdvanceDelay;
    /** Time remaining before auto advance delay expires. */
    private int mAutoAdvanceDelayRemaining;

    /** Handler for posting auto advance. */
    private Handler mAutoAdvanceHandler;

    /** Runnable to auto advance. */
    private Runnable mAutoAdvanceCallback = new Runnable()
    {
        @Override
        public void run()
        {
            if (--mAutoAdvanceDelayRemaining <= 0)
            {
                mViewAutoAdvance.performClick();
                setAutoAdvanceConditions(
                        mViewAutoAdvance, mTextViewAutoAdvanceStatus, false, mAutoAdvanceDelay);
            }
            else
            {
                if (mTextViewAutoAdvanceStatus != null)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            final int timeToDelay = 1000;
                            mTextViewAutoAdvanceStatus.setVisibility(View.VISIBLE);
                            mTextViewAutoAdvanceStatus.setText(mAutoAdvanceDelayRemaining
                                    + " seconds until auto advance");
                            mAutoAdvanceHandler.postDelayed(mAutoAdvanceCallback, timeToDelay);
                        }
                    });
                }
            }
        }
    };

    /** Queue of threads which are waiting to save data to the database. */
    private ConcurrentLinkedQueue<Thread> mQueueSavingThreads;
    /** Current thread saving to the database. */
    private Thread mRunningSaveThread;
    /** Indicates if the app is running and should continue to check for threads trying to save. */
    private AtomicBoolean mAppIsRunning = new AtomicBoolean(false);

    /** Navigation drawer layout. */
    private DrawerLayout mDrawerLayout;
    /** ListView to display items in navigation drawer. */
    private RecyclerView mDrawerRecyclerView;
    /** Adapter to manage items in navigation drawer. */
    private NavigationDrawerAdapter mDrawerAdapter;
    /** Toggle for the navigation drawer. */
    private ActionBarDrawerToggle mDrawerToggle;

    /** Title of the navigation drawer. */
    private int mDrawerTitle;
    /** Title of the activity for when navigation drawer is closed. */
    private int mTitle;
    /** Items in the navigation drawer. */
    private ArrayList<String> mListDrawerOptions;

    /** AdView to display ads to user. */
    private AdView mAdView;

    /** The primary floating action button. */
    private FloatingActionButton mFloatingActionButton;
    /** Current icon of the floating action button. */
    private int mCurrentFabIcon = 0;
    /** A reference to the current fragment. */
    private WeakReference<Fragment> mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
        Theme.loadTheme(this);
        mAutoAdvanceHandler = new AutoAdvanceHandler(Looper.getMainLooper());

        mTitle = R.string.app_name;
        mDrawerTitle = R.string.title_drawer;
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        setupNavigationDrawer();
        setupFloatingActionButton();

        mQueueSavingThreads = new ConcurrentLinkedQueue<>();
        mRunningSaveThread = null;

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
        {
            //Creates new BowlerFragment to display data, if no other fragment exists
            Fragment bowlerFragment = BowlerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_main_fragment_container, bowlerFragment,
                            Constants.FRAGMENT_BOWLERS)
                    .commit();
        }
        else
        {
            //Loads member variables from bundle
            mBowlerId = savedInstanceState.getLong(Constants.EXTRA_ID_BOWLER, -1);
            mLeagueId = savedInstanceState.getLong(Constants.EXTRA_ID_LEAGUE, -1);
            mSeriesId = savedInstanceState.getLong(Constants.EXTRA_ID_SERIES, -1);
            mGameId = savedInstanceState.getLong(Constants.EXTRA_ID_GAME, -1);
            mGameNumber = savedInstanceState.getByte(Constants.EXTRA_GAME_NUMBER, (byte) -1);
            mBowlerName = savedInstanceState.getString(Constants.EXTRA_NAME_BOWLER);
            mLeagueName = savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE);
            mSeriesDate = savedInstanceState.getString(Constants.EXTRA_NAME_SERIES);
            mNumberOfGames = savedInstanceState.getByte(Constants.EXTRA_NUMBER_OF_GAMES, (byte) -1);
            mIsEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mIsQuickSeries = savedInstanceState.getBoolean(Constants.EXTRA_QUICK_SERIES);
            int navCurrentGameNumber =
                    savedInstanceState.getInt(Constants.EXTRA_NAV_CURRENT_GAME);
            mDrawerAdapter.setCurrentItem(navCurrentGameNumber);
            mDrawerAdapter.notifyDataSetChanged();
        }

        setupAdView();

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
        outState.putInt(Constants.EXTRA_NAV_CURRENT_GAME, mDrawerAdapter.getCurrentItem());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAppIsRunning.set(true);

        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.resume();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (mAppIsRunning.get() || mQueueSavingThreads.peek() != null)
                {
                    mRunningSaveThread = mQueueSavingThreads.peek();
                    if (mRunningSaveThread != null)
                    {
                        mRunningSaveThread.start();
                        try
                        {
                            mRunningSaveThread.join();
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
                            //noinspection CheckStyle
                            Thread.sleep(100);
                        }
                        catch (InterruptedException ex)
                        {
                            throw new RuntimeException("Error while saving thread sleeping: "
                                    + ex.getMessage());
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
        mAppIsRunning.set(false);
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
        if (mDrawerLayout.getDrawerLockMode(GravityCompat.START)
                != DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                && mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackStackChanged()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments)
        {
            if (fragment == null || !fragment.isVisible() || fragment.getTag() == null)
                continue;

            switch (fragment.getTag())
            {
                case Constants.FRAGMENT_BOWLERS:
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
                    mCurrentFragmentTitle = Constants.FRAGMENT_BOWLERS;
                    break;
                case Constants.FRAGMENT_LEAGUES:
                    mLeagueId = -1;
                    mSeriesId = -1;
                    mGameId = -1;
                    mGameNumber = -1;
                    mLeagueName = null;
                    mSeriesDate = null;
                    mNumberOfGames = -1;
                    mCurrentFragmentTitle = Constants.FRAGMENT_LEAGUES;
                    break;
                case Constants.FRAGMENT_SERIES:
                    mSeriesId = -1;
                    mGameId = -1;
                    mGameNumber = -1;
                    mSeriesDate = null;
                    mCurrentFragmentTitle = Constants.FRAGMENT_SERIES;
                    break;
                case Constants.FRAGMENT_GAME:
                    mGameId = -1;
                    mGameNumber = -1;
                    mCurrentFragmentTitle = Constants.FRAGMENT_GAME;
                    learnNavigationDrawer();
                    break;
                case Constants.FRAGMENT_STATS:
                    mCurrentFragmentTitle = Constants.FRAGMENT_STATS;
                    break;
                default:
                    return;
            }
            mDrawerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (isDrawerOpen())
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            for (Fragment frag : fm.getFragments()) {
                if (frag != null && frag.isVisible()) {
                    FragmentManager childFm = frag.getChildFragmentManager();
                    if (childFm.getBackStackEntryCount() > 0) {
                        childFm.popBackStack();
                        return;
                    }
                }
            }
            super.onBackPressed();
        }
    }

    @Override
    public void updateTheme()
    {
        //Updates colors and sets theme for MainActivity valid
        if (getSupportActionBar() != null)
            getSupportActionBar()
                    .setBackgroundDrawable(new ColorDrawable(Theme.getPrimaryThemeColor()));
        mDrawerRecyclerView.setBackgroundColor(Theme.getPrimaryThemeColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            setTaskDescription(new ActivityManager.TaskDescription("Bowling Companion", icon,
                    Theme.getPrimaryThemeColor()));

            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(Theme.getStatusThemeColor());
        }

        if (mFloatingActionButton != null)
        {
            int[][] states = {
                    {android.R.attr.state_enabled},
                    {android.R.attr.state_pressed},
            };
            int[] colors = {
                    Theme.getPrimaryThemeColor(),
                    Theme.getTertiaryThemeColor(),
            };
            ColorStateList colorStateList = new ColorStateList(states, colors);
            mFloatingActionButton.setBackgroundTintList(colorStateList);
        }
        if (isDrawerOpen())
            setActionBarTitle(mDrawerTitle, false);
        else
            setActionBarTitle(mTitle, false);
    }

    @Override
    public void onBowlerSelected(Bowler bowler,
                                 boolean openLeagueFragment,
                                 boolean isQuickSeries)
    {
        mBowlerId = bowler.getBowlerId();
        mBowlerName = bowler.getBowlerName();
        mIsQuickSeries = isQuickSeries;

        if (openLeagueFragment)
        {
            LeagueEventFragment leagueEventFragment = LeagueEventFragment.newInstance();
            startFragmentTransaction(leagueEventFragment, Constants.FRAGMENT_BOWLERS,
                    Constants.FRAGMENT_LEAGUES);
        }
    }

    @Override
    public void onLeagueSelected(LeagueEvent leagueEvent, boolean openSeriesFragment)
    {
        mLeagueId = leagueEvent.getLeagueEventId();
        mLeagueName = leagueEvent.getLeagueEventName();
        mNumberOfGames = leagueEvent.getLeagueEventNumberOfGames();

        if (openSeriesFragment)
        {
            SeriesFragment seriesFragment = SeriesFragment.newInstance();
            startFragmentTransaction(seriesFragment, Constants.FRAGMENT_LEAGUES,
                    Constants.FRAGMENT_SERIES);
        }
    }

    @Override
    public void onSeriesSelected(Series series, boolean isEvent)
    {
        mSeriesId = series.getSeriesId();
        mSeriesDate = series.getSeriesDate();

        new OpenSeriesTask().execute(isEvent);
    }

    @Override
    public void onCreateNewSeries(boolean isEvent)
    {
        new AddSeriesTask().execute();
    }

    /**
     * Sets a reference to the current fragment in the activity.
     *
     * @param fragment current fragment
     */
    public void setCurrentFragment(Fragment fragment)
    {
        mCurrentFragment = new WeakReference<>(fragment);
    }

    /**
     * Sets up the navigation drawer for the game fragment.
     */
    public void createGameNavigationDrawer()
    {
        mListDrawerOptions.remove(NavigationUtils.NAVIGATION_ITEM_LEAGUES);
        mListDrawerOptions.remove(NavigationUtils.NAVIGATION_ITEM_SERIES);
        for (Iterator<String> it = mListDrawerOptions.iterator(); it.hasNext(); )
            if (it.next().matches("\\w+ \\d+"))
                it.remove();
        GameFragment gameFragment = (GameFragment) mCurrentFragment.get();
        int additionalOffset = 0;
        if (!isQuickSeries())
            mListDrawerOptions.add(
                    NavigationUtils.NAVIGATION_STATIC_ITEMS + additionalOffset++,
                    NavigationUtils.NAVIGATION_ITEM_LEAGUES);
        if (!isEventMode() && !isQuickSeries())
            mListDrawerOptions.add(
                    NavigationUtils.NAVIGATION_STATIC_ITEMS + additionalOffset++,
                    NavigationUtils.NAVIGATION_ITEM_SERIES);
        final int totalOffset = additionalOffset;
        for (byte i = 0; i < mNumberOfGames; i++)
            mListDrawerOptions.add(
                    NavigationUtils.NAVIGATION_STATIC_ITEMS + 1 + additionalOffset++,
                    "Game " + (i + 1));

        mDrawerAdapter.setCurrentItem(gameFragment.getCurrentGame()
                + NavigationUtils.NAVIGATION_STATIC_ITEMS + 1 + totalOffset);
        mDrawerAdapter.setHeaderTitle(mBowlerName);
        mDrawerAdapter.setHeaderSubtitle(mLeagueName);
    }

    /**
     * Sets the icon of the floating action button with animation.
     *
     * @param drawableId id of the drawable for the floating action button
     */
    public void setFloatingActionButtonIcon(final int drawableId)
    {
        if (drawableId != mCurrentFabIcon || drawableId == 0)
        {
            if (mCurrentFabIcon == 0 && drawableId != 0)
                growFloatingActionButton(drawableId);
            else
                shrinkFloatingActionButton(drawableId);
        }
    }

    /**
     * Shrinks the floating action button.
     *
     * @param drawableId new drawable to set if fab grows again
     */
    private void shrinkFloatingActionButton(final int drawableId)
    {
        final int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        ScaleAnimation shrink = new ScaleAnimation(1.0f,
                0f,
                1.0f,
                0f,
                Animation.RELATIVE_TO_SELF,
                CENTER_PIVOT,
                Animation.RELATIVE_TO_SELF,
                CENTER_PIVOT);
        shrink.setDuration((mCurrentFabIcon == 0)
                ? 1
                : shortAnimTime);
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // does nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                growFloatingActionButton(drawableId);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // does nothing
            }
        });

        mFloatingActionButton.startAnimation(shrink);
    }

    /**
     * Grows the floating action button.
     *
     * @param drawableId new drawable to set
     */
    private void growFloatingActionButton(final int drawableId)
    {
        final int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mCurrentFabIcon = drawableId;
        if (mCurrentFabIcon != 0)
            mFloatingActionButton.setVisibility(View.VISIBLE);
        else
        {
            mFloatingActionButton.setVisibility(View.GONE);
            return;
        }
        mFloatingActionButton.setImageResource(mCurrentFabIcon);
        Drawable drawable = mFloatingActionButton.getDrawable();
        if (drawable != null)
        {
            drawable.mutate();
            //noinspection CheckStyle
            drawable.setAlpha(0x8A);
        }
        ScaleAnimation grow = new ScaleAnimation(0f,
                1.0f,
                0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                CENTER_PIVOT,
                Animation.RELATIVE_TO_SELF,
                CENTER_PIVOT);
        grow.setDuration(shortAnimTime);
        grow.setInterpolator(new OvershootInterpolator());
        mFloatingActionButton.startAnimation(grow);
    }

    /**
     * Begins transaction in FragmentManager to open {@code fragment}.
     *
     * @param fragment fragment to open
     * @param backStackTag tag for current fragment in backstack
     * @param fragmentTag tag for new fragment in manager
     */
    private void startFragmentTransaction(Fragment fragment,
                                          String backStackTag,
                                          String fragmentTag)
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
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
        int offset = 0;
        while (mListDrawerOptions.size() > offset
                && !mListDrawerOptions.get(offset).matches("\\w+ \\d+"))
            offset++;
        int currentAdapterGame = mDrawerAdapter.getCurrentItem() - offset;
        if (currentAdapterGame == newGameNumber)
            return;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mDrawerAdapter.setCurrentItem(newGameNumber);
                mDrawerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNavigationItemClicked(int position)
    {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (mListDrawerOptions.get(position).matches("\\w+ \\d+"))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            GameFragment gameFragment = (GameFragment) getSupportFragmentManager()
                    .findFragmentByTag(Constants.FRAGMENT_GAME);
            if (gameFragment == null || !gameFragment.isVisible())
                return;

            int offset = 0;
            while (mListDrawerOptions.size() > offset
                    && !mListDrawerOptions.get(offset).matches("\\w+ \\d+"))
                offset++;
            gameFragment.switchGame((byte) (position - offset));
            return;
        }

        switch (mListDrawerOptions.get(position))
        {
            case NavigationUtils.NAVIGATION_ITEM_BOWLERS:
                getSupportFragmentManager().popBackStack(
                        Constants.FRAGMENT_BOWLERS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case NavigationUtils.NAVIGATION_ITEM_LEAGUES:
                getSupportFragmentManager().popBackStack(
                        Constants.FRAGMENT_LEAGUES, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case NavigationUtils.NAVIGATION_ITEM_SERIES:
                getSupportFragmentManager().popBackStack(
                        Constants.FRAGMENT_SERIES, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case NavigationUtils.NAVIGATION_ITEM_FEEDBACK:
                Intent emailIntent = EmailUtils.getEmailIntent(
                        "contact@josephroque.ca",
                        "Comm/Sug: Bowling Companion");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case NavigationUtils.NAVIGATION_ITEM_SETTINGS:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();

        if (mAutoAdvanceEnabled && mCurrentFragmentTitle.equals(Constants.FRAGMENT_GAME))
        {
            resetAutoAdvanceTimer();
        }
    }

    @Override
    public void setAutoAdvanceConditions(View clickToAdvance,
                                         TextView textViewStatus,
                                         boolean enabled,
                                         int delay)
    {
        mViewAutoAdvance = clickToAdvance;
        mTextViewAutoAdvanceStatus = textViewStatus;
        mAutoAdvanceEnabled = enabled;
        mAutoAdvanceDelay = delay;

        if (!mAutoAdvanceEnabled)
            stopAutoAdvanceTimer();
        else
            resetAutoAdvanceTimer();
    }

    @Override
    public void resetAutoAdvanceTimer()
    {
        if (!mAutoAdvanceEnabled)
            return;

        if (mTextViewAutoAdvanceStatus != null)
            mTextViewAutoAdvanceStatus.setVisibility(View.INVISIBLE);

        final int timeToDelay = 1000;
        mAutoAdvanceDelayRemaining = mAutoAdvanceDelay;
        mAutoAdvanceHandler.removeCallbacks(mAutoAdvanceCallback);
        mAutoAdvanceHandler.postDelayed(mAutoAdvanceCallback, timeToDelay);
    }

    @Override
    public void stopAutoAdvanceTimer()
    {
        if (mTextViewAutoAdvanceStatus != null)
            mTextViewAutoAdvanceStatus.setVisibility(View.INVISIBLE);
        mAutoAdvanceHandler.removeCallbacks(mAutoAdvanceCallback);
    }

    @Override
    public void updateGameScore(byte gameNumber, short gameScore)
    {
        mDrawerAdapter.setSubtitle("Game " + gameNumber, Short.toString(gameScore));
    }

    /**
     * Sets up the floating action button.
     */
    private void setupFloatingActionButton()
    {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            final int underLollipopMargin = 8;
            final float scale = getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams p =
                    (ViewGroup.MarginLayoutParams) mFloatingActionButton.getLayoutParams();
            p.setMargins(0, 0, DataFormatter.getPixelsFromDP(scale, underLollipopMargin), 0);
            mFloatingActionButton.setLayoutParams(p);
        }
        mFloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCurrentFragment != null && mCurrentFragment.get() != null
                        && mCurrentFragment.get() instanceof FloatingActionButtonHandler)
                    ((FloatingActionButtonHandler) mCurrentFragment.get()).onFabClick();
            }
        });
    }

    /**
     * Sets up the navigation drawer.
     */
    private void setupNavigationDrawer()
    {
        final int displayWidth = getResources().getDisplayMetrics().widthPixels;
        final int maxNavigationDrawerWidth = (int) Math.ceil(
                getResources().getDisplayMetrics().density
                        * NavigationUtils.MAX_NAVIGATION_DRAWER_WIDTH_DP);

        int toolbarHeight = 0;
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
            toolbarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,
                    getResources().getDisplayMetrics());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerRecyclerView = (RecyclerView) findViewById(R.id.main_drawer_list);
        ViewGroup.LayoutParams layoutParams = mDrawerRecyclerView.getLayoutParams();
        layoutParams.width = Math.min(displayWidth - toolbarHeight, maxNavigationDrawerWidth);
        mDrawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mListDrawerOptions = new ArrayList<>();
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_HEADER);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_BOWLERS);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_SUBHEADER_GAMES);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_SUBHEADER_OTHER);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_FEEDBACK);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_SETTINGS);

        mDrawerAdapter = new NavigationDrawerAdapter(this, mListDrawerOptions);
        mDrawerAdapter.setPositionToSubheader(NavigationUtils.NAVIGATION_SUBHEADER_GAMES);
        mDrawerAdapter.setPositionToSubheader(NavigationUtils.NAVIGATION_SUBHEADER_OTHER);
        mDrawerRecyclerView.setAdapter(mDrawerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.text_open_drawer, R.string.text_close_drawer)
        {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view)
            {
                super.onDrawerClosed(view);
                setActionBarTitle(mTitle, false);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                else
                    invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                setActionBarTitle(mDrawerTitle, false);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                else
                    invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Sets up the AdView and requests an ad.
     */
    private void setupAdView()
    {
        //Sets the adview to display an ad to the user
        mAdView = (AdView) findViewById(R.id.av_main);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //If ad fails to load, hides this adview
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdView.destroy();
                        mAdView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        AdRequest.Builder builder = new AdRequest.Builder()
                .addTestDevice("B3EEABB8EE11C2BE770B684D95219ECB")
                .addTestDevice("F2B8E706AC77AA09B97D016DB70BF723")
                .addTestDevice("7387C5A63BE83E951937A7F2842F6C28");
        mAdView.loadAd(builder.build());
    }

    /**
     * Gets a new instance of StatsFragment and displays it.
     *
     * @param tag represents fragment which should be returned to when backstack is popped
     */
    private void openStatsFragment(String tag)
    {
        StatsFragment statsFragment = StatsFragment.newInstance();
        startFragmentTransaction(statsFragment, tag, Constants.FRAGMENT_STATS);
    }

    /**
     * Sets title of action bar to string pointed to by resId.
     *
     * @param resId id of string to be set at title
     * @param override indicates if reference to resId title should be saved in mTitle
     */
    public void setActionBarTitle(int resId, boolean override)
    {
        //Changing title theme color
        //final String hexColor = DataFormatter.getHexColorFromInt(Theme.getHeaderFontThemeColor());

        //if (getSupportActionBar() != null)
            //getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + hexColor + "\">"
                    //+ getResources().getString(resId) + "</font>"));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(resId);
        if (override)
            mTitle = resId;
    }

    /**
     * Returns id of current bowler being used in fragments.
     *
     * @return value of mBowlerId
     */
    public long getBowlerId()
    {
        return mBowlerId;
    }

    /**
     * Returns id of current league being used in fragments.
     *
     * @return value of mLeagueId
     */
    public long getLeagueId()
    {
        return mLeagueId;
    }

    /**
     * Returns id of current series being used in fragments.
     *
     * @return value of mSeriesId
     */
    public long getSeriesId()
    {
        return mSeriesId;
    }

    /**
     * Returns current number of games being used in fragments.
     *
     * @return value of mNumberOfGames
     */
    public byte getNumberOfGames()
    {
        return mNumberOfGames;
    }

    /**
     * Returns name of current bowler being used in fragments.
     *
     * @return value of mBowlerName
     */
    public String getBowlerName()
    {
        return mBowlerName;
    }

    /**
     * Returns name of current league being used in fragments.
     *
     * @return value of mLeagueName
     */
    public String getLeagueName()
    {
        return mLeagueName;
    }

    /**
     * Returns id of current game being used in fragments.
     *
     * @return value of mGameId
     */
    public long getGameId()
    {
        return mGameId;
    }

    /**
     * Returns game number in current series.
     *
     * @return value of mGameNumber
     */
    public byte getGameNumber()
    {
        return mGameNumber;
    }

    /**
     * Returns name of current series being used in fragments.
     *
     * @return value of mSeriesId
     */
    public String getSeriesDate()
    {
        return mSeriesDate;
    }

    /**
     * Returns true if the navigation drawer is currently open, false otherwise.
     *
     * @return true if the drawer is open, false otherwise
     */
    public boolean isDrawerOpen()
    {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Returns true if the fragments are in event mode, false otherwise.
     *
     * @return the value of mIsEventMode
     */
    public boolean isEventMode()
    {
        return mIsEventMode;
    }

    /**
     * Returns true if a quick series is being created, false otherwise.
     *
     * @return the value of mIsQuickSeries
     */
    public boolean isQuickSeries()
    {
        return mIsQuickSeries;
    }

    /**
     * Loads game data related to seriesId and displays it in a new GameFragment instance.
     */
    private class OpenSeriesTask
            extends AsyncTask<Boolean, Void, Object[]>
    {

        @Override
        protected Object[] doInBackground(Boolean... isEvent)
        {
            long[] gameId = new long[mNumberOfGames];
            //noinspection CheckStyle
            long[] frameId = new long[mNumberOfGames * 10];
            boolean[] gameLocked = new boolean[mNumberOfGames];
            boolean[] manualScore = new boolean[mNumberOfGames];
            byte[] matchPlay = new byte[mNumberOfGames];

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
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
                while (!cursor.isAfterLast())
                {
                    long newGameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    if (newGameId == currentGameId)
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                    else
                    {
                        currentGameId = newGameId;
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                        gameId[++currentGame] = currentGameId;
                        gameLocked[currentGame] = cursor.getInt(
                                cursor.getColumnIndex(GameEntry.COLUMN_IS_LOCKED)) == 1;
                        manualScore[currentGame] = cursor.getInt(
                                cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1;
                        matchPlay[currentGame] = (byte) cursor.getInt(
                                cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY));
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return new Object[]{gameId, frameId, gameLocked, manualScore, matchPlay, isEvent[0]};
        }

        @SuppressWarnings("CheckStyle")
        @Override
        protected void onPostExecute(Object[] params)
        {
            long[] gameIds = (long[]) params[0];
            long[] frameIds = (long[]) params[1];
            boolean[] gameLocked = (boolean[]) params[2];
            boolean[] manualScore = (boolean[]) params[3];
            byte[] matchPlay = (byte[]) params[4];
            mIsEventMode = (boolean) params[5];

            GameFragment gameFragment = GameFragment.newInstance(gameIds, frameIds, gameLocked,
                    manualScore, matchPlay);
            startFragmentTransaction(gameFragment, (isEventMode()
                    ? Constants.FRAGMENT_LEAGUES
                    : Constants.FRAGMENT_SERIES), Constants.FRAGMENT_GAME);
        }
    }

    /**
     * Creates a new series in the database and displays it in a new instance of GameFragment.
     */
    private class AddSeriesTask
            extends AsyncTask<Void, Void, Object[]>
    {

        @Override
        protected Object[] doInBackground(Void... params)
        {
            long seriesId = -1;
            long[] gameId = new long[mNumberOfGames];
            //noinspection CheckStyle
            long[] frameId = new long[mNumberOfGames * 10];

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(MainActivity.this).getReadableDatabase();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
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
                    values.put(GameEntry.COLUMN_SCORE, (short) 0);
                    values.put(GameEntry.COLUMN_SERIES_ID, seriesId);
                    gameId[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                    for (byte j = 0; j < Constants.NUMBER_OF_FRAMES; j++)
                    {
                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1);
                        values.put(FrameEntry.COLUMN_GAME_ID, gameId[i]);
                        //noinspection CheckStyle
                        frameId[j + 10 * i] = database.insert(FrameEntry.TABLE_NAME, null, values);
                    }
                }

                database.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Could not create new series entry in database: "
                        + ex.getMessage());
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
            long[] gameIds = (long[]) params[0];
            long[] frameIds = (long[]) params[1];
            mIsEventMode = false;

            GameFragment gameFragment = GameFragment.newInstance(
                    gameIds,
                    frameIds,
                    new boolean[mNumberOfGames],
                    new boolean[mNumberOfGames],
                    new byte[mNumberOfGames]);
            startFragmentTransaction(
                    gameFragment,
                    (isQuickSeries()
                            ? Constants.FRAGMENT_BOWLERS
                            : Constants.FRAGMENT_SERIES),
                    Constants.FRAGMENT_GAME);
        }
    }

    /**
     * Queues a new thread to save data to database.
     *
     * @param thread saving thread
     */
    public void addSavingThread(Thread thread)
    {
        mQueueSavingThreads.add(thread);
    }

    /**
     * Waits thread until all saving threads in the queue have finished.
     *
     * @param activity source activity
     */
    public static void waitForSaveThreads(WeakReference<MainActivity> activity)
    {
        //Waits for saving to database to finish, before loading from database
        while (activity.get() != null && activity.get().mQueueSavingThreads.peek() != null)
        {
            try
            {
                //noinspection CheckStyle
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
                throw new RuntimeException("Could not wait for threads to finish saving: "
                        + ex.getMessage());
            }
            //wait for saving threads to finish
        }
    }

    /**
     * Enables or disables the navigation drawer and its menu icon.
     *
     * @param isEnabled true to enable the drawer, false to disable
     */
    public void setDrawerState(boolean isEnabled)
    {
        if (isEnabled)
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();
        }
        else
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
        }
        Log.i(TAG, "isEnabled: " + isEnabled);
    }

    /**
     * Checks if the user has opened the navigation drawer and, if not, opens it.
     */
    private void learnNavigationDrawer()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(NavigationUtils.NAVIGATION_DRAWER_LEARNED, false))
        {
            Log.i(TAG, "Opening drawer");
            mDrawerLayout.openDrawer(GravityCompat.START);
            preferences.edit().putBoolean(NavigationUtils.NAVIGATION_DRAWER_LEARNED, true).apply();
        }
        else
        {
            Log.i(TAG, "Not opening drawer");
        }
    }

    @Override
    public void loadGameScoresForDrawer(final long[] gameIds)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final short[] gameScores = new short[gameIds.length];
                SQLiteDatabase database = DatabaseHelper.getInstance(MainActivity.this)
                        .getReadableDatabase();

                String rawScoreQuery = "SELECT "
                        + GameEntry.COLUMN_SCORE
                        + " FROM " + GameEntry.TABLE_NAME
                        + " WHERE " + GameEntry._ID + " in "
                        + Arrays.toString(gameIds).replace("[", "(").replace("]", ")")
                        + " ORDER BY " + GameEntry._ID;
                Cursor cursor = database.rawQuery(rawScoreQuery, null);
                int curGame = 0;
                if (cursor.moveToFirst())
                {
                    while (!cursor.isAfterLast())
                    {
                        gameScores[curGame++] = (short) cursor.getInt(cursor.getColumnIndex(
                                GameEntry.COLUMN_SCORE));
                        cursor.moveToNext();
                    }
                }
                cursor.close();

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < gameIds.length; i++)
                        {
                            mDrawerAdapter.setSubtitle("Game " + (i + 1),
                                    Short.toString(gameScores[i]));
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * To delay auto advancing.
     */
    private static final class AutoAdvanceHandler
            extends Handler
    {

        /**
         * Sends {@code Looper} to super class.
         *
         * @param looper looper
         */
        private AutoAdvanceHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message message)
        {
            // does nothing;
        }
    }
}
