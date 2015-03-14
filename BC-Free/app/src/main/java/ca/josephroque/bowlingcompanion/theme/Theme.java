package ca.josephroque.bowlingcompanion.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-03-13.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.theme
 * in project Bowling Companion
 */
public class Theme
{
    /** Identifier for the current theme */
    private static String sThemeName = null;

    /** Primary color for the current theme */
    private static int sThemeColorPrimary = -1;
    /** Secondary color for the current theme */
    private static int sThemeColorSecondary = -1;
    /** Tertiary color for the current theme */
    private static int sThemeColorTertiary = -1;
    /** Long press effect color for the current theme */
    private static int sThemeColorLongPress = -1;
    /** Number of milliseconds which short animations will last for */
    private static int sShortAnimationDuration = -1;
    /** Number of milliseconds which medium animations will last for */
    private static int sMediumAnimationDuration = -1;
    /** List item background color for the current theme */
    private static int sThemeListItemBackground = -1;

    /** Indicates whether the main activity has an invalid theme */
    private static boolean sMainActivityThemeInvalid = true;

    /**
     * Loads the default theme from the preferences, or the theme
     * which was set by the user in previous runs of the app
     *
     * @param context current context to obtain values from
     */
    public static void loadTheme(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = prefs.getString(Constants.PREF_THEME_COLORS, "Green");
        boolean lightTheme = prefs.getBoolean(Constants.PREF_THEME_LIGHT, true);
        setTheme(context, themeName, lightTheme);
    }

    /**
     * Sets the theme colors to the specified theme
     *
     * @param context current context to obtain values from
     * @param themeName color of the theme to load
     * @param lightThemeEnabled indicates whether the light variation of a theme is enabled
     */
    public static void setTheme(Context context, String themeName, boolean lightThemeEnabled)
    {
        sThemeName = themeName;
        if (sThemeName == null)
            sThemeName = "Green";

        if (sShortAnimationDuration == -1)
            sShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (sMediumAnimationDuration == -1)
            sMediumAnimationDuration = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        if (sThemeListItemBackground == -1)
            sThemeListItemBackground = context.getResources().getColor(R.color.secondary_background);

        switch(sThemeName)
        {
            case "Green":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_green_primary
                        : R.color.theme_dark_green_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_green_secondary
                        : R.color.theme_dark_green_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_green_tertiary
                        : R.color.theme_dark_green_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_green_recyclerview_longpress);
                break;
            case "Orange":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_orange_primary
                        : R.color.theme_dark_orange_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_orange_secondary
                        : R.color.theme_dark_orange_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_orange_tertiary
                        : R.color.theme_dark_orange_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_orange_recyclerview_longpress);
                break;
            case "Blue":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_blue_primary
                        : R.color.theme_dark_blue_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_blue_secondary
                        : R.color.theme_dark_blue_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_blue_tertiary
                        : R.color.theme_dark_blue_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_blue_recyclerview_longpress);
                break;
            case "Purple":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_purple_primary
                        : R.color.theme_dark_purple_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_purple_secondary
                        : R.color.theme_dark_purple_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_purple_tertiary
                        : R.color.theme_dark_purple_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_purple_recyclerview_longpress);
                break;
            case "Red":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_red_primary
                        : R.color.theme_dark_red_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_red_secondary
                        : R.color.theme_dark_red_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_red_tertiary
                        : R.color.theme_dark_red_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_red_recyclerview_longpress);
                break;
            case "Gray":
                sThemeColorPrimary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_gray_primary
                        : R.color.theme_dark_gray_primary);
                sThemeColorSecondary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_gray_secondary
                        : R.color.theme_dark_gray_secondary);
                sThemeColorTertiary = context.getResources().getColor(
                        (lightThemeEnabled)
                        ? R.color.theme_light_gray_tertiary
                        : R.color.theme_dark_gray_tertiary);
                sThemeColorLongPress =
                        context.getResources().getColor(R.color.theme_gray_recyclerview_longpress);
                break;
            default:
                //If an invalid theme was selected, the default is applied
                setTheme(context, "Green", true);
                return;
        }

        invalidateThemes();
    }

    /**
     * Indicates that all activities will need to update their themes
     */
    private static void invalidateThemes()
    {
        sMainActivityThemeInvalid = true;
    }

    /**
     * Sets the background color of a view depending on the build version
     *
     * @param view view to set background of
     * @param background drawable to set background to
     */
    public static void setBackgroundByAPI(View view, Drawable background)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackgroundDrawable(background);
        }
        else
        {
            view.setBackground(background);
        }
    }

    /**
     * Checks if the main activity's theme is valid
     * @return the value of sMainActivityThemeInvalidated
     */
    public static boolean getMainActivityThemeInvalidated() {return sMainActivityThemeInvalid;}

    /**
     * Should be called when main activity theme has been updated
     */
    public static void validateMainActivityTheme() {sMainActivityThemeInvalid = false;}

    /**
     * Gets the primary color for the theme
     * @return the value of sThemeColorPrimary
     */
    public static int getPrimaryThemeColor() {return sThemeColorPrimary;}
    /**
     * Gets the secondary color for the theme
     * @return the value of sThemeColorSecondary
     */
    public static int getSecondaryThemeColor() {return sThemeColorSecondary;}
    /**
     * Gets the tertiary color for the theme
     * @return the value of sThemeColorTertiary
     */
    public static int getTertiaryThemeColor() {return sThemeColorTertiary;}
    /**
     * Gets the long press animation color for the theme
     * @return the value of sThemeColorLongPress
     */
    public static int getLongPressThemeColor() {return sThemeColorLongPress;}
    /**
     * Gets the list item color for the theme
     * @return the value of sThemeListItemBackground
     */
    public static int getListItemBackground() {return sThemeListItemBackground;}
    /**
     * Gets the duration to be used for short animations
     * @return the value of sShortAnimationDuration
     */
    public static int getShortAnimationDuration() {return sShortAnimationDuration;}
    /**
     * Gets the duration to be used for medium animations
     * @return the value of sMediumAnimationDuration
     */
    public static int getMediumAnimationDuration() {return sMediumAnimationDuration;}

    /**
     * Provides methods to update the theme colors of an object
     */
    public static interface ChangeableTheme
    {
        /**
         * When overridden, should update colors of relevant objects
         * and views to match the theme
         */
        public void updateTheme();
    }
}
