package ca.josephroque.bowlingcompanion.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * Created by Joseph Roque on 2015-07-24. Displays some content which describes the functionality of the application to
 * the user.
 */
public class TutorialFragment
        extends Fragment
        implements Theme.ChangeableTheme {

    /** To identify output from this class in the Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SplashActivity";

    /** Represents page of the tutorial displayed by this fragment. */
    private static final String ARG_PAGE = "arg_page";

    /** Number of pages in the tutorial. */
    public static final int TUTORIAL_TOTAL_PAGES = 6;
    /** Vertical padding between views. */
    private static final int VERTICAL_SPACE = 16;
    /** Size of the text in tutorials. */
    private static final int TUTORIAL_TEXT_SIZE_SP = 16;

    /**
     * A cached array of the {@link ca.josephroque.bowlingcompanion.fragment.TutorialFragment.TutorialPage} items.
     */
    private static TutorialPage[] sTutorialPageValues;

    /** Displays layout to showcase tutorial step. */
    private View mViewTutorial;
    /** Explains the tutorial step. */
    private TextView mTextViewTutorial;
    /** Additional tutorial step explanation. */
    private TextView mTextViewExtra;

    /** Page of the tutorial shown in this fragment. */
    private TutorialPage mTutorialPage;
    /** Indicates if the current device is a tablet. */
    private boolean mIsTablet = false;

    /**
     * Creates a new instance of TutorialFragment showing the page defined.
     *
     * @param page tutorial page to show
     * @return a new instance of TutorialFragment
     */
    public static TutorialFragment newInstance(int page) {
        if (page < 0 || page >= TUTORIAL_TOTAL_PAGES)
            throw new IllegalArgumentException("page must be between 0 and "
                    + (TUTORIAL_TOTAL_PAGES - 1));

        TutorialFragment instance = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsTablet = !getResources().getBoolean(R.bool.portrait_only);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout rootView =
                (RelativeLayout) inflater.inflate(R.layout.fragment_tutorial, container, false);

        Bundle args = (savedInstanceState == null)
                ? getArguments()
                : savedInstanceState;

        if (sTutorialPageValues == null)
            sTutorialPageValues = TutorialPage.values();
        mTutorialPage = sTutorialPageValues[args.getInt(ARG_PAGE)];

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setupTutorialPageAndText((RelativeLayout) getView());
        updateTheme();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PAGE, mTutorialPage.ordinal());
    }

    @Override
    public void updateTheme() {
        if (getView() != null) {
            switch (mTutorialPage) {
                case WELCOME:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_blue_tertiary));
                    break;
                case ADD_BOWLER_LEAGUE_SERIES:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_purple_tertiary));
                    break;
                case LEAGUES_VS_EVENTS:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_red_tertiary));
                    break;
                case PIN_GAME_AND_SWIPE:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_orange_tertiary));
                    break;
                case STATISTICS_AND_GRAPH:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_green_tertiary));
                    break;
                case SETTINGS:
                    getView().setBackgroundColor(DisplayUtils.getColorResource(getResources(),
                            R.color.theme_grey_tertiary));
                    break;
                default:
                    // does nothing
            }
        }
    }

    /**
     * Initializes the text and image objects for the tutorial.
     *
     * @param rootView root to attach views to
     */
    private void setupTutorialPageAndText(RelativeLayout rootView) {
        final int dp16 = DisplayUtils.getPixelsFromDP(getResources().getDisplayMetrics().density,
                VERTICAL_SPACE);

        mTextViewTutorial = new TextView(getContext());
        mTextViewTutorial.setId(R.id.tv_tutorial);
        mTextViewTutorial.setPadding(dp16, dp16, dp16, dp16);
        mTextViewTutorial.setGravity(Gravity.CENTER_HORIZONTAL);
        mTextViewTutorial.setTextSize(TypedValue.COMPLEX_UNIT_SP, TUTORIAL_TEXT_SIZE_SP);
        mTextViewTutorial.setTextColor(DisplayUtils.getColorResource(getResources(), android.R.color.white));

        switch (mTutorialPage) {
            case WELCOME:
                createWelcomeTutorialPage();
                break;
            case ADD_BOWLER_LEAGUE_SERIES:
                createAddItemTutorialPage();
                break;
            case LEAGUES_VS_EVENTS:
                createLeaguesAndEventsTutorialPage();
                break;
            case PIN_GAME_AND_SWIPE:
                createPinGameAndSwipeTutorialPage();
                break;
            case STATISTICS_AND_GRAPH:
                createStatisticsTutorialPage();
                break;
            case SETTINGS:
                createSettingsTutorialPage();
                break;
            default:
                throw new IllegalStateException("invalid tutorial page: " + mTutorialPage);
        }

        setTutorialLayout(rootView);
    }

    /**
     * Sets up layout for settings tutorial.
     */
    private void createSettingsTutorialPage() {
        if (mTutorialPage != TutorialPage.SETTINGS)
            throw new IllegalStateException("invalid tutorial page");

        FloatingActionButton fab;
        mTextViewTutorial.setText(R.string.text_tutorial_settings);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_settings, null);

        // Setting colors of first fab
        fab = (FloatingActionButton) mViewTutorial.findViewById(R.id.fab_tutorial_add_1);
        DisplayUtils.setFloatingActionButtonColors(fab,
                DisplayUtils.getColorResource(getResources(), R.color.theme_blue_primary),
                DisplayUtils.getColorResource(getResources(), R.color.theme_blue_tertiary));
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), fab);

        // Setting colors of second fab
        fab = (FloatingActionButton) mViewTutorial.findViewById(R.id.fab_tutorial_add_2);
        DisplayUtils.setFloatingActionButtonColors(fab,
                DisplayUtils.getColorResource(getResources(), R.color.theme_red_primary),
                DisplayUtils.getColorResource(getResources(), R.color.theme_red_tertiary));
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), fab);

        // Setting colors of third fab
        fab = (FloatingActionButton) mViewTutorial.findViewById(R.id.fab_tutorial_add_3);
        DisplayUtils.setFloatingActionButtonColors(fab,
                DisplayUtils.getColorResource(getResources(), R.color.theme_green_primary),
                DisplayUtils.getColorResource(getResources(), R.color.theme_green_tertiary));
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), fab);
    }

    /**
     * Sets up layout for statistics tutorial.
     */
    private void createStatisticsTutorialPage() {
        if (mTutorialPage != TutorialPage.STATISTICS_AND_GRAPH)
            throw new IllegalStateException("invalid tutorial page");

        mTextViewTutorial.setText(R.string.text_tutorial_statistics);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_statistics, null);
    }

    /**
     * Sets up layout for scoring games tutorial.
     */
    private void createPinGameAndSwipeTutorialPage() {
        if (mTutorialPage != TutorialPage.PIN_GAME_AND_SWIPE)
            throw new IllegalStateException("invalid tutorial page");

        final int dp16 = DisplayUtils.getPixelsFromDP(getResources().getDisplayMetrics().density,
                VERTICAL_SPACE);

        mTextViewTutorial.setText(R.string.text_tutorial_pin_game);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_pin_game, null);

        mTextViewExtra = new TextView(getContext());
        mTextViewExtra.setPadding(dp16, dp16, dp16, dp16);
        mTextViewExtra.setGravity(Gravity.CENTER_HORIZONTAL);
        mTextViewExtra.setTextSize(TypedValue.COMPLEX_UNIT_SP, TUTORIAL_TEXT_SIZE_SP);
        mTextViewExtra.setTextColor(DisplayUtils.getColorResource(getResources(), android.R.color.white));
        mTextViewExtra.setText(R.string.text_tutorial_manual_game);
    }

    /**
     * Sets up layout for leagues vs. events tutorial.
     */
    private void createLeaguesAndEventsTutorialPage() {
        if (mTutorialPage != TutorialPage.LEAGUES_VS_EVENTS)
            throw new IllegalStateException("invalid tutorial page");

        mTextViewTutorial.setText(R.string.text_tutorial_leagues_events);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_leagues_events, null);
    }

    /**
     * Sets up layout for adding bowlers/leagues tutorial.
     */
    private void createAddItemTutorialPage() {
        if (mTutorialPage != TutorialPage.ADD_BOWLER_LEAGUE_SERIES)
            throw new IllegalStateException("invalid tutorial page");

        FloatingActionButton fab;
        mTextViewTutorial.setText(R.string.text_tutorial_add_bowler_league_series);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_add, null);

        // Setting colors of first fab
        fab = (FloatingActionButton) mViewTutorial.findViewById(R.id.fab_tutorial_add_person);
        DisplayUtils.setFloatingActionButtonColors(fab,
                DisplayUtils.getColorResource(getResources(), R.color.theme_orange_primary),
                DisplayUtils.getColorResource(getResources(), R.color.theme_orange_tertiary));
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), fab);

        // Setting colors of second fab
        fab = (FloatingActionButton) mViewTutorial.findViewById(R.id.fab_tutorial_add);
        DisplayUtils.setFloatingActionButtonColors(fab,
                DisplayUtils.getColorResource(getResources(), R.color.theme_orange_primary),
                DisplayUtils.getColorResource(getResources(), R.color.theme_orange_tertiary));
        DisplayUtils.fixFloatingActionButtonMargins(getResources(), fab);
    }

    /**
     * Sets up layout for welcome tutorial.
     */
    private void createWelcomeTutorialPage() {
        if (mTutorialPage != TutorialPage.WELCOME)
            throw new IllegalStateException("invalid tutorial page");

        mTextViewTutorial.setText(R.string.text_tutorial_welcome);
        mViewTutorial = View.inflate(getContext(), R.layout.tutorial_welcome, null);
    }

    /**
     * Sets the layout to alternate between two setups depending on the page.
     *
     * @param rootView root to attach views to
     */
    private void setTutorialLayout(RelativeLayout rootView) {
        final float maxTutorialWidth = getResources().getDimension(R.dimen.max_tutorial_width);
        RelativeLayout.LayoutParams layoutParams;
        rootView.removeAllViews();

        final int dp16 = DisplayUtils.getPixelsFromDP(getResources().getDisplayMetrics().density,
                VERTICAL_SPACE);

        if (mIsTablet)
            layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        else
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp16, dp16, dp16, dp16);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootView.addView(mViewTutorial, layoutParams);

        if (mIsTablet)
            layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        else
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.tutorial_content);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootView.addView(mTextViewTutorial, layoutParams);

        if (mTextViewExtra != null) {
            if (mIsTablet)
                layoutParams = new RelativeLayout.LayoutParams((int) maxTutorialWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.tutorial_content);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rootView.addView(mTextViewExtra, layoutParams);
        }
    }

    /**
     * Possible tutorial pages.
     */
    public enum TutorialPage {
        /** Tutorial page to introduce the app. */
        WELCOME,
        /** Tutorial page for adding new bowlers, leagues and series. */
        ADD_BOWLER_LEAGUE_SERIES,
        /** Tutorial page for leagues vs events. */
        LEAGUES_VS_EVENTS,
        /** Tutorial page for recording a game with pins. */
        PIN_GAME_AND_SWIPE,
        /** Tutorial page for viewing statistics and graphs. */
        STATISTICS_AND_GRAPH,
        /** Tutorial page for changing settings. */
        SETTINGS,
    }
}
