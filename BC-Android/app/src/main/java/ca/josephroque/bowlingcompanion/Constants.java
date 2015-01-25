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
    /** Number of frames in a single game */
    public static final int NUMBER_OF_FRAMES = 10;

    /** SharedPreferences identifier for the app*/
    public static final String MY_PREFS = "ca.josephroque.bowlingcompanion";

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
}
