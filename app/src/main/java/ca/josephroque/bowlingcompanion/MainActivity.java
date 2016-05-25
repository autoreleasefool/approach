package ca.josephroque.bowlingcompanion;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.NumberPicker;
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
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry;
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.fragment.BowlerFragment;
import ca.josephroque.bowlingcompanion.fragment.GameFragment;
import ca.josephroque.bowlingcompanion.fragment.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.fragment.MatchPlayFragment;
import ca.josephroque.bowlingcompanion.fragment.SeriesFragment;
import ca.josephroque.bowlingcompanion.fragment.StatsGraphFragment;
import ca.josephroque.bowlingcompanion.fragment.StatsListFragment;
import ca.josephroque.bowlingcompanion.fragment.TransferFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DateUtils;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.utilities.EmailUtils;
import ca.josephroque.bowlingcompanion.utilities.FloatingActionButtonHandler;
import ca.josephroque.bowlingcompanion.utilities.NavigationController;
import ca.josephroque.bowlingcompanion.utilities.NavigationUtils;
import ca.josephroque.bowlingcompanion.utilities.PermissionUtils;
import ca.josephroque.bowlingcompanion.utilities.Startup;
import ca.josephroque.bowlingcompanion.view.AnimatedFloatingActionButton;
import ca.josephroque.bowlingcompanion.wrapper.Bowler;
import ca.josephroque.bowlingcompanion.wrapper.LeagueEvent;
import ca.josephroque.bowlingcompanion.wrapper.Series;

/**
 * Created by Joseph Roque. The main activity which handles most interaction with the application.
 */
