package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import ca.josephroque.bowlingcompanion.adapter.LeagueEventFragmentPagerAdapter;
import ca.josephroque.bowlingcompanion.dialog.NewLeagueEventDialog;
import ca.josephroque.bowlingcompanion.fragments.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class LeagueEventActivity extends ActionBarActivity
    implements NewLeagueEventDialog.NewLeagueEventDialogListener, ChangeableTheme
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "LeagueEventActivity";

    /** ViewPager which displays fragments to user */
    private ViewPager mLeagueEventViewPager;

    private long mBowlerId = -1;
    private String mBowlerName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_event);

        mLeagueEventViewPager = (ViewPager)findViewById(R.id.viewPager_leagues_events);
        mLeagueEventViewPager.setAdapter(new LeagueEventFragmentPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)
                findViewById(R.id.slidingTab_leagues_events);
        tabStrip.setViewPager(mLeagueEventViewPager);
        tabStrip.setTextColor(getResources().getColor(R.color.secondary_background));

        if (savedInstanceState != null)
        {
            mBowlerId = savedInstanceState.getLong(Constants.EXTRA_ID_BOWLER);
            mBowlerName = savedInstanceState.getString(Constants.EXTRA_NAME_BOWLER);
        }

        updateTheme();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mBowlerId == -1)
        {
            mBowlerId = getIntent().getLongExtra(Constants.EXTRA_ID_BOWLER, -1);
            mBowlerName = getIntent().getStringExtra(Constants.EXTRA_NAME_BOWLER);
        }

        if(Theme.getLeagueEventActivityThemeInvalidated())
        {
            updateTheme();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(Constants.EXTRA_ID_BOWLER, mBowlerId);
        outState.putString(Constants.EXTRA_NAME_BOWLER, mBowlerName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league_event, menu);
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
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_stats:
                showBowlerStats();
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
     * Creates a StatsActivity to displays the stats corresponding to the current bowler
     */
    private void showBowlerStats()
    {
        Intent statsIntent = new Intent(LeagueEventActivity.this, StatsActivity.class);
        statsIntent.putExtra(Constants.EXTRA_ID_BOWLER, mBowlerId);
        statsIntent.putExtra(Constants.EXTRA_NAME_BOWLER, mBowlerName);
        startActivity(statsIntent);
    }

    @Override
    public void onAddNewLeague(String leagueEventName, byte numberOfGames)
    {
        //Calls the method addNewLeagueOrEvent(String, byte) of the current fragment instance
        int viewPagerCurrentItem = mLeagueEventViewPager.getCurrentItem();
        switch(viewPagerCurrentItem)
        {
            case 0:case 1:
            LeagueEventFragment leagueEventFragment = (LeagueEventFragment)
                    getSupportFragmentManager()
                    .findFragmentByTag(
                            "android:switcher:"
                            + R.id.viewPager_leagues_events
                            + ":"
                            + viewPagerCurrentItem);
            leagueEventFragment.addNewLeagueOrEvent(leagueEventName, numberOfGames);
            default:
                Log.w(TAG, "Unavailable fragment. Current tab: " + viewPagerCurrentItem);
                break;
        }
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.slidingTab_leagues_events);
        tabStrip.setBackgroundColor(Theme.getActionBarTabThemeColor());
        Theme.validateLeagueEventActivityTheme();
    }

    public void showNewLeagueEventInstructions(boolean shouldShowEventInstructions)
    {
        int leagueOrEventFragment = (shouldShowEventInstructions)
                ? 1
                : 0;
        LeagueEventFragment leagueEventFragment = (LeagueEventFragment)
                getSupportFragmentManager()
                        .findFragmentByTag(
                                "android:switcher:"
                                + R.id.viewPager_leagues_events
                                + ":"
                                + leagueOrEventFragment);
        leagueEventFragment.showNewLeagueEventInstructions();
    }

    public long getBowlerId() {return mBowlerId;}
    public String getBowlerName() {return mBowlerName;}
}
