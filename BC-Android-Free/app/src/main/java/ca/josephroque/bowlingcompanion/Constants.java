package ca.josephroque.bowlingcompanion;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Constants
{

    //SETTINGS VALUES
    public static final String KEY_PREF_ENABLE_QUICK = "pref_enable_quick_series";
    public static final String KEY_PREF_QUICK_BOWLER = "pref_quick_bowler";
    public static final String KEY_PREF_QUICK_LEAGUE = "pref_quick_league";
    public static final String KEY_PREF_ENABLE_PINS = "pref_enable_pins";
    public static final String KEY_PREF_THEME_COLORS = "pref_theme_colors";
    public static final String KEY_PREF_THEME_LIGHT = "pref_theme_light";

    //GAME VALUES
    /** The maximum number of games in a league */
    public static final byte MAX_NUMBER_LEAGUE_GAMES = 5;
    /** The maximum number of games in an event */
    public static final byte MAX_NUMBER_EVENT_GAMES = 20;
    /** The maximum number of frames in a single game */
    public static final byte NUMBER_OF_FRAMES = 10;
    /** The last frame in a game (starting from frame 0) */
    public static final byte LAST_FRAME = 9;

    //SCORING VALUES
    /** Symbol representing a strike */
    public static final String BALL_STRIKE = "X";
    /** Symbol representing a spare */
    public static final String BALL_SPARE = "/";
    /** Symbol representing a 'left' */
    public static final String BALL_LEFT = "L";
    /** Symbol representing a 'right' */
    public static final String BALL_RIGHT = "R";
    /** Symbol representing an 'ace' */
    public static final String BALL_ACE = "A";
    /** Symbol representing a 'chop off' */
    public static final String BALL_CHOP_OFF = "C/O";
    /** Symbol representing a 'split' */
    public static final String BALL_SPLIT = "Sp";
    /** Symbol representing a 'head pin' */
    public static final String BALL_HEAD_PIN = "Hp";
    /** Symbol representing a 'head pin + 2 pin' */
    public static final String BALL_HEAD_PIN_2 = "H2";
    /** Symbol representing an empty frame */
    public static final String BALL_EMPTY = "-";
    /** Array representing the state of all pins as being knocked down */
    public static final boolean[] FRAME_PINS_DOWN = {true, true, true, true, true};

    //INTENT EXTRAS
    /** Key value which represents the value of "Event Mode" */
    public static final String EXTRA_EVENT_MODE = "EM";
    /** Key value which represents the value of the "Number of Games" */
    public static final String EXTRA_NUMBER_OF_GAMES = "ENOG";
    /** Key value which represents the value of "Game Ids" */
    public static final String EXTRA_ARRAY_GAME_IDS = "EAGI";
    /** Key value which represents the value of "Frame Ids" */
    public static final String EXTRA_ARRAY_FRAME_IDS = "EAFI";
    /** Key value which represents the "Game Number" */
    public static final String EXTRA_GAME_NUMBER = "EGN";
    /** Key value which represents the activity which created a new settings activity */
    public static final String EXTRA_SETTINGS_SOURCE = "ESS";

    //PREFERENCES
    /** Identifier to retrieve shared preferences */
    public static final String PREFERENCES = "ca.josephroque.bowlingcompanionfree";
    /** Key value for the id of the most recent bowler */
    public static final String PREFERENCE_ID_RECENT_BOWLER = "IdRB";
    /** Key value for the id of the most recent league */
    public static final String PREFERENCE_ID_RECENT_LEAGUE = "IdRL";
    /** Key value for the id of the set "quick" bowler */
    public static final String PREFERENCE_ID_QUICK_BOWLER = "IdQB";
    /** Key value for the id of the set "quick" league */
    public static final String PREFERENCE_ID_QUICK_LEAGUE = "IdQL";
    /** Key value for the id of the selected bowler */
    public static final String PREFERENCE_ID_BOWLER = "IdB";
    /** Key value for the id of the selected league */
    public static final String PREFERENCE_ID_LEAGUE = "IdL";
    /** Key value for the id of the selected series */
    public static final String PREFERENCE_ID_SERIES = "IdS";
    /** Key value for the id of the selected game */
    public static final String PREFERENCE_ID_GAME = "IdG";
    /** Key value for the name of the selected bowler */
    public static final String PREFERENCE_NAME_BOWLER = "NB";
    /** Key value for the name of the selected league */
    public static final String PREFERENCE_NAME_LEAGUE = "NL";

    //DIALOG BUTTON TEXT
    /** Dialog text for an "Okay" button */
    public static final String DIALOG_OKAY = "Okay";
    /** Dialog text for an "Add" button */
    public static final String DIALOG_ADD = "Add";
    /** Dialog text for a "Cancel" button */
    public static final String DIALOG_CANCEL = "Cancel";
    /** Dialog text for a "Delete" button */
    public static final String DIALOG_DELETE = "Delete";

    //NAME DEFAULTS
    /** Maximum length for the name of a bowler, league or event */
    public static final int NAME_MAX_LENGTH = 30;
    /** Reserved name of the default "Open" league */
    public static final String NAME_LEAGUE_OPEN = "Open";

    //BALL VALUES
    /** Indicates a strike was thrown */
    public static final byte BALL_VALUE_STRIKE = 0;
    /** Indicates a left was thrown */
    public static final byte BALL_VALUE_LEFT = 1;
    /** Indicates a right was thrown */
    public static final byte BALL_VALUE_RIGHT = 2;
    /** Indicates a left chop off was thrown */
    public static final byte BALL_VALUE_LEFT_CHOP = 3;
    /** Indicates a right chop off was thrown */
    public static final byte BALL_VALUE_RIGHT_CHOP = 4;
    /** Indicates an ace was thrown */
    public static final byte BALL_VALUE_ACE = 5;
    /** Indicates a left split was thrown */
    public static final byte BALL_VALUE_LEFT_SPLIT = 6;
    /** Indicates a right split was thrown */
    public static final byte BALL_VALUE_RIGHT_SPLIT = 7;
    /** Indicates a head pin was thrown */
    public static final byte BALL_VALUE_HEAD_PIN = 8;

    //STAT ARRAY INDICES
    /** Index which should correspond to the number of middle hits */
    public static final byte STAT_MIDDLE_HIT = 0;
    /** Index which should correspond to the number of strikes */
    public static final byte STAT_STRIKES = 1;
    /** Index which should correspond to the number of spares */
    public static final byte STAT_SPARE_CONVERSIONS = 2;
    /** Index which should correspond to the number of head pins */
    public static final byte STAT_HEAD_PINS = 3;
    /** Index which should correspond to the number of head pins spared */
    public static final byte STAT_HEAD_PINS_SPARED = 4;
    /** Index which should correspond to the number of lefts */
    public static final byte STAT_LEFTS = 5;
    /** Index which should correspond to the number of lefts spared */
    public static final byte STAT_LEFTS_SPARED = 6;
    /** Index which should correspond to the number of rights */
    public static final byte STAT_RIGHTS = 7;
    /** Index which should correspond to the number of rights spared */
    public static final byte STAT_RIGHTS_SPARED = 8;
    /** Index which should correspond to the number of aces */
    public static final byte STAT_ACES = 9;
    /** Index which should correspond to the number of aces spared */
    public static final byte STAT_ACES_SPARED = 10;
    /** Index which should correspond to the number of chop offs */
    public static final byte STAT_CHOP_OFFS = 11;
    /** Index which should correspond to the number of chop offs spared */
    public static final byte STAT_CHOP_OFFS_SPARED = 12;
    /** Index which should correspond to the number of left chop offs */
    public static final byte STAT_LEFT_CHOP_OFFS = 13;
    /** Index which should correspond to the number of left chop offs spared */
    public static final byte STAT_LEFT_CHOP_OFFS_SPARED = 14;
    /** Index which should correspond to the number of right chop offs */
    public static final byte STAT_RIGHT_CHOP_OFFS = 15;
    /** Index which should correspond to the number of right chop offs spared */
    public static final byte STAT_RIGHT_CHOP_OFFS_SPARED = 16;
    /** Index which should correspond to the number of splits */
    public static final byte STAT_SPLITS = 17;
    /** Index which should correspond to the number of splits spared */
    public static final byte STAT_SPLITS_SPARED = 18;
    /** Index which should correspond to the number of left splits */
    public static final byte STAT_LEFT_SPLITS = 19;
    /** Index which should correspond to the number of left splits spared */
    public static final byte STAT_LEFT_SPLITS_SPARED = 20;
    /** Index which should correspond to the number of right splits */
    public static final byte STAT_RIGHT_SPLITS = 21;
    /** Index which should correspond to the number of right splits spared */
    public static final byte STAT_RIGHT_SPLITS_SPARED = 22;
    /** Index which should correspond to the number of fouls */
    public static final byte STAT_FOULS = 23;
    /** Index which should correspond to the number of pins left on deck */
    public static final byte STAT_PINS_LEFT_ON_DECK = 24;
    /** Index which should correspond to the average number of pins left on deck */
    public static final byte STAT_AVERAGE_PINS_LEFT_ON_DECK = 25;
    /** Index which should correspond to the bowler/league average */
    public static final byte STAT_AVERAGE = 26;
    /** Index which should correspond to the bowler/league high single */
    public static final byte STAT_HIGH_SINGLE = 27;
    /** Index which should correspond to the bowler/league high series */
    public static final byte STAT_HIGH_SERIES = 28;
    /** Index which should correspond to the bowler/league total pinfall */
    public static final byte STAT_TOTAL_PINFALL = 29;
    /** Index which should correspond to the bowler/league total number of games */
    public static final byte STAT_NUMBER_OF_GAMES = 30;
}
