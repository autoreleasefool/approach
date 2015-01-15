package ca.josephroque.bowlingcompanion;

import android.content.Context;

import ca.josephroque.bowlingcompanion.database.BowlingContract;

/**
 * Created by josephroque on 15-01-15.
 * <p/>
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class Preferences
{

    public static final String MY_PREFS = "ca.josephroque.bowlingcompanion";
    public static final String NAME_BOWLER = "BowlerName";
    public static final String NAME_LEAGUE = "LeagueName";

    public static void setPreferences(Context context, String bowlerName, String leagueName, long bowlerID, long leagueID, long seriesID, long gameID, int numberOfGames)
    {
        context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit()
                .putString(NAME_BOWLER, bowlerName)
                .putString(NAME_LEAGUE, leagueName)
                .putLong(BowlingContract.BowlerEntry.TABLE_NAME + "." + BowlingContract.BowlerEntry._ID, bowlerID)
                .putLong(BowlingContract.LeagueEntry.TABLE_NAME + "." + BowlingContract.LeagueEntry._ID, leagueID)
                .putLong(BowlingContract.SeriesEntry.TABLE_NAME + "." + BowlingContract.SeriesEntry._ID, seriesID)
                .putLong(BowlingContract.GameEntry.TABLE_NAME + "." + BowlingContract.GameEntry._ID, gameID)
                .putInt(BowlingContract.LeagueEntry.TABLE_NAME + "." + BowlingContract.LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, numberOfGames)
                .apply();
    }

    public static void clearPreferences(Context context)
    {
        context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit()
                .clear()
                .apply();
    }
}