@SuppressWarnings("Convert2Lambda")
public class MainActivity
        extends AppCompatActivity
        implements
        FragmentManager.OnBackStackChangedListener,
        Theme.ChangeableTheme,
        BowlerFragment.BowlerCallback,
        LeagueEventFragment.LeagueEventCallback,
        SeriesFragment.SeriesCallback,
        GameFragment.GameFragmentCallback,
        NavigationDrawerAdapter.NavigationCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationController {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    /** Id of current bowler being used in fragments. */
    private long mBowlerId = -1;
    /** Id of current league being used in fragments. */
    private long mLeagueId = -1;
    /** Id of current series being used in fragments. */
    private long mSeriesId = -1;
    /** Id of current game being used in fragments. */
    private long mGameId = -1;
    /** Number of games in current league/event in fragments. */
    private int mDefaultNumberOfGames;
    /** Number of games in a newly created series. */
    private int mNumberOfGamesForSeries;
    /** Name of current bowler being used in fragments. */
    private String mBowlerName;
    /** Name of current league being used in fragments. */
    private String mLeagueName;
    /** Date of current series being used in fragments. */
    private String mSeriesDate;
    /** Game number in series. */
    private int mGameNumber;
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

    /** Indicates if the user should be capable of navigating through the app through interactions with the Activity. */
    private boolean mNavigationEnabled = true;

    /** Handler for posting auto advance. */
    private Handler mAutoAdvanceHandler;

    /** Runnable to auto advance. */
    private final Runnable mAutoAdvanceCallback = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Auto advance: " + mAutoAdvanceDelayRemaining);
            if (--mAutoAdvanceDelayRemaining <= 0) {
                mViewAutoAdvance.performClick();
                stopAutoAdvanceTimer();
            } else {
                if (mTextViewAutoAdvanceStatus != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final int timeToDelay = 1000;
                            mTextViewAutoAdvanceStatus.setVisibility(View.VISIBLE);
                            mTextViewAutoAdvanceStatus.setText(String.format(
                                    getResources().getString(R.string.text_until_auto_advance_placeholder),
                                    mAutoAdvanceDelayRemaining));
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
    private final AtomicBoolean mAppIsRunning = new AtomicBoolean(false);

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
    private AnimatedFloatingActionButton mPrimaryFab;
    /** The secondary floating action button. */
    private AnimatedFloatingActionButton mSecondaryFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.loadTheme(this);

        if (getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
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

        if (savedInstanceState == null) {
            // Creates new BowlerFragment to display data, if no other fragment exists
            Fragment bowlerFragment = BowlerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_main_fragment_container, bowlerFragment,
                            Constants.FRAGMENT_BOWLERS)
                    .commit();
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                    .edit()
                    .remove(Constants.PREF_FACEBOOK_CLOSED)
                    .apply();

            mDefaultNumberOfGames = -1;
            mNumberOfGamesForSeries = -1;
        } else {
            // Loads member variables from bundle
            mBowlerId = savedInstanceState.getLong(Constants.EXTRA_ID_BOWLER, -1);
            mLeagueId = savedInstanceState.getLong(Constants.EXTRA_ID_LEAGUE, -1);
            mSeriesId = savedInstanceState.getLong(Constants.EXTRA_ID_SERIES, -1);
            mGameId = savedInstanceState.getLong(Constants.EXTRA_ID_GAME, -1);
            mGameNumber = savedInstanceState.getInt(Constants.EXTRA_GAME_NUMBER, -1);
            mBowlerName = savedInstanceState.getString(Constants.EXTRA_NAME_BOWLER);
            mLeagueName = savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE);
            mSeriesDate = savedInstanceState.getString(Constants.EXTRA_NAME_SERIES);
            mDefaultNumberOfGames = savedInstanceState.getInt(Constants.EXTRA_NUMBER_OF_GAMES, -1);
            mNumberOfGamesForSeries = savedInstanceState.getInt(Constants.EXTRA_GAMES_IN_SERIES, -1);
            mIsEventMode = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_MODE);
            mIsQuickSeries = savedInstanceState.getBoolean(Constants.EXTRA_QUICK_SERIES);
            int navCurrentGameNumber =
                    savedInstanceState.getInt(Constants.EXTRA_NAV_CURRENT_GAME);
            mDrawerAdapter.setCurrentItem(navCurrentGameNumber);
            mDrawerAdapter.notifyDataSetChanged();
        }

        setupAdView();
        Startup.onStartup(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saves member variables to bundle
        outState.putLong(Constants.EXTRA_ID_BOWLER, mBowlerId);
        outState.putLong(Constants.EXTRA_ID_LEAGUE, mLeagueId);
        outState.putLong(Constants.EXTRA_ID_SERIES, mSeriesId);
        outState.putLong(Constants.EXTRA_ID_GAME, mGameId);
        outState.putString(Constants.EXTRA_NAME_BOWLER, mBowlerName);
        outState.putString(Constants.EXTRA_NAME_LEAGUE, mLeagueName);
        outState.putString(Constants.EXTRA_NAME_SERIES, mSeriesDate);
        outState.putInt(Constants.EXTRA_NUMBER_OF_GAMES, mDefaultNumberOfGames);
        outState.putInt(Constants.EXTRA_GAMES_IN_SERIES, mNumberOfGamesForSeries);
        outState.putInt(Constants.EXTRA_GAME_NUMBER, mGameNumber);
        outState.putBoolean(Constants.EXTRA_QUICK_SERIES, mIsQuickSeries);
        outState.putBoolean(Constants.EXTRA_EVENT_MODE, mIsEventMode);
        outState.putInt(Constants.EXTRA_NAV_CURRENT_GAME, mDrawerAdapter.getCurrentItem());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppIsRunning.set(true);

        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.resume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mAppIsRunning.get() || mQueueSavingThreads.peek() != null) {
                    mRunningSaveThread = mQueueSavingThreads.peek();
                    if (mRunningSaveThread != null) {
                        mRunningSaveThread.start();
                        try {
                            mRunningSaveThread.join();
                            mQueueSavingThreads.poll();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException("Error saving game: " + ex.getMessage());
                        }
                    } else {
                        try {
                            //noinspection CheckStyle
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
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
    protected void onPause() {
        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.pause();
        super.onPause();
        mAppIsRunning.set(false);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null && mAdView.getVisibility() == View.VISIBLE)
            mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Sets menu items visibility depending on if navigation drawer is open
        boolean drawerOpen = isDrawerOpen();
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_tutorial).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout.getDrawerLockMode(GravityCompat.START)
                != DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                && mDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (!mNavigationEnabled)
            return true;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_tutorial:
                openSplashActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackStackChanged() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment == null || !fragment.isVisible() || fragment.getTag() == null)
                continue;

            switch (fragment.getTag()) {
                case Constants.FRAGMENT_BOWLERS:
                    mBowlerId = -1;
                    mLeagueId = -1;
                    mSeriesId = -1;
                    mGameId = -1;
                    mGameNumber = -1;
                    mBowlerName = null;
                    mLeagueName = null;
                    mSeriesDate = null;
                    mDefaultNumberOfGames = -1;
                    mNumberOfGamesForSeries = -1;
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
                    mDefaultNumberOfGames = -1;
                    mNumberOfGamesForSeries = -1;
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
                case Constants.FRAGMENT_STAT_LIST:
                    mCurrentFragmentTitle = Constants.FRAGMENT_STAT_LIST;
                    break;
                case Constants.FRAGMENT_STAT_GRAPH:
                    mCurrentFragmentTitle = Constants.FRAGMENT_STAT_GRAPH;
                    break;
                default:
                    return;
            }
            mDrawerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mNavigationEnabled)
            return;

        if (isDrawerOpen())
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else {
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
    public void updateTheme() {
        // Updates colors and sets theme for MainActivity valid
        if (getSupportActionBar() != null)
            getSupportActionBar()
                    .setBackgroundDrawable(new ColorDrawable(Theme.getPrimaryThemeColor()));
        mDrawerRecyclerView.setBackgroundColor(Theme.getPrimaryThemeColor());

        String taskName = getResources().getString(R.string.app_name);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            // The .debug specified in gradle
            if (pInfo.packageName.equals("ca.josephroque.bowlingcompanion.debug")) {
                taskName += " (DEBUG)";
            }
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e(TAG, "Error finding package name.", ex);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            setTaskDescription(new ActivityManager.TaskDescription(taskName, icon,
                    Theme.getPrimaryThemeColor()));

            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(Theme.getStatusThemeColor());
        } else {
            setTitle(taskName);
        }

        DisplayUtils.setFloatingActionButtonColors(mPrimaryFab,
                Theme.getPrimaryThemeColor(),
                Theme.getTertiaryThemeColor());
        DisplayUtils.setFloatingActionButtonColors(mSecondaryFab,
                Theme.getPrimaryThemeColor(),
                Theme.getTertiaryThemeColor());

        if (isDrawerOpen())
            setActionBarTitle(mDrawerTitle, false);
        else
            setActionBarTitle(mTitle, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GameFragment gameFragment = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment frag : fragmentManager.getFragments()) {
                    if (frag != null && frag instanceof GameFragment) {
                        gameFragment = (GameFragment) frag;
                        break;
                    }
                }

                if (gameFragment != null)
                    gameFragment.externalStoragePermissionGranted();
            }
        }
    }

    @Override
    public void onBowlerSelected(Bowler bowler,
                                 boolean openLeagueFragment,
                                 boolean isQuickSeries) {
        mBowlerId = bowler.getBowlerId();
        mBowlerName = bowler.getBowlerName();
        mIsQuickSeries = isQuickSeries;

        if (openLeagueFragment) {
            LeagueEventFragment leagueEventFragment = LeagueEventFragment.newInstance();
            startFragmentTransaction(leagueEventFragment, Constants.FRAGMENT_BOWLERS,
                    Constants.FRAGMENT_LEAGUES);
        }
    }

    @Override
    public void onDataTransferSelected() {
        startFragmentTransaction(TransferFragment.newInstance(),
                Constants.FRAGMENT_BOWLERS,
                Constants.FRAGMENT_TRANSFER);
    }

    @Override
    public void onLeagueSelected(LeagueEvent leagueEvent, boolean openSeriesFragment) {
        mLeagueId = leagueEvent.getLeagueEventId();
        mLeagueName = leagueEvent.getLeagueEventName();
        mDefaultNumberOfGames = leagueEvent.getLeagueEventNumberOfGames();

        if (openSeriesFragment) {
            SeriesFragment seriesFragment = SeriesFragment.newInstance();
            startFragmentTransaction(seriesFragment, Constants.FRAGMENT_LEAGUES,
                    Constants.FRAGMENT_SERIES);
        }
    }

    @Override
    public void onSeriesSelected(Series series, boolean isEvent) {
        mSeriesId = series.getSeriesId();
        mSeriesDate = series.getSeriesDate();
        if (!isEvent)
            mNumberOfGamesForSeries = series.getNumberOfGames();
        else
            mNumberOfGamesForSeries = mDefaultNumberOfGames;

        new OpenSeriesTask(MainActivity.this).execute(isEvent);
    }

    @Override
    public void onCreateNewSeries(boolean isEvent) {
        boolean promptNumberOfGames = !isEvent && mLeagueName != null && mLeagueName.equals(Constants.NAME_OPEN_LEAGUE);

        if (promptNumberOfGames) {
            final NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMaxValue(Constants.MAX_NUMBER_LEAGUE_GAMES);
            numberPicker.setMinValue(1);
            numberPicker.setWrapSelectorWheel(false);

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mNumberOfGamesForSeries = (byte) numberPicker.getValue();
                        new AddSeriesTask(MainActivity.this).execute();
                    }
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("How many games?")
                    .setView(numberPicker)
                    .setPositiveButton(R.string.dialog_create, listener)
                    .setNegativeButton(R.string.dialog_cancel, listener)
                    .create()
                    .show();
        } else {
            mNumberOfGamesForSeries = mDefaultNumberOfGames;
            new AddSeriesTask(MainActivity.this).execute();
        }
    }

    /**
     * Sets up the navigation drawer for the game fragment.
     */
    public void createGameNavigationDrawer() {
        mListDrawerOptions.remove(NavigationUtils.NAVIGATION_ITEM_LEAGUES);
        mListDrawerOptions.remove(NavigationUtils.NAVIGATION_ITEM_SERIES);
        for (Iterator<String> it = mListDrawerOptions.iterator(); it.hasNext();)
            if (it.next().matches("\\w+ \\d+"))
                it.remove();
        GameFragment gameFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag != null && frag instanceof GameFragment) {
                gameFragment = (GameFragment) frag;
                break;
            }
        }

        if (gameFragment == null)
            return;

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
        for (byte i = 0; i < mNumberOfGamesForSeries; i++)
            mListDrawerOptions.add(
                    NavigationUtils.NAVIGATION_STATIC_ITEMS + 1 + additionalOffset++,
                    "Game " + (i + 1));

        mDrawerAdapter.setCurrentItem(gameFragment.getCurrentGame()
                + NavigationUtils.NAVIGATION_STATIC_ITEMS + 1 + totalOffset);
        mDrawerAdapter.setHeaderTitle(mBowlerName);
        mDrawerAdapter.setHeaderSubtitle(mLeagueName);
    }

    /**
     * Sets the icon of the two floating action buttons with animation, using the theme colors.
     *
     * @param primaryDrawableId id of the drawable for the primary floating action button
     * @param secondaryDrawableId id of the drawable for the secondary floating action button
     */
    public void setFloatingActionButtonState(int primaryDrawableId, int secondaryDrawableId) {
        setFloatingActionButtonState(primaryDrawableId,
                Theme.getPrimaryThemeColor(),
                Theme.getTertiaryThemeColor(),
                secondaryDrawableId,
                Theme.getPrimaryThemeColor(),
                Theme.getTertiaryThemeColor());
    }

    /**
     * Sets the icon and colors of the two floating action buttons with animation.
     *
     * @param primaryDrawableId id of the drawable for the primary floating action button
     * @param primaryDefaultColor standing color of the primary floating action button
     * @param primaryPressedColor pressed color of the primary floating action button
     * @param secondaryDrawableId id of the drawable for the secondary floating action button
     * @param secondaryDefaultColor standing color of the secondary floating action button
     * @param secondaryPressedColor pressed color of the secondary floating action button
     */
    public void setFloatingActionButtonState(int primaryDrawableId,
                                             int primaryDefaultColor,
                                             int primaryPressedColor,
                                             int secondaryDrawableId,
                                             int secondaryDefaultColor,
                                             int secondaryPressedColor) {
        mPrimaryFab.animate(primaryDrawableId, primaryDefaultColor, primaryPressedColor);
        mSecondaryFab.animate(secondaryDrawableId, secondaryDefaultColor, secondaryPressedColor);
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
                                          String fragmentTag) {
        if (!mNavigationEnabled)
            return;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fl_main_fragment_container, fragment, fragmentTag)
                .addToBackStack(backStackTag)
                .commit();
    }

    @Override
    public void onBowlerStatsOpened() {
        openStatsFragment(Constants.FRAGMENT_LEAGUES);
    }

    @Override
    public void onLeagueStatsOpened() {
        openStatsFragment(Constants.FRAGMENT_SERIES);
    }

    @Override
    public void onSeriesStatsOpened() {
        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    @Override
    public void onGameStatsOpened(long gameId, byte gameNumber) {
        mGameId = gameId;
        mGameNumber = gameNumber;

        openStatsFragment(Constants.FRAGMENT_GAME);
    }

    @Override
    public void onGameChanged(final int newGameNumber) {
        int offset = 0;
        while (mListDrawerOptions.size() > offset
                && !mListDrawerOptions.get(offset).matches("\\w+ \\d+"))
            offset++;
        int currentAdapterGame = mDrawerAdapter.getCurrentItem() - offset;
        if (currentAdapterGame == newGameNumber)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDrawerAdapter.setCurrentItem(newGameNumber);
                mDrawerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNavigationItemClicked(int position) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if (!mNavigationEnabled)
            return;

        if (mListDrawerOptions.get(position).matches("\\w+ \\d+")) {
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

        switch (mListDrawerOptions.get(position)) {
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
                        "Comm/Sug: Bowling Companion",
                        null);
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case NavigationUtils.NAVIGATION_ITEM_SETTINGS:
                openSettings();
                break;
            case NavigationUtils.NAVIGATION_ITEM_HELP:
                openSplashActivity();
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        if (mAutoAdvanceEnabled && mCurrentFragmentTitle.equals(Constants.FRAGMENT_GAME)) {
            resetAutoAdvanceTimer();
        }
    }

    @Override
    public void setAutoAdvanceConditions(View clickToAdvance,
                                         TextView textViewStatus,
                                         boolean enabled,
                                         int delay) {
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
    public void resetAutoAdvanceTimer() {
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
    public void stopAutoAdvanceTimer() {
        if (mTextViewAutoAdvanceStatus != null)
            mTextViewAutoAdvanceStatus.setVisibility(View.INVISIBLE);
        mAutoAdvanceHandler.removeCallbacks(mAutoAdvanceCallback);
    }

    @Override
    public void updateGameScore(byte gameNumber, short gameScore) {
        mDrawerAdapter.setSubtitle("Game " + gameNumber, Short.toString(gameScore));
    }

    @Override
    public void setNavigationEnabled(boolean enable) {
        mNavigationEnabled = enable;
    }

    /**
     * Sets up the floating action buttons.
     */
    private void setupFloatingActionButton() {
        // Setting up the appearance and actions of the primary Fab
        mPrimaryFab = (AnimatedFloatingActionButton) findViewById(R.id.fab_main);
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), mPrimaryFab);
        mPrimaryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null && fragment.isVisible()
                            && fragment instanceof FloatingActionButtonHandler)
                        ((FloatingActionButtonHandler) fragment).onFabClick();
                }
            }
        });

        // Setting up the appearance and actions of the secondary Fab
        mSecondaryFab = (AnimatedFloatingActionButton) findViewById(R.id.fab_secondary);
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), mSecondaryFab);
        mSecondaryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null && fragment.isVisible()
                            && fragment instanceof FloatingActionButtonHandler)
                        ((FloatingActionButtonHandler) fragment).onSecondaryFabClick();
                }
            }
        });
    }

    /**
     * Sets up the navigation drawer.
     */
    @SuppressWarnings("CheckStyle")
    private void setupNavigationDrawer() {
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
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_HELP);
        mListDrawerOptions.add(NavigationUtils.NAVIGATION_ITEM_SETTINGS);

        mDrawerAdapter = new NavigationDrawerAdapter(this, mListDrawerOptions);
        mDrawerAdapter.setPositionToSubheader(NavigationUtils.NAVIGATION_SUBHEADER_GAMES);
        mDrawerAdapter.setPositionToSubheader(NavigationUtils.NAVIGATION_SUBHEADER_OTHER);
        mDrawerRecyclerView.setAdapter(mDrawerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.text_open_drawer,
                R.string.text_close_drawer) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setActionBarTitle(mTitle, false);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setActionBarTitle(mDrawerTitle, false);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                NavigationUtils.setDrawerOffset(slideOffset);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Sets up the AdView and requests an ad.
     */
    private void setupAdView() {
        // Sets the adview to display an ad to the user
        mAdView = (AdView) findViewById(R.id.av_main);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // If ad fails to load, hides this adview
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
                .addTestDevice("7387C5A63BE83E951937A7F2842F6C28")
                .addTestDevice("699FFDF176FEE8F8B6AD7E3D322A43AB");
        mAdView.loadAd(builder.build());
    }

    /**
     * Gets a new instance of {@link ca.josephroque.bowlingcompanion.fragment.StatsListFragment} and displays it.
     *
     * @param tag represents fragment which should be returned to when backstack is popped
     */
    private void openStatsFragment(String tag) {
        StatsListFragment statsListFragment = StatsListFragment.newInstance();
        startFragmentTransaction(statsListFragment, tag, Constants.FRAGMENT_STAT_LIST);
    }

    /**
     * Gets a new instance of {@link ca.josephroque.bowlingcompanion.fragment.StatsGraphFragment} and displays it.
     *
     * @param statCategory category of stat to display
     * @param statIndex index in category of stat to display
     */
    public void openStatGraph(int statCategory, int statIndex) {
        StatsGraphFragment fragment = StatsGraphFragment.newInstance(statCategory, statIndex);
        startFragmentTransaction(fragment,
                Constants.FRAGMENT_STAT_LIST,
                Constants.FRAGMENT_STAT_GRAPH);
    }

    /**
     * Gets a new instance of {@link ca.josephroque.bowlingcompanion.fragment.MatchPlayFragment} and displays it.
     *
     * @param gameId id of game o
     */
    public void openMatchPlayStats(long gameId) {
        MatchPlayFragment fragment = MatchPlayFragment.newInstance(gameId);
        startFragmentTransaction(fragment,
                Constants.FRAGMENT_GAME,
                Constants.FRAGMENT_MATCH_PLAY);
    }

    /**
     * Sets title of action bar to string pointed to by resId.
     *
     * @param resId id of string to be set at title
     * @param override indicates if reference to resId title should be saved in mTitle
     */
    public void setActionBarTitle(int resId, boolean override) {
        // Changing title theme color
        // final String hexColor = DataFormatter.getHexColorFromInt(Theme.getHeaderFontThemeColor());

        // if (getSupportActionBar() != null)
        // getSupportActionBar().setTitle(Html.fromHtml("<font color=\"" + hexColor + "\">"
        // + getResources().getString(resId) + "</font>"));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(resId);
        if (override)
            mTitle = resId;
    }

    /**
     * Opens the settings activity.
     */
    private void openSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, ca.josephroque.bowlingcompanion.SettingsActivity.class);
        startActivity(settingsIntent);
    }

    /**
     * Opens the tutorial.
     */
    private void openSplashActivity() {
        Intent tutorialIntent = new Intent(MainActivity.this, SplashActivity.class);
        tutorialIntent.putExtra(Constants.EXTRA_IGNORE_WATCHED, true);
        startActivity(tutorialIntent);
    }

    /**
     * Returns id of current bowler being used in fragments.
     *
     * @return value of mBowlerId
     */
    public long getBowlerId() {
        return mBowlerId;
    }

    /**
     * Returns id of current league being used in fragments.
     *
     * @return value of mLeagueId
     */
    public long getLeagueId() {
        return mLeagueId;
    }

    /**
     * Returns id of current series being used in fragments.
     *
     * @return value of mSeriesId
     */
    public long getSeriesId() {
        return mSeriesId;
    }

    /**
     * Returns current number of games being used in fragments.
     *
     * @return value of mDefaultNumberOfGames
     */
    public int getDefaultNumberOfGames() {
        return mDefaultNumberOfGames;
    }

    /**
     * Returns current number of games being used for series.
     *
     * @return value of mNumberOfGamesForSeries
     */
    public int getNumberOfGamesForSeries() {
        return mNumberOfGamesForSeries;
    }

    /**
     * Returns name of current bowler being used in fragments.
     *
     * @return value of mBowlerName
     */
    public String getBowlerName() {
        return mBowlerName;
    }

    /**
     * Returns name of current league being used in fragments.
     *
     * @return value of mLeagueName
     */
    public String getLeagueName() {
        return mLeagueName;
    }

    /**
     * Returns id of current game being used in fragments.
     *
     * @return value of mGameId
     */
    public long getGameId() {
        return mGameId;
    }

    /**
     * Returns game number in current series.
     *
     * @return value of mGameNumber
     */
    public int getGameNumber() {
        return mGameNumber;
    }

    /**
     * Returns name of current series being used in fragments.
     *
     * @return value of mSeriesId
     */
    public String getSeriesDate() {
        return mSeriesDate;
    }

    /**
     * Returns true if the navigation drawer is currently open, false otherwise.
     *
     * @return true if the drawer is open, false otherwise
     */
    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Returns true if the fragments are in event mode, false otherwise.
     *
     * @return the value of mIsEventMode
     */
    public boolean isEventMode() {
        return mIsEventMode;
    }

    /**
     * Returns true if a quick series is being created, false otherwise.
     *
     * @return the value of mIsQuickSeries
     */
    public boolean isQuickSeries() {
        return mIsQuickSeries;
    }

    /**
     * Loads game data related to seriesId and displays it in a new GameFragment instance.
     */
    private static final class OpenSeriesTask
            extends AsyncTask<Boolean, Void, Object[]> {

        /** Weak reference to the parent activity. */
        private final WeakReference<MainActivity> mMainActivity;

        /**
         * Assigns a weak reference to the parent activity.
         *
         * @param activity parent activity
         */
        private OpenSeriesTask(MainActivity activity) {
            mMainActivity = new WeakReference<>(activity);
        }

        @Override
        protected Object[] doInBackground(Boolean... isEvent) {
            MainActivity mainActivity = mMainActivity.get();
            if (mainActivity == null)
                return null;

            long[] gameId = new long[mainActivity.mNumberOfGamesForSeries];
            long[] frameId = new long[mainActivity.mNumberOfGamesForSeries * Constants.NUMBER_OF_FRAMES];
            boolean[] gameLocked = new boolean[mainActivity.mNumberOfGamesForSeries];
            boolean[] manualScore = new boolean[mainActivity.mNumberOfGamesForSeries];

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
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
            String[] rawSeriesArgs = {String.valueOf(mainActivity.mSeriesId)};

            int currentGame = -1;
            long currentGameId = -1;
            int currentFrame = -1;
            Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    long newGameId = cursor.getLong(cursor.getColumnIndex("gid"));
                    if (newGameId == currentGameId)
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                    else {
                        currentGameId = newGameId;
                        frameId[++currentFrame] = cursor.getLong(cursor.getColumnIndex("fid"));
                        gameId[++currentGame] = currentGameId;
                        gameLocked[currentGame] = cursor.getInt(
                                cursor.getColumnIndex(GameEntry.COLUMN_IS_LOCKED)) == 1;
                        manualScore[currentGame] = cursor.getInt(
                                cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1;
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();

            return new Object[]{gameId, frameId, gameLocked, manualScore, isEvent[0]};
        }

        @SuppressWarnings("CheckStyle")
        @Override
        protected void onPostExecute(Object[] params) {
            MainActivity mainActivity = mMainActivity.get();
            if (mainActivity == null || params == null)
                return;

            long[] gameIds = (long[]) params[0];
            long[] frameIds = (long[]) params[1];
            boolean[] gameLocked = (boolean[]) params[2];
            boolean[] manualScore = (boolean[]) params[3];
            mainActivity.mIsEventMode = (boolean) params[4];

            GameFragment gameFragment = GameFragment.newInstance(gameIds, frameIds, gameLocked, manualScore);
            mainActivity.startFragmentTransaction(gameFragment, (mainActivity.isEventMode()
                    ? Constants.FRAGMENT_LEAGUES
                    : Constants.FRAGMENT_SERIES), Constants.FRAGMENT_GAME);
        }
    }

    /**
     * Creates a new series in the database and displays it in a new instance of GameFragment.
     */
    private static final class AddSeriesTask
            extends AsyncTask<Void, Void, Object[]> {

        /** Weak reference to the parent activity. */
        private final WeakReference<MainActivity> mMainActivity;

        /**
         * Assigns a weak reference to the parent activity.
         *
         * @param activity parent activity
         */
        private AddSeriesTask(MainActivity activity) {
            mMainActivity = new WeakReference<>(activity);
        }

        @Override
        protected Object[] doInBackground(Void... params) {
            MainActivity mainActivity = mMainActivity.get();

            if (mainActivity == null)
                return null;

            long seriesId = -1;
            long[] gameId = new long[mainActivity.mNumberOfGamesForSeries];
            long[] frameId = new long[mainActivity.mNumberOfGamesForSeries
                    * Constants.NUMBER_OF_FRAMES];

            SQLiteDatabase database =
                    DatabaseHelper.getInstance(mainActivity).getReadableDatabase();
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
            String seriesDate = dateFormat.format(new Date());

            database.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(SeriesEntry.COLUMN_SERIES_DATE, seriesDate);
                values.put(SeriesEntry.COLUMN_LEAGUE_ID, mainActivity.mLeagueId);
                seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values);

                for (byte i = 0; i < mainActivity.mNumberOfGamesForSeries; i++) {
                    values = new ContentValues();
                    values.put(GameEntry.COLUMN_GAME_NUMBER, i + 1);
                    values.put(GameEntry.COLUMN_SCORE, (short) 0);
                    values.put(GameEntry.COLUMN_SERIES_ID, seriesId);
                    gameId[i] = database.insert(GameEntry.TABLE_NAME, null, values);

                    for (byte j = 0; j < Constants.NUMBER_OF_FRAMES; j++) {
                        values = new ContentValues();
                        values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1);
                        values.put(FrameEntry.COLUMN_GAME_ID, gameId[i]);
                        frameId[j + Constants.NUMBER_OF_FRAMES * i] = database.insert(FrameEntry.TABLE_NAME,
                                null,
                                values);
                    }
                }

                database.setTransactionSuccessful();
            } catch (Exception ex) {
                throw new RuntimeException("Could not create new series entry in database: "
                        + ex.getMessage());
            } finally {
                database.endTransaction();
            }

            mainActivity.mSeriesId = seriesId;
            mainActivity.mSeriesDate = DateUtils.formattedDateToPrettyCompact(seriesDate);
            return new Object[]{gameId, frameId};
        }

        @Override
        protected void onPostExecute(Object[] params) {
            MainActivity mainActivity = mMainActivity.get();
            if (mainActivity == null || params == null)
                return;

            long[] gameIds = (long[]) params[0];
            long[] frameIds = (long[]) params[1];
            mainActivity.mIsEventMode = false;

            GameFragment gameFragment = GameFragment.newInstance(
                    gameIds,
                    frameIds,
                    new boolean[mainActivity.mNumberOfGamesForSeries],
                    new boolean[mainActivity.mNumberOfGamesForSeries]);
            mainActivity.startFragmentTransaction(
                    gameFragment,
                    (mainActivity.isQuickSeries()
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
    public void addSavingThread(Thread thread) {
        mQueueSavingThreads.add(thread);
    }

    /**
     * Waits thread until all saving threads in the queue have finished.
     *
     * @param activity source activity
     */
    public static void waitForSaveThreads(WeakReference<MainActivity> activity) {
        // Waits for saving to database to finish, before loading from database
        while (activity.get() != null && activity.get().mQueueSavingThreads.peek() != null) {
            try {
                //noinspection CheckStyle
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new RuntimeException("Could not wait for threads to finish saving: "
                        + ex.getMessage());
            }
            // wait for saving threads to finish
        }
    }

    /**
     * Enables or disables the navigation drawer and its menu icon.
     *
     * @param isEnabled true to enable the drawer, false to disable
     */
    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
        }
    }

    /**
     * Checks if the user has opened the navigation drawer and, if not, opens it.
     */
    private void learnNavigationDrawer() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean(NavigationUtils.NAVIGATION_DRAWER_LEARNED, false)) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            preferences.edit().putBoolean(NavigationUtils.NAVIGATION_DRAWER_LEARNED, true).apply();
        }
    }

    @Override
    public void loadGameScoresForDrawer(final long[] gameIds) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        gameScores[curGame++] = (short) cursor.getInt(cursor.getColumnIndex(
                                GameEntry.COLUMN_SCORE));
                        cursor.moveToNext();
                    }
                }
                cursor.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < gameIds.length; i++) {
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
            extends Handler {

        /**
         * Sends {@code Looper} to super class.
         *
         * @param looper looper
         */
        private AutoAdvanceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            // does nothing;
        }
    }
}
