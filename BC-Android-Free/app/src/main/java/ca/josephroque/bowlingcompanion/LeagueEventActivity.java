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


public class LeagueEventActivity extends ActionBarActivity
    implements NewLeagueEventDialog.NewLeagueEventDialogListener
{

    private static final String TAG = "LeagueEventActivity";

    private ViewPager mLeagueEventViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_event);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.primary_green)));

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(getResources().getColor(R.color.primary_background));

        mLeagueEventViewPager = (ViewPager)findViewById(R.id.viewPager_leagues_events);
        mLeagueEventViewPager.setAdapter(new LeagueEventFragmentPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)
                findViewById(R.id.slidingTab_leagues_events);
        tabStrip.setViewPager(mLeagueEventViewPager);
        tabStrip.setTextColor(getResources().getColor(R.color.secondary_background));
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
            case R.id.action_stats:
                showBowlerStats();
                return true;
            case R.id.action_settings:
                //TODO: showSettingsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBowlerStats()
    {
        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCE_ID_LEAGUE, -1)
                .putLong(Constants.PREFERENCE_ID_GAME, -1)
                .putLong(Constants.PREFERENCE_ID_SERIES, -1)
                .apply();

        Intent statsIntent = new Intent(LeagueEventActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

    @Override
    public void onAddNewLeague(String leagueEventName, byte numberOfGames)
    {
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
}
