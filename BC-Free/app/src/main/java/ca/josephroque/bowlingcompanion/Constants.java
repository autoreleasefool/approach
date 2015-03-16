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
    public static final String EXTRA_NAME_LEAGUE = "ENL";
    public static final String EXTRA_ID_BOWLER = "EIB";
    public static final String EXTRA_EVENT_MODE = "EEM";
    public static final String EXTRA_NUMBER_OF_GAMES = "ENOG";

    // REGULAR EXPRESSIONS
    public static final String REGEX_NAME = "^[A-Za-z]+[ A-Za-z]*[A-Za-z]*$";
    public static final String REGEX_LEAGUE_EVENT_NAME = "^[A-Za-z0-9]+[ A-Za-z0-9]*[A-Za-z0-9]*$";

    // GAMES
    public static final byte NUMBER_OF_FRAMES = 10;
    public static final byte MAX_NUMBER_EVENT_GAMES = 20;
    public static final byte MAX_NUMBER_LEAGUE_GAMES = 5;

    // NAMES
    public static final byte NAME_MAX_LENGTH = 30;
    public static final String NAME_OPEN_LEAGUE = "Open";
}
