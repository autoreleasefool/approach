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

    private long mBowlerId = -1;
    private long mLeagueId = -1;
    private long mSeriesId = -1;
    private byte mNumberOfGames = -1;
    private String mBowlerName;
    private String mLeagueName;
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
            BowlerFragment bowlerFragment = BowlerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_main_fragment_container, bowlerFragment)
                    .commit();
        }
        else
        {
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

    public void shouldDisplayHomeUp()
    {
        boolean canBack = (getSupportFragmentManager().getBackStackEntryCount() > 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
    }

    public void setActionBarTitle(int resId)
    {
        getSupportActionBar().setTitle(getResources().getString(resId));
    }

    public long getBowlerId(){return mBowlerId;}
    public long getLeagueId(){return mLeagueId;}
    public long getSeriesId(){return mSeriesId;}
    public byte getNumberOfGames(){return mNumberOfGames;}
    public String getBowlerName(){return mBowlerName;}
    public String getLeagueName(){return mLeagueName;}
    public String getSeriesDate(){return mSeriesDate;}
}
