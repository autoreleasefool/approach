package ca.josephroque.bowlingcompanion;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ca.josephroque.bowlingcompanion.fragment.BowlerFragment;
import ca.josephroque.bowlingcompanion.fragment.LeagueEventFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class MainActivity extends ActionBarActivity
    implements
        FragmentManager.OnBackStackChangedListener,
        Theme.ChangeableTheme,
        BowlerFragment.OnBowlerSelectedListener
{

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

        //TODO: AppRater.appLaunched(getActivity());
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
                    fm.popBackStack();
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
        Bundle args = new Bundle();
        args.putLong(Constants.EXTRA_ID_BOWLER, bowlerId);
        args.putString(Constants.EXTRA_NAME_BOWLER, bowlerName);
        leagueEventFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fl_main_fragment_container, leagueEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
}
