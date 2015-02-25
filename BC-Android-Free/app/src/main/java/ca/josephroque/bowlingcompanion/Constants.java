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
    public static final String EXTRA_GAME_NUMBER = "EGN";

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

    //THEME COLORS
    //public static final String COLOR_BACKGROUND_PRIMARY = "#BDC3C7";
    //public static final String COLOR_BACKGROUND_SECONDARY = "#ECF0F1";
    //public static final String COLOR_ACTIONBAR = "#2ECC71";
}
