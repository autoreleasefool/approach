package ca.josephroque.bowlingcompanion;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ca.josephroque.bowlingcompanion.fragments.SettingsFragment;
import ca.josephroque.bowlingcompanion.theme.ChangeableThemedActivity;
import ca.josephroque.bowlingcompanion.theme.Theme;


public class SettingsActivity extends ActionBarActivity
    implements ChangeableThemedActivity
{

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        updateTheme();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (Theme.getSettingsActivityThemeInvalidated())
        {
            updateTheme();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateTheme()
    {
        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(Theme.getActionBarThemeColor()));
        Theme.validateSettingsActivityTheme();
    }
}
