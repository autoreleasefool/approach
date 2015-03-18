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
    /** Identifier for SharedPreferences of app */
    public static final String PREFS = "ca.josephroque.bowlingcompanion";
    /** Identifier for most recently selected bowler id, stored in preferences */
    public static final String PREF_RECENT_BOWLER_ID = "RBI";
    /** Identifier for most recently selected league id, stored in preferences */
    public static final String PREF_RECENT_LEAGUE_ID = "RLI";
    /** Identifier for custom set bowler id, stored in preferences */
    public static final String PREF_QUICK_BOWLER_ID = "QBI";
    /** Identifier for custom set league id, stored in preferences */
    public static final String PREF_QUICK_LEAGUE_ID = "QLI";
    /** Identifier for set theme color, stored in preferences */
    public static final String PREF_THEME_COLORS = "TC";
    /** Identifier for set theme variation, stored in preferences */
    public static final String PREF_THEME_LIGHT = "TL";

    // EXTRAS
    /** Identifies the name of a bowler as an extra */
    public static final String EXTRA_NAME_BOWLER = "EBN";
    /** Identifies the name of a league as an extra */
    public static final String EXTRA_NAME_LEAGUE = "ENL";
    /** Identifies the name of a series as an extra */
    public static final String EXTRA_NAME_SERIES = "ENS";
    /** Identifies the id of a bowler as an extra */
    public static final String EXTRA_ID_BOWLER = "EIB";
    /** Identifies the id of a league as an extra */
    public static final String EXTRA_ID_LEAGUE = "EIL";
    /** Identifies the id of a series as an extra */
    public static final String EXTRA_ID_SERIES = "EIS";
    /** Identifies state of event mode as an extra */
    public static final String EXTRA_EVENT_MODE = "EEM";
    /** Identifies the number of games as an extra */
    public static final String EXTRA_NUMBER_OF_GAMES = "ENOG";

    // REGULAR EXPRESSIONS
    /** Regular Expression to match regular names */
    public static final String REGEX_NAME = "^[A-Za-z]+[ A-Za-z]*[A-Za-z]*$";
    /** Regular Expression to match regular names with numbers */
    public static final String REGEX_LEAGUE_EVENT_NAME = "^[A-Za-z0-9]+[ A-Za-z0-9]*[A-Za-z0-9]*$";

    // GAMES
    /** Number of frames in a game */
    public static final byte NUMBER_OF_FRAMES = 10;
    /** Maximum number of games in an event */
    public static final byte MAX_NUMBER_EVENT_GAMES = 20;
    /** Maximum number of games in a league */
    public static final byte MAX_NUMBER_LEAGUE_GAMES = 5;

    // NAMES
    /** Maximum length of a regular name */
    public static final byte NAME_MAX_LENGTH = 30;
    /** Name of a default league available to every bowler */
    public static final String NAME_OPEN_LEAGUE = "Open";

    // FRAGMENTS
    /** Tag to identify instances of BowlerFragment */
    public static final String FRAGMENT_BOWLERS = "BowlerFragment";
    /** Tag to identify instances of LeagueEventFragment */
    public static final String FRAGMENT_LEAGUES = "LeagueEventFragment";
    /** Tag to identify instances of SeriesFragment */
    public static final String FRAGMENT_SERIES = "SeriesFragment";
}
