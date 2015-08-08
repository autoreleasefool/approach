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

    /** Number of items at the top of the navigation drawer which do not change. */
    public static final int NAVIGATION_STATIC_ITEMS = 2;

    /** Represents the header item in the navigation drawer. */
    public static final String NAVIGATION_ITEM_HEADER = "Header";
    /** Represents the item for bowlers in the navigation drawer. */
    public static final String NAVIGATION_ITEM_BOWLERS = "Bowlers";
    /** Represents the item for leagues and events in the navigation drawer. */
    public static final String NAVIGATION_ITEM_LEAGUES = "Leagues & Events";
    /** Represents the item for series in the navigation drawer. */
    public static final String NAVIGATION_ITEM_SERIES = "Series";
    /** Represents the subheader for games in the navigation drawer. */
    public static final String NAVIGATION_SUBHEADER_GAMES = "Games";
    /** Represents the subheader for other in the navigation drawer. */
    public static final String NAVIGATION_SUBHEADER_OTHER = "Other";
    /** Represents the item for settings in the navigation drawer. */
    public static final String NAVIGATION_ITEM_SETTINGS = "Settings";
    /** Represents the item for help in the navigation drawer. */
    public static final String NAVIGATION_ITEM_HELP = "Help";
    /** Represents the item for feedback in the navigation drawer. */
    public static final String NAVIGATION_ITEM_FEEDBACK = "Feedback";

    /** Offset of the drawer. 0 is fully closed, 1 is fully open. */
    private static float sDrawerOffset;

    /**
     * Updates the offset of the drawer.
     *
     * @param drawerOffset new value for drawer offset
     */
    public static void setDrawerOffset(float drawerOffset)
    {
        sDrawerOffset = drawerOffset;
    }

    /**
     * Gets the current offset of the drawer.
     *
     * @return the value of {@code sDrawerOffset}
     */
    public static float getDrawerOffset()
    {
        return sDrawerOffset;
    }

    /**
     * Default private constructor.
     */
    private NavigationUtils()
    {
        // does nothing
    }
}
