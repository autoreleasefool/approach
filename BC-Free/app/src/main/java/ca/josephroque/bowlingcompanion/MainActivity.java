package ca.josephroque.bowlingcompanion;

import android.graphics.drawable.ColorDrawable;
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
        Theme.ChangeableTheme,
        BowlerFragment.OnBowlerSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Theme.loadTheme(this);

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
            case R.id.action_settings:
                //TODO: show settings
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        transaction.replace(R.id.fl_main_fragment_container, leagueEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
