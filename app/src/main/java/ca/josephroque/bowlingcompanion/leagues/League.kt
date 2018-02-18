package ca.josephroque.bowlingcompanion.leagues

import android.content.Context
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.INameAverage
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single League, which has a set of series.
 */

data class League(private val leagueName: String,
             private val leagueAverage: Double,
             private val leagueId: Long,
             val isEvent: Boolean,
             val additionalPinfall: Int,
             val additionalGames: Int): INameAverage {

    override val name: String
        get() = leagueName

    override val average: Double
        get() = leagueAverage

    override val id: Long
        get() = leagueId

    companion object {
        val TAG = "League"
        val OPEN_LEAGUE_NAME = "Open"
        val PRACTICE_LEAGUE_NAME = "Practice"

        fun fetchAll(context: Context, bowler: Bowler): Deferred<List<League>> {
            return async (CommonPool) {
                val leagues: MutableList<League> = ArrayList()
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val rawLeagueEventQuery = ("SELECT "
                        + "league." + LeagueEntry._ID + " AS lid, "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_IS_EVENT + ", "
                        + LeagueEntry.COLUMN_BASE_AVERAGE + ", "
                        + LeagueEntry.COLUMN_BASE_GAMES + ", "
                        + LeagueEntry.COLUMN_NUMBER_OF_GAMES + ", "
                        + GameEntry.COLUMN_SCORE
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                        + " LEFT JOIN " + SeriesEntry.TABLE_NAME + " AS series"
                        + " ON league." + LeagueEntry._ID + "=series." + SeriesEntry.COLUMN_LEAGUE_ID
                        + " LEFT JOIN " + GameEntry.TABLE_NAME + " AS game"
                        + " ON series." + SeriesEntry._ID + "=game." + GameEntry.COLUMN_SERIES_ID
                        + " WHERE " + LeagueEntry.COLUMN_BOWLER_ID + "=?"
                        + " ORDER BY " + LeagueEntry.COLUMN_DATE_MODIFIED + " DESC")

                val cursor = database.rawQuery(rawLeagueEventQuery, arrayOf(bowler.id.toString()))
                var lastLeagueEventId: Long = -1
                var leagueNumberOfGames = 0
                var leagueTotal = 0
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val leagueEventId = cursor.getLong(cursor.getColumnIndex("lid"))
                        if (leagueEventId != lastLeagueEventId && lastLeagueEventId != -1L) {
                            cursor.moveToPrevious()
                            val name = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME))
                            val isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1
                            val additionalPinfall = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_PINFALL))
                            val additionalGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_GAMES))

                            val average = Score.getAdjustedAverage(
                                    leagueTotal,
                                    leagueNumberOfGames,
                                    additionalPinfall,
                                    additionalGames)

                            val leagueEvent = LeagueEvent(
                                    cursor.getLong(cursor.getColumnIndex("lid")),
                                    name,
                                    isEvent,
                                    average,
                                    baseAverage,
                                    baseGames,
                                    cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES)) as Byte)

                            leagues.add(leagueEvent)
                            leagueTotal = 0
                            leagueNumberOfGames = 0

                            if (!fragment.mPromptToUpdateIncorrectName && name.matches("^[LE][A-Z].*".toRegex())
                                    && promptToFixNames) {
                                preferences.edit().putBoolean(Constants.PREF_PROMPT_LEAGUE_EVENT_NAME_FIX, false).apply()
                                fragment.mPromptToUpdateIncorrectName = true
                            }

                            cursor.moveToNext()
                        }
                        val score = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE))
                        if (score > 0) {
                            leagueTotal += score.toInt()
                            leagueNumberOfGames++
                        }

                        lastLeagueEventId = leagueEventId
                        cursor.moveToNext()
                    }
                    cursor.moveToPrevious()
                    val isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) === 1
                    val baseAverage = cursor.getShort(cursor.getColumnIndex(LeagueEntry.COLUMN_BASE_AVERAGE))
                    val baseGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_BASE_GAMES))
                    val average = Score.getAdjustedAverage(leagueTotal, leagueNumberOfGames, baseAverage, baseGames)

                    val leagueEvent = LeagueEvent(cursor.getLong(cursor.getColumnIndex("lid")),
                            cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME)),
                            isEvent,
                            average,
                            baseAverage,
                            baseGames,
                            cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES)) as Byte)
                    listLeagueEvents.add(leagueEvent)
                }

                cursor.close()

                leagues
            }
        }
    }
}