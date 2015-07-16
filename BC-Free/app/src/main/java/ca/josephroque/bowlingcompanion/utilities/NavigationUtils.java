package ca.josephroque.bowlingcompanion.utilities;

/**
 * Created by Joseph Roque on 2015-07-16. Offers utilities to set up the navigation drawer for the
 * application.
 */
public final class NavigationUtils
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NavigationUtils";

    /** Represents a boolean indicating if the navigation drawer has been opened for the user. */
    public static final String NAVIGATION_DRAWER_LEARNED = "nav_drawer_learned";

    /** Maximum width of the navigation drawer in dps. */
    public static final int MAX_NAVIGATION_DRAWER_WIDTH_DP = 320;

    public static final String NAVIGATION_ITEM_BOWLERS = "Bowlers";
    public static final String NAVIGATION_ITEM_LEAGUES = "Leagues & Events";
    public static final String NAVIGATION_ITEM_SERIES = "Series";
    public static final String NAVIGATION_SUBHEADER_OTHER = "Other";
    public static final String NAVIGATION_ITEM_SETTINGS = "Settings";
    public static final String NAVIGATION_ITEM_FEEDBACK = "Feedback";

    /**
     * Default private constructor.
     */
    private NavigationUtils()
    {
        // does nothing
    }
}
