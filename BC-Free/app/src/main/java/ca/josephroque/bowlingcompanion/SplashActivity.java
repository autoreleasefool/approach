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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.josephroque.bowlingcompanion.adapter.SplashPagerAdapter;
import ca.josephroque.bowlingcompanion.fragment.TutorialFragment;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Displays a tutorial to the user.
 */
public class SplashActivity
        extends FragmentActivity
        implements Theme.ChangeableTheme {

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
    /** View to indicate the user's current page in the tutorial. */
    private View mCurrentPageIndicator;

    /** View to provide navigation to the next item. */
    private TextView mTextViewNext;
    /** View to provide navigation to the previous item. */
    private TextView mTextViewBack;

    /** Current page of view pager. */
    private int mCurrentTutorialPage = 0;
    /** Indicates if the activity was restored from a saved instance state. */
    private boolean mFromSavedInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.loadTheme(this);

        boolean ignoreWatched = (getIntent() != null)
                && getIntent().getBooleanExtra(Constants.EXTRA_IGNORE_WATCHED, false);

        if (savedInstanceState != null) {
            mCurrentTutorialPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, 0);
            mFromSavedInstanceState = true;
        }

        // Phones can access portrait only
        if (getResources().getBoolean(R.bool.portrait_only))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(PREF_TUTORIAL_WATCHED, false) && !ignoreWatched) {
            openMainActivity();
            return;
        }

        preferences.edit().putBoolean(PREF_TUTORIAL_WATCHED, true).apply();
        setupViewPager();
        setupNavigationButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFromSavedInstanceState)
            mViewPagerContent.setCurrentItem(mCurrentTutorialPage);

        updateTheme();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_PAGE, mCurrentTutorialPage);
    }

    @Override
    public void updateTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    private void setupNavigationButtons() {
        mTextViewNext = (TextView) findViewById(R.id.tv_skip);
        mTextViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPagerContent.getCurrentItem() < TutorialFragment.TUTORIAL_TOTAL_PAGES - 1)
                    mViewPagerContent.setCurrentItem(mViewPagerContent.getCurrentItem() + 1);
                else
                    openMainActivity();
            }
        });

        mTextViewBack = (TextView) findViewById(R.id.tv_back);
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPagerContent.getCurrentItem() > 0)
                    mViewPagerContent.setCurrentItem(mViewPagerContent.getCurrentItem() - 1);
            }
        });
    }

    /**
     * Starts an instance of {@link MainActivity}.
     */
    private void openMainActivity() {
        Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    /**
     * Gets adapter for view pager and initializes views.
     */
    private void setupViewPager() {
        final int indicatorSize = getResources().getDimensionPixelSize(R.dimen.indicator_size);
        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        LinearLayout linearLayoutToolbar = (LinearLayout) findViewById(R.id.ll_splash_toolbar);
        final View[] positionIndicators = createPageIndicators(indicatorSize, linearLayoutToolbar);
        mViewPagerContent = (ViewPager) findViewById(R.id.splash_view_pager);

        /* Manages pages in the view pager. */
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(getSupportFragmentManager());
        mViewPagerContent.setAdapter(splashPagerAdapter);

        mCurrentPageIndicator = new View(SplashActivity.this);
        mCurrentPageIndicator.setBackgroundResource(R.drawable.position_indicator);
        mCurrentPageIndicator.setAlpha(INDICATOR_ACTIVE);
        rootLayout.addView(mCurrentPageIndicator, new RelativeLayout.LayoutParams(indicatorSize, indicatorSize));

        mViewPagerContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int[] currentLocation = new int[2];
                int[] nextLocation = new int[2];

                positionIndicators[position].getLocationInWindow(currentLocation);
                if (position < TutorialFragment.TUTORIAL_TOTAL_PAGES - 1) {
                    positionIndicators[position + 1].getLocationInWindow(nextLocation);
                }
                mCurrentPageIndicator.setX(
                        currentLocation[0] + (nextLocation[0] - currentLocation[0]) * positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentTutorialPage = position;
                if (mCurrentTutorialPage == TutorialFragment.TUTORIAL_TOTAL_PAGES - 1)
                    mTextViewNext.setText(R.string.text_continue);
                else
                    mTextViewNext.setText(R.string.text_skip);

                if (mCurrentTutorialPage == 0)
                    mTextViewBack.setVisibility(View.GONE);
                else
                    mTextViewBack.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // does nothing
            }
        });
    }

    /**
     * Sets up views to indicate the current page and aligns them at the bottom of the activity.
     *
     * @param indicatorSize size of position indicators
     * @param linearLayoutToolbar container for page indicators
     * @return an array of indicators, one for each tutorial page
     */
    private View[] createPageIndicators(final int indicatorSize, LinearLayout linearLayoutToolbar) {
        final View[] positionIndicators = new View[TutorialFragment.TUTORIAL_TOTAL_PAGES];

        for (int i = 0; i < positionIndicators.length; i++) {
            positionIndicators[i] = new View(SplashActivity.this);
            positionIndicators[i].setAlpha(INDICATOR_INACTIVE);
            positionIndicators[i].setBackgroundResource(R.drawable.position_indicator);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            layoutParams.setMargins(indicatorSize, indicatorSize, indicatorSize, indicatorSize);
            linearLayoutToolbar.addView(positionIndicators[i], layoutParams);
        }

        positionIndicators[0].getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation") // uses newer APIs where available
                    public void onGlobalLayout() {
                        View rootView = findViewById(R.id.rl_splash);
                        final int statusBarOffset = getResources().getDisplayMetrics().heightPixels
                                - rootView.getMeasuredHeight();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            positionIndicators[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else
                            positionIndicators[0].getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        int[] locations = new int[2];
                        positionIndicators[0].getLocationOnScreen(locations);
                        mCurrentPageIndicator.setX(locations[0]);
                        mCurrentPageIndicator.setY(locations[1] - statusBarOffset);
                    }
                });

        return positionIndicators;
    }
}
