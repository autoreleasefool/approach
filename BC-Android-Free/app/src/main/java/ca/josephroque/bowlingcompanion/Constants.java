package ca.josephroque.bowlingcompanion;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Constants
{

    //GAME VALUES
    public static final byte MAX_NUMBER_LEAGUE_GAMES = 5;
    public static final byte MAX_NUMBER_EVENT_GAMES = 20;
    public static final byte NUMBER_OF_FRAMES = 10;
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

    public static final boolean[] FRAME_PINS_DOWN = {true, true, true, true, true};

    //INTENT EXTRAS
    public static final String EXTRA_EVENT_MODE = "EM";
    public static final String EXTRA_NUMBER_OF_GAMES = "ENOG";
    public static final String EXTRA_ARRAY_GAME_IDS = "EAGI";
    public static final String EXTRA_ARRAY_FRAME_IDS = "EAFI";

    //PREFERENCES
    public static final String PREFERENCES = "ca.josephroque.bowlingcompanionfree";
    public static final String PREFERENCE_ID_RECENT_BOWLER = "IdRB";
    public static final String PREFERENCE_ID_RECENT_LEAGUE = "IdRL";
    public static final String PREFERENCE_ID_QUICK_BOWLER = "IdQB";
    public static final String PREFERENCE_ID_QUICK_LEAGUE = "IdQL";
    public static final String PREFERENCE_ID_BOWLER = "IdB";
    public static final String PREFERENCE_ID_LEAGUE = "IdL";
    public static final String PREFERENCE_ID_SERIES = "IdS";
    public static final String PREFERENCE_ID_GAME = "IdG";
    public static final String PREFERENCE_NAME_BOWLER = "NB";
    public static final String PREFERENCE_NAME_LEAGUE = "NL";

    //DIALOG BUTTON TEXT
    public static final String DIALOG_OKAY = "Okay";
    public static final String DIALOG_ADD = "Add";
    public static final String DIALOG_CANCEL = "Cancel";
    public static final String DIALOG_DELETE = "Delete";

    //NAME DEFAULTS
    public static final int NAME_MAX_LENGTH = 30;
    public static final String NAME_LEAGUE_OPEN = "Open";

    //THEME COLORS
    //public static final String COLOR_BACKGROUND_PRIMARY = "#BDC3C7";
    //public static final String COLOR_BACKGROUND_SECONDARY = "#ECF0F1";
    //public static final String COLOR_ACTIONBAR = "#2ECC71";
}
