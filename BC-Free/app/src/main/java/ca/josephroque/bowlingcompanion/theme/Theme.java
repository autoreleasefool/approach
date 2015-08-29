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
        switch (themeName) {
            case "Green":
                loadThemeColors(resources,
                        R.color.theme_green_highlight,
                        R.color.theme_green_primary,
                        R.color.theme_green_secondary,
                        R.color.theme_green_tertiary,
                        R.color.theme_green_status);
                break;
            case "Orange":
                loadThemeColors(resources,
                        R.color.theme_orange_highlight,
                        R.color.theme_orange_primary,
                        R.color.theme_orange_secondary,
                        R.color.theme_orange_tertiary,
                        R.color.theme_orange_status);
                break;
            case "Blue":
                loadThemeColors(resources,
                        R.color.theme_blue_highlight,
                        R.color.theme_blue_primary,
                        R.color.theme_blue_secondary,
                        R.color.theme_blue_tertiary,
                        R.color.theme_blue_status);
                break;
            case "Purple":
                loadThemeColors(resources,
                        R.color.theme_purple_highlight,
                        R.color.theme_purple_primary,
                        R.color.theme_purple_secondary,
                        R.color.theme_purple_tertiary,
                        R.color.theme_purple_status);
                break;
            case "Red":
                loadThemeColors(resources,
                        R.color.theme_red_highlight,
                        R.color.theme_red_primary,
                        R.color.theme_red_secondary,
                        R.color.theme_red_tertiary,
                        R.color.theme_red_status);
                break;
            case "Grey":
                loadThemeColors(resources,
                        R.color.theme_gray_highlight,
                        R.color.theme_gray_primary,
                        R.color.theme_gray_secondary,
                        R.color.theme_gray_tertiary,
                        R.color.theme_gray_status);
                break;
            default:
                //If an invalid theme was selected, the default is applied
                setTheme(context, "Blue");
        }
    }

    /**
     * Loads theme colors from resources.
     *
     * @param resources to get resources
     * @param highlightColorId id of highlight theme color
     * @param primaryColorId id of primary theme color
     * @param secondaryColorId id of secondary theme color
     * @param tertiaryColorId id of tertiary theme color
     * @param statusColorId id of status bar theme color
     */
    private static void loadThemeColors(Resources resources,
                                        int highlightColorId,
                                        int primaryColorId,
                                        int secondaryColorId,
                                        int tertiaryColorId,
                                        int statusColorId) {
        sThemeColorHighlight = DisplayUtils.getColorResource(resources, highlightColorId);
        sThemeColorPrimary = DisplayUtils.getColorResource(resources, primaryColorId);
        sThemeColorSecondary = DisplayUtils.getColorResource(resources, secondaryColorId);
        sThemeColorTertiary = DisplayUtils.getColorResource(resources, tertiaryColorId);
        sThemeColorStatus = DisplayUtils.getColorResource(resources, statusColorId);
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
