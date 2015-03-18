package ca.josephroque.bowlingcompanion;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ca.josephroque.bowlingcompanion.fragment.BowlerFragment;
import ca.josephroque.bowlingcompanion.fragment.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.fragment.SeriesFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class MainActivity extends ActionBarActivity
    implements
        FragmentManager.OnBackStackChangedListener,
        Theme.ChangeableTheme,
        BowlerFragment.OnBowlerSelectedListener,
        LeagueEventFragment.OnLeagueSelectedListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "MainActivity";

    /** Id of current bowler being used in fragments */
    private long mBowlerId = -1;
    /** Id of current league being used in fragments */
    private long mLeagueId = -1;
    /** Id of current series being used in fragments */
    private long mSeriesId = -1;
    /** Number of games in current league/event in fragments */
    private byte mNumberOfGames = -1;
    /** Name of current bowler being used in fragments */
    private String mBowlerName;
    /** Name of current league being used in fragments */
    private String mLeagueName;
    /** Date of current series being used in fragments */
    private String mSeriesDate;

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
            mBowlerName = savedInstanceState.getString(Constants.EXTRA_NAME_BOWLER);
            mLeagueName = savedInstanceState.getString(Constants.EXTRA_NAME_LEAGUE);
            mSeriesDate = savedInstanceState.getString(Constants.EXTRA_NAME_SERIES);
            mNumberOfGames = savedInstanceState.getByte(Constants.EXTRA_NUMBER_OF_GAMES, (byte)-1);
        }

        //TODO: AppRater.appLaunched(getActivity());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Saves member variables to bundle
        outState.putLong(Constants.EXTRA_ID_BOWLER, mBowlerId);
        outState.putLong(Constants.EXTRA_ID_LEAGUE, mLeagueId);
        outState.putLong(Constants.EXTRA_ID_SERIES, mSeriesId);
        outState.putString(Constants.EXTRA_NAME_BOWLER, mBowlerName);
        outState.putString(Constants.EXTRA_NAME_LEAGUE, mLeagueName);
        outState.putString(Constants.EXTRA_NAME_SERIES, mSeriesDate);
        outState.putByte(Constants.EXTRA_NUMBER_OF_GAMES, mNumberOfGames);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //Updates theme if invalid
        if (Theme.getMainActivityThemeInvalidated())
        {
            updateTheme();
        }
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
                            break;
                        default:
                            Log.w(TAG, "Invalid back stack name: " + backStackEntryName);
                    }
                }
                return true;
            case R.id.action_settings:
                //TODO: show settings
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
        Theme.validateMainActivityTheme();
    }

    @Override
    public void onBowlerSelected(long bowlerId, String bowlerName)
    {
        LeagueEventFragment leagueEventFragment = LeagueEventFragment.newInstance();
        mBowlerId = bowlerId;
        mBowlerName = bowlerName;

        mLeagueId = -1;
        mSeriesId = -1;
        mNumberOfGames = -1;
        mLeagueName = null;
        mSeriesDate = null;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fl_main_fragment_container, leagueEventFragment)
                .addToBackStack(Constants.FRAGMENT_BOWLERS)
                .commit();
    }

    @Override
    public void onLeagueSelected(long leagueId, String leagueName, byte numberOfGames)
    {
        SeriesFragment seriesFragment = SeriesFragment.newInstance();
        mLeagueId = leagueId;
        mLeagueName = leagueName;
        mNumberOfGames = numberOfGames;

        mSeriesId = -1;
        mSeriesDate = null;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fl_main_fragment_container, seriesFragment)
                .addToBackStack(Constants.FRAGMENT_LEAGUES)
                .commit();
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
     * Returns name of current series being used in fragments
     * @return value of mSeriesId
     */
    public String getSeriesDate(){return mSeriesDate;}
}
