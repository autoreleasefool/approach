package ca.josephroque.bowlingcompanion.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * Created by Joseph Roque on 15-03-13. Provides methods which load and return colors and other values to provide a
 * consistent theme across the application, which can be altered by the user
 */
public final class Theme {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Theme";

    /** Represents the primary color of the theme. */
    public static final byte COLOR_PRIMARY = 0;
    /** Represents the secondary color of the theme. */
    public static final byte COLOR_SECONDARY = 1;
    /** Represents the tertiary color of the theme. */
    public static final byte COLOR_TERTIARY = 2;
    /** Represents the highlight color of the theme. */
    public static final byte COLOR_HIGHLIGHT = 3;
    /** Represents the status bar color of the theme. */
    public static final byte COLOR_STATUS = 4;

    /** Primary color for the current theme. */
    private static int sThemeColorHighlight = -1;
    /** Primary color for the current theme. */
    private static int sThemeColorPrimary = -1;
    /** Secondary color for the current theme. */
    private static int sThemeColorSecondary = -1;
    /** Tertiary color for the current theme. */
    private static int sThemeColorTertiary = -1;
    /** Status bar color for the current theme. */
    private static int sThemeColorStatus = -1;

    /**
     * Loads the default theme from the preferences, or the theme which was set by the user in previous runs of the
     * app.
     *
     * @param context current context to obtain values from
     */
    public static void loadTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = prefs.getString(Constants.KEY_THEME_COLORS, "Blue");
        setTheme(context, themeName);
    }

    /**
     * Sets the theme colors to the specified theme.
     *
     * @param context current context to obtain values from
     * @param themeName color of the theme to load
     */
    public static void setTheme(Context context,
                                String themeName) {
        if (themeName == null)
            themeName = "Blue";

        final Resources resources = context.getResources();
        updateThemeColors(getThemeColors(resources, themeName));
    }

    /**
     * Updates theme colors for the application.
     *
     * @param themeColors colors to be used by the theme
     */
    private static void updateThemeColors(int[] themeColors) {
        sThemeColorHighlight = themeColors[COLOR_HIGHLIGHT];
        sThemeColorPrimary = themeColors[COLOR_PRIMARY];
        sThemeColorSecondary = themeColors[COLOR_SECONDARY];
        sThemeColorTertiary = themeColors[COLOR_TERTIARY];
        sThemeColorStatus = themeColors[COLOR_STATUS];
    }

    /**
     * Retrieves the colors for the theme as defined in the resources.
     *
     * @param resources to get colors
     * @param theme theme to load
     * @return an array of integer colors, where the indices refer to colors as denoted by {@code COLOR_PRIMARY}, {@code
     * COLOR_SECONDARY}, {@code COLOR_TERTIARY}, {@code COLOR_HIGHLIGHT}, {@code COLOR_STATUS}
     */
    public static int[] getThemeColors(Resources resources, String theme) {
        switch (theme) {
            default:
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_blue_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_blue_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_blue_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_blue_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_blue_status),
                };
            case "Green":
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_green_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_green_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_green_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_green_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_green_status),
                };
            case "Orange":
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_orange_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_orange_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_orange_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_orange_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_orange_status),
                };
            case "Purple":
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_purple_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_purple_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_purple_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_purple_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_purple_status),
                };
            case "Red":
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_red_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_red_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_red_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_red_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_red_status),
                };
            case "Grey":
                return new int[]{
                        DisplayUtils.getColorResource(resources, R.color.theme_grey_primary),
                        DisplayUtils.getColorResource(resources, R.color.theme_grey_secondary),
                        DisplayUtils.getColorResource(resources, R.color.theme_grey_tertiary),
                        DisplayUtils.getColorResource(resources, R.color.theme_grey_highlight),
                        DisplayUtils.getColorResource(resources, R.color.theme_grey_status),
                };
        }
    }

    /**
     * Gets the highlight color for the theme.
     *
     * @return the value of {@code sThemeColorHighlight}
     */
    public static int getHighlightThemeColor() {
        return sThemeColorHighlight;
    }

    /**
     * Gets the primary color for the theme.
     *
     * @return the value of {@code sThemeColorPrimary}
     */
    public static int getPrimaryThemeColor() {
        return sThemeColorPrimary;
    }

    /**
     * Gets the secondary color for the theme.
     *
     * @return the value of {@code sThemeColorSecondary}
     */
    public static int getSecondaryThemeColor() {
        return sThemeColorSecondary;
    }

    /**
     * Gets the tertiary color for the theme.
     *
     * @return the value of {@code sThemeColorTertiary}
     */
    public static int getTertiaryThemeColor() {
        return sThemeColorTertiary;
    }

    /**
     * Gets the status bar color for the theme.
     *
     * @return the value of {@code sThemeColorStatus}
     */
    public static int getStatusThemeColor() {
        return sThemeColorStatus;
    }

    /**
     * Provides methods to update the theme colors of an object.
     */
    public interface ChangeableTheme {

        /**
         * When overridden, should update colors of relevant objects and views to match the theme.
         */
        @SuppressWarnings("unused")
        // Is used by classes implementing interface
        void updateTheme();
    }

    /**
     * Default private constructor.
     */
    private Theme() {
        // does nothing
    }
}
