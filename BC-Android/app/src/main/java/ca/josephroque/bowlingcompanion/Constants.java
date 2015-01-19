package ca.josephroque.bowlingcompanion;

import android.content.Context;

import ca.josephroque.bowlingcompanion.database.BowlingContract;
import ca.josephroque.bowlingcompanion.database.BowlingContract.*;

/**
 * Created by josephroque on 15-01-15.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Constants
{

    public static final String MY_PREFS = "ca.josephroque.bowlingcompanion";

    public static final String PREFERENCES_NAME_BOWLER = "BowlerName";
    public static final String PREFERENCES_NAME_LEAGUE = "LeagueName";
    public static final String PREFERENCES_ID_BOWLER = BowlerEntry.TABLE_NAME + "." + BowlerEntry._ID;
    public static final String PREFERENCES_ID_LEAGUE = LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID;
    public static final String PREFERENCES_ID_SERIES = SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID;
    public static final String PREFERENCES_ID_GAME = GameEntry.TABLE_NAME + "." + GameEntry._ID;
    public static final String PREFERENCES_NUMBER_OF_GAMES = LeagueEntry.TABLE_NAME + "." + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES;

    public static final String FRAME_CLEAR = "xxxxx";
    public static final String BALL_STRIKE = "X";
    public static final String BALL_SPARE = "/";
    public static final String BALL_LEFT = "L";
    public static final String BALL_RIGHT = "R";
    public static final String BALL_ACE = "A";
    public static final String BALL_CHOP_OFF = "C/O";
    public static final String BALL_SPLIT = "Sp";
    public static final String BALL_HEAD_PIN = "Hp";
    public static final String BALL_HEAD_PIN_2 = "H2";
    public static final String BALL_EMPTY = "-";
}
