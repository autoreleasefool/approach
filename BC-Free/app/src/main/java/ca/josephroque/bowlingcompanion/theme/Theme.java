package ca.josephroque.bowlingcompanion.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by Joseph Roque on 15-03-13.
 * <p/>
 * Provides methods which load and return colors and other values to provide a consistent
 * theme across the application, which can be altered by the user
 */
public final class Theme
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Theme";

    /**
     * Default private constructor.
     */
    private Theme()
    {
        // does nothing
    }

    /** Identifier for the current theme. */
    private static String sThemeName = null;

    /** Primary color for the current theme. */
    private static int sThemeColorPrimary = -1;
    /** Secondary color for the current theme. */
    private static int sThemeColorSecondary = -1;
    /** Tertiary color for the current theme. */
    private static int sThemeColorTertiary = -1;
    /** Long press effect color for the current theme. */
    private static int sThemeColorLongPress = -1;
    /** Number of milliseconds which medium animations will last for. */
    private static int sMediumAnimationDuration = -1;
    /** List item background color for the current theme. */
    private static int sThemeListItemBackground = -1;

    /**
     * Loads the default theme from the preferences, or the theme
     * which was set by the user in previous runs of the app.
     *
     * @param context current context to obtain values from
     */
    public static void loadTheme(Context context)
    {
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
                                String themeName)
    {
        sThemeName = themeName;
        if (sThemeName == null)
            sThemeName = "Blue";

        if (sMediumAnimationDuration == -1)
            sMediumAnimationDuration = context.getResources().getInteger(
                    android.R.integer.config_mediumAnimTime);
        if (sThemeListItemBackground == -1)
            sThemeListItemBackground = context.getResources().getColor(
                    R.color.secondary_background);

        switch (sThemeName)
        {
            case "Green":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_green_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_green_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_green_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_green_recyclerview_longpress);
                break;
            case "Orange":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_orange_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_orange_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_orange_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(
                                R.color.theme_orange_recyclerview_longpress);
                break;
            case "Blue":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_blue_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_blue_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_blue_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_blue_recyclerview_longpress);
                break;
            case "Purple":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_purple_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_purple_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_purple_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(
                                R.color.theme_purple_recyclerview_longpress);
                break;
            case "Red":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_red_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_red_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_red_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_red_recyclerview_longpress);
                break;
            case "Grey":
                sThemeColorPrimary = context.getResources().getColor(
                        R.color.theme_gray_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        R.color.theme_gray_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        R.color.theme_gray_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_gray_recyclerview_longpress);
                break;
            default:
                //If an invalid theme was selected, the default is applied
                setTheme(context, "Blue");
        }
    }

    /**
     * Returns the current theme color name.
     *
     * @return value of sThemeName
     */
    public static String getThemeName()
    {
        return sThemeName;
    }

    /**
     * Gets the primary color for the theme.
     *
     * @return the value of sThemeColorPrimary
     */
    public static int getPrimaryThemeColor()
    {
        return sThemeColorPrimary;
    }

    /**
     * Gets the secondary color for the theme.
     *
     * @return the value of sThemeColorSecondary
     */
    public static int getSecondaryThemeColor()
    {
        return sThemeColorSecondary;
    }

    /**
     * Gets the tertiary color for the theme.
     *
     * @return the value of sThemeColorTertiary
     */
    public static int getTertiaryThemeColor()
    {
        return sThemeColorTertiary;
    }

    /**
     * Gets the long press animation color for the theme.
     *
     * @return the value of sThemeColorLongPress
     */
    public static int getLongPressThemeColor()
    {
        return sThemeColorLongPress;
    }

    /**
     * Gets the list item color for the theme.
     *
     * @return the value of sThemeListItemBackground
     */
    public static int getListItemBackground()
    {
        return sThemeListItemBackground;
    }

    /**
     * Gets the duration to be used for medium animations.
     *
     * @return the value of sMediumAnimationDuration
     */
    public static int getMediumAnimationDuration()
    {
        return sMediumAnimationDuration;
    }

    /**
     * Provides methods to update the theme colors of an object.
     */
    public interface ChangeableTheme
    {
        /**
         * When overridden, should update colors of relevant objects
         * and views to match the theme.
         */
        @SuppressWarnings("unused")
        // Is used by classes implementing interface
        void updateTheme();
    }
}
