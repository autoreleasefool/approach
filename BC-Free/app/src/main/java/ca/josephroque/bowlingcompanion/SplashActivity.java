package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;


public class SplashActivity
        extends FragmentActivity
{

    /** To identify output from this class in the Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SplashActivity";

    /** Alpha value for an active indicator dot. */
    private static final float INDICATOR_ACTIVE = 0.75f;
    /** Alpha value for an inactive indicator dot. */
    private static final float INDICATOR_INACTIVE = 0.25f;
    /** Represents the user's current page in the view pager. */
    private static final String ARG_CURRENT_PAGE = "arg_current_page";
    /** Represents a boolean indicating if the user has seen the tutorial. */
    private static final String PREF_TUTORIAL_WATCHED = "arg_tut_watched";

    /** View pager for content fragments. */
    private ViewPager mViewPagerContent;
    /** Toolbar associated with view pager. */
    private LinearLayout mLinearLayoutToolbar;

    /** Intent to initiate instance of {@link ca.josephroque.bowlingcompanion.SplashActivity}. */
    private Intent mIntentMainActivity;

    /** Current page of view pager. */
    private int mCurrentTutorialPage = 0;
    /** Indicates if the activity was restored from a saved instance state. */
    private boolean mFromSavedInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        boolean ignoreWatched = (getIntent() != null)
                && getIntent().getBooleanExtra(Constants.EXTRA_IGNORE_WATCHED, false);

        if (savedInstanceState != null)
        {
            mCurrentTutorialPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, 0);
            mFromSavedInstanceState = true;
        }

        // Phones can access portrait only
        if (getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_splash);

        mIntentMainActivity = new Intent(SplashActivity.this, MainActivity.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(PREF_TUTORIAL_WATCHED, false) && !ignoreWatched)
        {
            startActivity(mIntentMainActivity);
            finish();
            return;
        }

        setupViewPager();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mFromSavedInstanceState)
            mViewPagerContent.setCurrentItem(mCurrentTutorialPage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentTutorialPage);
    }

    /**
     * Gets adapter for view pager and initializes views.
     */
    private void setupViewPager()
    {

    }
}
