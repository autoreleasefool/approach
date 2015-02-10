package ca.josephroque.bowlingcompanion;

import ca.josephroque.bowlingcompanion.database.BowlingContract.*;

/**
 * Created by josephroque on 15-01-15.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Constants
{

    /** Maximum number of games a league can have per week */
    public static final int MAX_NUMBER_OF_GAMES = 5;
    /** Maximum number of games in a tournament */
    public static final int MAX_NUMBER_OF_TOURNAMENT_GAMES = 20;
    /** Number of frames in a single game */
    public static final int NUMBER_OF_FRAMES = 10;
    /** The last frame of a game */
    public static final int LAST_FRAME = 9;

    /** SharedPreferences identifier for the app*/
    public static final String MY_PREFS = "ca.josephroque.bowlingcompanionlite";

    /** Indicates which preferences store values used to create quick series */
    private static final String RECENT = ".RECENT";

    /** Preference containing name of current bowler */
    public static final String PREFERENCES_NAME_BOWLER = "BowlerName";
    /** Preference containing name of current league */
    public static final String PREFERENCES_NAME_LEAGUE = "LeagueName";
    /** Preference containing id of current bowler */
    public static final String PREFERENCES_ID_BOWLER = BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID;
    /** Preference containing id of current league */
    public static final String PREFERENCES_ID_LEAGUE = LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID;
    /** Preference containing id of current series */
    public static final String PREFERENCES_ID_SERIES = SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID;
    /** Preference containing id of current game */
    public static final String PREFERENCES_ID_GAME = GameEntry.TABLE_NAME + "." + GameEntry._ID;
    /** Preference containing number of games of current league */
    public static final String PREFERENCES_NUMBER_OF_GAMES = LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES;
    /** Preference containing current game number in series */
    public static final String PREFERENCES_GAME_NUMBER = GameEntry.TABLE_NAME + "." + GameEntry.COLUMN_NAME_GAME_NUMBER;
    /** Preference to save the most recent bowler ID */
    public static final String PREFERENCES_ID_BOWLER_RECENT = BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID + RECENT;
    /** Preference to save the most recent league ID */
    public static final String PREFERENCES_ID_LEAGUE_RECENT= LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID + RECENT;
    /** Preference to save whether games are in tournament mode or not */
    public static final String PREFERENCES_TOURNAMENT_MODE = LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_IS_TOURNAMENT;
    /** Preference to save whether tutorial has been shown before or not */
    public static final String PREFERENCES_HAS_SHOWN_TUTORIAL_MAIN = "TUTORIAL_MAIN";
    /** Preference to save whether tutorial has been shown before or not */
    public static final String PREFERENCES_HAS_SHOWN_TUTORIAL_LEAGUE = "TUTORIAL_LEAGUE";
    /** Preference to save whether tutorial has been shown before or not */
    public static final String PREFERENCES_HAS_SHOWN_TUTORIAL_SERIES = "TUTORIAL_SERIES";
    /** Preference to save whether tutorial has been shown before or not */
    public static final String PREFERENCES_HAS_SHOWN_TUTORIAL_GAME = "TUTORIAL_GAME";

    //TODO add documentation
    public static final String BUTTON_POSITIVE = "Okay";
    public static final String BUTTON_NEGATIVE = "Cancel";

    /** Name of default open league, available to all bowlers */
    public static final String OPEN_LEAGUE = "Open";

    /** Represents a frame with all pins cleared */
    public static final boolean[] FRAME_CLEAR = {true, true, true, true, true};
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
