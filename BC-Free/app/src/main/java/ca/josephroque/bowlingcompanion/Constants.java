package ca.josephroque.bowlingcompanion;

/**
 * Created by josephroque on 15-03-13.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Constants
{
    // PREFERENCES
    public static final String PREFS = "ca.josephroque.bowlingcompanion";
    public static final String PREF_RECENT_BOWLER_ID = "RBI";
    public static final String PREF_RECENT_LEAGUE_ID = "RLI";
    public static final String PREF_QUICK_BOWLER_ID = "QBI";
    public static final String PREF_QUICK_LEAGUE_ID = "QLI";
    public static final String PREF_THEME_COLORS = "TC";
    public static final String PREF_THEME_LIGHT = "TL";

    // EXTRAS
    public static final String EXTRA_NAME_BOWLER = "EBN";

    // REGULAR EXPRESSIONS
    public static final String REGEX_NAME = "^[A-Za-z]+[ A-Za-z]*[A-Za-z]*$";

    // NAME DEFAULTS
    public static final byte NAME_MAX_LENGTH = 30;
    public static final String NAME_OPEN_LEAGUE = "Open";
}
