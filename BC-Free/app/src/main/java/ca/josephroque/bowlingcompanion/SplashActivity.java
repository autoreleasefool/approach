package ca.josephroque.bowlingcompanion;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ca.josephroque.bowlingcompanion.adapter.SplashPagerAdapter;
import ca.josephroque.bowlingcompanion.fragment.TutorialFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Displays a tutorial to the user.
 */
public class SplashActivity
        extends FragmentActivity
        implements Theme.ChangeableTheme
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

    /** Current page of view pager. */
    private int mCurrentTutorialPage = 0;
    /** Indicates if the activity was restored from a saved instance state. */
    private boolean mFromSavedInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Theme.loadTheme(this);

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(PREF_TUTORIAL_WATCHED, false) && !ignoreWatched)
        {
            openMainActivity();
            return;
        }

        preferences.edit().putBoolean(PREF_TUTORIAL_WATCHED, true).apply();
        setupViewPager();
        setupSkipButton();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mFromSavedInstanceState)
            mViewPagerContent.setCurrentItem(mCurrentTutorialPage);

        updateTheme();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentTutorialPage);
    }

    @Override
    public void updateTheme()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            setTaskDescription(new ActivityManager.TaskDescription("Bowling Companion", icon,
                    Theme.getPrimaryThemeColor()));

            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(Theme.getStatusThemeColor());
        }
    }

    /**
     * Creates on click listener for skip button to open the main activity.
     */
    private void setupSkipButton()
    {
        findViewById(R.id.tv_skip).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mViewPagerContent.getCurrentItem() < TutorialFragment.TUTORIAL_TOTAL_PAGES - 1)
                    mViewPagerContent.setCurrentItem(mViewPagerContent.getCurrentItem() + 1);
                else
                    openMainActivity();
            }
        });
    }

    /**
     * Starts an instance of {@link MainActivity}.
     */
    private void openMainActivity()
    {
        Intent mainActivityIntet = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainActivityIntet);
        finish();
    }

    /**
     * Gets adapter for view pager and initializes views.
     */
    private void setupViewPager()
    {
        mViewPagerContent = (ViewPager) findViewById(R.id.splash_view_pager);
        /* Manages pages in the view pager. */
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(getSupportFragmentManager());
        mViewPagerContent.setAdapter(splashPagerAdapter);
        LinearLayout linearLayoutToolbar = (LinearLayout) findViewById(R.id.ll_splash_toolbar);

        final View[] positionIndicator = new View[TutorialFragment.TUTORIAL_TOTAL_PAGES];
        for (int i = 0; i < positionIndicator.length; i++)
        {
            final int viewId = getResources().getIdentifier("view_indicator_" + i, "id",
                    getPackageName());
            positionIndicator[i] = linearLayoutToolbar.findViewById(viewId);
            positionIndicator[i].setAlpha(INDICATOR_INACTIVE);
        }
        positionIndicator[mCurrentTutorialPage].setAlpha(INDICATOR_ACTIVE);

        mViewPagerContent.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                //Changes which page indicator is 'highlighted'
                positionIndicator[mCurrentTutorialPage].setAlpha(INDICATOR_INACTIVE);
                positionIndicator[position].setAlpha(INDICATOR_ACTIVE);

                mCurrentTutorialPage = position;
            }
        });
    }
}
