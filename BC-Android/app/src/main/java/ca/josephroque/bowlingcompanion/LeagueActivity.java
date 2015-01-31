package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.prefs.Preferences;

import ca.josephroque.bowlingcompanion.adapter.LeagueTabsPagerAdapter;
import ca.josephroque.bowlingcompanion.database.BowlingContract;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.AddLeagueDialog;
import ca.josephroque.bowlingcompanion.fragments.LeagueFragment;
import ca.josephroque.bowlingcompanion.fragments.TournamentFragment;

/**
 * Created by josephroque on 15-01-09.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class LeagueActivity extends ActionBarActivity
    implements AddLeagueDialog.AddLeagueDialogListener, ActionBar.TabListener
{

    /** TAG identifier for output to log */
    private static final String TAG = "LeagueActivity";

    /** Instance of the activity view pager */
    private ViewPager viewPager = null;
    /** Instance of the activity action bar */
    private ActionBar actionBar = null;
    /** Names for the tabs */
    private String[] tabs = {"Leagues", "Tournaments"};

    /** Indicates currently selected tab */
    private int currentTabPosition = 0;

    /** Layout which shows the tutorial first time */
    private RelativeLayout topLevelLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league);

        viewPager = (ViewPager)findViewById(R.id.pager_league);
        actionBar = getSupportActionBar();
        LeagueTabsPagerAdapter leagueTabAdapter = new LeagueTabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(leagueTabAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab : tabs)
        {
            actionBar.addTab(actionBar.newTab()
                    .setText(tab)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
                currentTabPosition = position;
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
            @Override
            public void onPageScrollStateChanged(int state){}
        });

        topLevelLayout = (RelativeLayout)findViewById(R.id.league_top_layout);
        if (hasShownTutorial())
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_SERIES, -1)
                .putLong(Constants.PREFERENCES_ID_GAME, -1)
                .putLong(Constants.PREFERENCES_ID_LEAGUE, -1)
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (topLevelLayout.getVisibility() == View.VISIBLE)
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_bowler_stats:
                showBowlerStats();
                return true;
            case R.id.action_new_league:
                showAddLeagueDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction)
    {
        if (topLevelLayout != null && topLevelLayout.getVisibility() == View.VISIBLE)
        {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }

        viewPager.setCurrentItem(tab.getPosition());
        currentTabPosition = tab.getPosition();
    }
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction){}
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction){}

    /**
     * Creates a StatsActivity to show the complete stats
     * of the selected bowler
     */
    private void showBowlerStats()
    {
        getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE)
                .edit()
                .putLong(Constants.PREFERENCES_ID_LEAGUE, -1)
                .putLong(Constants.PREFERENCES_ID_GAME, -1)
                .putLong(Constants.PREFERENCES_ID_SERIES, -1);

        Intent statsIntent = new Intent(LeagueActivity.this, StatsActivity.class);
        startActivity(statsIntent);
    }

    /**
     * Creates an instance of AddLeagueDialogFragment to create a new league
     * for the selected bowler
     */
    private void showAddLeagueDialog()
    {
        DialogFragment dialog = new AddLeagueDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.PREFERENCES_TOURNAMENT_MODE, currentTabPosition == 0);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), (currentTabPosition == 0)
                ? "AddLeagueDialogFragment"
                : "AddTournamentDialogFragment");
    }

    @Override
    public void onAddNewLeague(String leagueName, int numberOfGames)
    {
        switch(currentTabPosition)
        {
            case 0:
                LeagueFragment leagueFragment = (LeagueFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_league + ":" + viewPager.getCurrentItem());
                leagueFragment.addNewLeague(leagueName, numberOfGames);
                break;
            case 1:
                TournamentFragment tournamentFragment = (TournamentFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager_league + ":" + viewPager.getCurrentItem());
                tournamentFragment.addNewTournament(leagueName, numberOfGames);
                break;
            default:
                Log.w(TAG, "Unavailable fragment! Current tab: " + currentTabPosition);
                break;
        }
    }

    @Override
    public void onCancelNewLeague()
    {
        //do nothing
    }

    /**
     * Displays a tutorial overlay if one hasn't been shown to
     * the user yet
     *
     * @return true if the tutorial has already been shown, false otherwise
     */
    private boolean hasShownTutorial()
    {
        SharedPreferences preferences = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        boolean hasShownTutorial = preferences.getBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_LEAGUE, false);

        if (!hasShownTutorial)
        {
            preferences.edit()
                    .putBoolean(Constants.PREFERENCES_HAS_SHOWN_TUTORIAL_LEAGUE, true)
                    .apply();
            topLevelLayout.setVisibility(View.VISIBLE);
            topLevelLayout.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    topLevelLayout.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
        }
        return hasShownTutorial;
    }
}
