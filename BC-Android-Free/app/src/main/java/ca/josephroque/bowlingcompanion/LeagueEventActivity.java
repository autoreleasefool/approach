package ca.josephroque.bowlingcompanion;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import ca.josephroque.bowlingcompanion.adapter.LeagueEventFragmentPagerAdapter;


public class LeagueEventActivity extends ActionBarActivity
{

    private static final String TAG = "LeagueEventActivity";

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

        ViewPager mViewPager = (ViewPager)findViewById(R.id.viewPager_leagues_events);
        mViewPager.setAdapter(new LeagueEventFragmentPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip mTabStrip = (PagerSlidingTabStrip)
                findViewById(R.id.slidingTab_leagues_events);
        mTabStrip.setViewPager(mViewPager);
        mTabStrip.setTextColor(getResources().getColor(R.color.secondary_background));
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
