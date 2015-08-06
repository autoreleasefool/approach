package ca.josephroque.bowlingcompanion;

/**
 * Created by Joseph Roque on 15-03-13. <p/> Provides constant values which are accessible across
 * the entire application
 */
public final class Constants
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Constants";

    /**
     * Default private constructor.
     */
    private Constants()
    {
        // does nothing
    }

    // SETTINGS KEYS
    /** Identifier for preference which should open app in play store. */
    public static final String KEY_RATE = "pref_rate";
    /** Identifier for preference which should open email intent. */
    public static final String KEY_REPORT_BUG = "pref_report_bug";
    /** Identifier for preference which should open email intent. */
    public static final String KEY_COMMENT_SUGGESTION = "pref_comment_suggestion";
    /** Identifier for preference which indicates if quick bowlers are set. */
    public static final String KEY_ENABLE_QUICK = "pref_enable_quick";
    /** Identifier for preference which allows user to select a bowler. */
    public static final String KEY_QUICK_BOWLER = "pref_quick_bowler";
    /** Identifier for preference which allows user to select a league belong to bowler. */
    public static final String KEY_QUICK_LEAGUE = "pref_quick_league";
    /** Identifier for preference which indicates if events should be included in stats. */
    public static final String KEY_INCLUDE_EVENTS = "pref_include_events";
    /** Identifier for preference which indicates if open games should be included in stats. */
    public static final String KEY_INCLUDE_OPEN = "pref_include_open";
    /** Identifier for preference which allows user to select a theme color. */
    public static final String KEY_THEME_COLORS = "pref_theme_colors";
    /** Identifier for preference which allows user to select a minimum score to be highlighted. */
    public static final String KEY_HIGHLIGHT_SCORE = "pref_highlight_score";
    /** Identifier for preference which allows user to enable auto advancing frames. */
    public static final String KEY_ENABLE_AUTO_ADVANCE = "pref_enable_auto_advance";
    /** Identifier for preference which allows user to select time interval before auto advance. */
    public static final String KEY_AUTO_ADVANCE_TIME = "pref_auto_advance_time";
    /** Identifier for preference for if app should ask user to combine similar series. */
    public static final String KEY_ASK_COMBINE = "pref_ask_combine";
    /** Identifier for preference for if floating buttons should be shown when editing a game. */
    public static final String KEY_ENABLE_FABS = "pref_enable_fabs";

    // PREFERENCES
    /** Identifier for SharedPreferences of app. */
    public static final String PREFS = "ca.josephroque.bowlingcompanion";
    /** Identifier for most recently selected bowler id, stored in preferences. */
    public static final String PREF_RECENT_BOWLER_ID = "RBI";
    /** Identifier for most recently selected league id, stored in preferences. */
    public static final String PREF_RECENT_LEAGUE_ID = "RLI";
    /** Identifier for custom set bowler id, stored in preferences. */
    public static final String PREF_QUICK_BOWLER_ID = "QBI";
    /** Identifier for custom set league id, stored in preferences. */
    public static final String PREF_QUICK_LEAGUE_ID = "QLI";

    // EXTRAS
    /** Identifies the name of a bowler as an extra. */
    public static final String EXTRA_NAME_BOWLER = "EBN";
    /** Identifies the name of a league as an extra. */
    public static final String EXTRA_NAME_LEAGUE = "ENL";
    /** Identifies the name of a series as an extra. */
    public static final String EXTRA_NAME_SERIES = "ENS";
    /** Identifies the id of a bowler as an extra. */
    public static final String EXTRA_ID_BOWLER = "EIB";
    /** Identifies the id of a league as an extra. */
    public static final String EXTRA_ID_LEAGUE = "EIL";
    /** Identifies the id of a series as an extra. */
    public static final String EXTRA_ID_SERIES = "EIS";
    /** Identifies the id of a game as an extra. */
    public static final String EXTRA_ID_GAME = "EIG";
    /** Identifies the number of a game as an extra. */
    public static final String EXTRA_GAME_NUMBER = "EGN";
    /** Identifies state of event mode as an extra. */
    public static final String EXTRA_EVENT_MODE = "EEM";
    /** Identifies state of quick series as an extra. */
    public static final String EXTRA_QUICK_SERIES = "EQS";
    /** Identifies the number of games as an extra. */
    public static final String EXTRA_NUMBER_OF_GAMES = "ENOG";
    /** Identifies the number of games for a newly created series. */
    public static final String EXTRA_GAMES_IN_SERIES = "EGIS";
    /** Identifies an array of ids of games. */
    public static final String EXTRA_ARRAY_GAME_IDS = "EAGI";
    /** Identifies an array of ids of frames. */
    public static final String EXTRA_ARRAY_FRAME_IDS = "EAFI";
    /** Identifies an array of booleans to indicate if games are locked. */
    public static final String EXTRA_ARRAY_GAME_LOCKED = "EAGL";
    /** Identifies an array of booleans to indicate if games have manual scores. */
    public static final String EXTRA_ARRAY_MANUAL_SCORE_SET = "EAMSS";
    /** Identifies the current game number for the navigation drawer. */
    public static final String EXTRA_NAV_CURRENT_GAME = "ENCG";
    /** Identifies an array of bytes to indicate results of match play. */
    public static final String EXTRA_ARRAY_MATCH_PLAY = "EAMP";
    /** Identifies byte indicating current game in GameFragment. */
    public static final String EXTRA_CURRENT_GAME = "ECG";
    /** Identifies a series object. */
    public static final String EXTRA_SERIES = "ES";
    /** Identifies a boolean indicating if the user has seen a the tutorial. */
    public static final String EXTRA_IGNORE_WATCHED = "EIW";

    // REGULAR EXPRESSIONS
    /** Regular Expression to match regular names. */
    public static final String REGEX_NAME = "^[A-Za-z]+[ A-Za-z]*[A-Za-z]*$";
    /** Regular Expression to match regular names with numbers. */
    public static final String REGEX_LEAGUE_EVENT_NAME =
            "^[A-Za-z0-9]+[ A-Za-z0-9'!@#$%^&*()_+:\"?/\\~-]*[A-Za-z0-9'!@#$%^&*()_+:\"?/\\~-]*$";

    // GAMES
    /** Number of frames in a game. */
    public static final byte NUMBER_OF_FRAMES = 10;
    /** Last frame in a game. */
    public static final byte LAST_FRAME = 9;
    /** Maximum number of games in an event. */
    public static final byte MAX_NUMBER_EVENT_GAMES = 20;
    /** Maximum number of games in a league. */
    public static final byte MAX_NUMBER_LEAGUE_GAMES = 5;

    //SCORING VALUES
    /** Symbol representing a strike. */
    public static final String BALL_STRIKE = "X";
    /** Symbol representing a spare. */
    public static final String BALL_SPARE = "/";
    /** Symbol representing a 'left'. */
    public static final String BALL_LEFT = "L";
    /** Symbol representing a 'right'. */
    public static final String BALL_RIGHT = "R";
    /** Symbol representing an 'ace'. */
    public static final String BALL_ACE = "A";
    /** Symbol representing a 'chop off'. */
    public static final String BALL_CHOP_OFF = "C/O";
    /** Symbol representing a 'split'. */
    public static final String BALL_SPLIT = "Sp";
    /** Symbol representing a 'head pin'. */
    public static final String BALL_HEAD_PIN = "Hp";
    /** Symbol representing a 'head pin + 2 pin'. */
    public static final String BALL_HEAD_PIN_2 = "H2";
    /** Symbol representing an empty frame. */
    public static final String BALL_EMPTY = "-";
    /** Array representing the state of all pins as being knocked down. */
    public static final boolean[] FRAME_PINS_DOWN = {true, true, true, true, true};

    //BALL VALUES
    /** Indicates a strike was thrown. */
    public static final byte BALL_VALUE_STRIKE = 0;
    /** Indicates a left was thrown. */
    public static final byte BALL_VALUE_LEFT = 1;
    /** Indicates a right was thrown. */
    public static final byte BALL_VALUE_RIGHT = 2;
    /** Indicates a left chop off was thrown. */
    public static final byte BALL_VALUE_LEFT_CHOP = 3;
    /** Indicates a right chop off was thrown. */
    public static final byte BALL_VALUE_RIGHT_CHOP = 4;
    /** Indicates an ace was thrown. */
    public static final byte BALL_VALUE_ACE = 5;
    /** Indicates a left split was thrown. */
    public static final byte BALL_VALUE_LEFT_SPLIT = 6;
    /** Indicates a right split was thrown. */
    public static final byte BALL_VALUE_RIGHT_SPLIT = 7;
    /** Indicates a head pin was thrown. */
    public static final byte BALL_VALUE_HEAD_PIN = 8;

    // NAMES
    /** Maximum length of a regular name. */
    public static final byte NAME_MAX_LENGTH = 30;
    /** Name of a default league available to every bowler. */
    public static final String NAME_OPEN_LEAGUE = "Open";

    // FRAGMENTS
    /** Tag to identify instances of BowlerFragment. */
    public static final String FRAGMENT_BOWLERS = "BowlerFragment";
    /** Tag to identify instances of LeagueEventFragment. */
    public static final String FRAGMENT_LEAGUES = "LeagueEventFragment";
    /** Tag to identify instances of SeriesFragment. */
    public static final String FRAGMENT_SERIES = "SeriesFragment";
    /** Tag to identify instance of GameFragment. */
    public static final String FRAGMENT_GAME = "GameFragment";
    /** Tag to identify instance of StatsListFragment. */
    public static final String FRAGMENT_STAT_LIST = "StatsListFragment";
    /** Tag to identify instance of StatsGraphFragment. */
    public static final String FRAGMENT_STAT_GRAPH = "StatsGraphFragment";

    /** Maximum score of a 5 pin game. */
    public static final int GAME_MAX_SCORE = 450;
    /** Default minimum score to highlight. */
    public static final int DEFAULT_GAME_HIGHLIGHT = 300;
}
