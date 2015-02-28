package ca.josephroque.bowlingcompanion.theme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-27.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Theme
{

    private static int sThemeColorActionBar = -1;
    private static int sThemeColorActionBarTabs = -1;
    private static int sThemeColorActionButton = -1;
    private static int sThemeColorActionButtonRipple = -1;

    private static boolean sLightThemeVariationEnabled = true;
    private static String sThemeName = "Green";

    private static boolean sMainActivityThemeSet = true;
    private static boolean sLeagueEventActivityThemeSet = true;
    private static boolean sLeagueFragmentThemeSet = true;
    private static boolean sEventFragmentThemeSet = true;
    private static boolean sSeriesActivityThemeSet = true;
    private static boolean sGameActivityThemeSet = true;
    private static boolean sStatsActivityThemeSet = true;
    private static boolean sSettingsActivityThemeSet = true;

    public static void loadTheme(Activity activity)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String themeName = preferences.getString(Constants.KEY_PREF_THEME_COLORS, "Green");
        boolean lightTheme = preferences.getBoolean(Constants.KEY_PREF_THEME_LIGHT, true);
        setTheme(activity, themeName, lightTheme);
    }

    public static void setTheme(Activity srcActivity, String themeName, boolean lightThemeEnabled)
    {
        sLightThemeVariationEnabled = lightThemeEnabled;

        if (themeName == null)
        {
            themeName = sThemeName;
        }
        sThemeName = themeName;

        if(themeName.equals("Green"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                        ? R.color.theme_light_green_primary
                        : R.color.theme_dark_green_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                        ? R.color.theme_light_green_secondary
                        : R.color.theme_dark_green_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_green_primary
                            : R.color.theme_dark_green_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_green_tertiary
                            : R.color.theme_dark_green_tertiary);
        }
        else if (themeName.equals("Orange"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_orange_primary
                            : R.color.theme_dark_orange_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_orange_secondary
                            : R.color.theme_dark_orange_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_orange_primary
                            : R.color.theme_dark_orange_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_orange_tertiary
                            : R.color.theme_dark_orange_tertiary);
        }
        else if (themeName.equals("Blue"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_blue_primary
                            : R.color.theme_dark_blue_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_blue_secondary
                            : R.color.theme_dark_blue_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_blue_primary
                            : R.color.theme_dark_blue_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_blue_tertiary
                            : R.color.theme_dark_blue_tertiary);
        }
        else if (themeName.equals("Purple"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_purple_primary
                            : R.color.theme_dark_purple_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_purple_secondary
                            : R.color.theme_dark_purple_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_purple_primary
                            : R.color.theme_dark_purple_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_purple_tertiary
                            : R.color.theme_dark_purple_tertiary);
        }
        else if (themeName.equals("Red"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_red_primary
                            : R.color.theme_dark_red_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_red_secondary
                            : R.color.theme_dark_red_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_red_primary
                            : R.color.theme_dark_red_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_red_tertiary
                            : R.color.theme_dark_red_tertiary);
        }
        else if (themeName.equals("Grayscale"))
        {
            sThemeColorActionBar = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_gray_primary
                            : R.color.theme_dark_gray_primary);
            sThemeColorActionBarTabs = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_gray_secondary
                            : R.color.theme_dark_gray_secondary);
            sThemeColorActionButton = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_gray_primary
                            : R.color.theme_dark_gray_primary);
            sThemeColorActionButtonRipple = srcActivity.getResources().getColor(
                    (lightThemeEnabled)
                            ? R.color.theme_light_gray_tertiary
                            : R.color.theme_dark_gray_tertiary);
        }

        invalidateActivityThemes();
    }

    private static void invalidateActivityThemes()
    {
        sMainActivityThemeSet = true;
        sLeagueEventActivityThemeSet = true;
        sLeagueFragmentThemeSet = true;
        sEventFragmentThemeSet = true;
        sSeriesActivityThemeSet = true;
        sGameActivityThemeSet = true;
        sStatsActivityThemeSet = true;
        sSettingsActivityThemeSet = true;
    }

    public static boolean getMainActivityThemeInvalidated(){return sMainActivityThemeSet;}
    public static boolean getLeagueEventActivityThemeInvalidated(){return sLeagueEventActivityThemeSet;}
    public static boolean getLeagueFragmentThemeInvalidated(){return sLeagueFragmentThemeSet;}
    public static boolean getEventFragmentThemeInvalidated(){return sEventFragmentThemeSet;}
    public static boolean getSeriesActivityThemeInvalidated(){return sSeriesActivityThemeSet;}
    public static boolean getGameActivityThemeInvalidated(){return sGameActivityThemeSet;}
    public static boolean getStatsActivityThemeInvalidated(){return sStatsActivityThemeSet;}
    public static boolean getSettingsActivityThemeInvalidated(){return sSettingsActivityThemeSet;}

    public static void validateMainActivityTheme(){sMainActivityThemeSet = true;}
    public static void validateLeagueEventActivityTheme(){sLeagueEventActivityThemeSet = true;}
    public static void validateLeagueFragmentTheme(){sLeagueFragmentThemeSet = true;}
    public static void validateEventFragmentTheme(){sEventFragmentThemeSet = true;}
    public static void validateSeriesActivityTheme(){sSeriesActivityThemeSet = true;}
    public static void validateGameActivityTheme(){sGameActivityThemeSet = true;}
    public static void validateStatsActivityTheme(){sStatsActivityThemeSet = true;}
    public static void validateSettingsActivityTheme(){sSettingsActivityThemeSet = true;}

    public static int getActionBarThemeColor() {return sThemeColorActionBar;}
    public static int getActionBarTabThemeColor() {return sThemeColorActionBarTabs;}
    public static int getActionButtonThemeColor() {return sThemeColorActionButton;}
    public static int getActionButtonRippleThemeColor() {return sThemeColorActionButtonRipple;}
}
