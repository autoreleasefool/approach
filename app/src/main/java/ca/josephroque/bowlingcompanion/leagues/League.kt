package ca.josephroque.bowlingcompanion.leagues

import android.content.Context
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.INameAverage
import ca.josephroque.bowlingcompanion.database.Contract.*
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.scoring.Average
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

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
                  val additionalGames: Int,
                  val gamesPerSeries: Int): INameAverage {

    override val name: String
        get() = leagueName

    override val average: Double
        get() = leagueAverage

    override val id: Long
        get() = leagueId

    companion object {
        /** Logging identifier. */
        val TAG = "League"

        /** Name of the "Open" league. */
        @Deprecated("Replaced with [PRACTICE_LEAGUE_NAME")
        val OPEN_LEAGUE_NAME = "Open"

        /** Name of the "Practice" league. */
        val PRACTICE_LEAGUE_NAME = "Practice"

        /**
         * Get all of the leagues and events belonging to the [Bowler].
         *
         * @param context to get database instance
         * @param bowler the bowler whose leagues to retrieve
         * @return a [List] of [League] instances from the database.
         */
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
                var lastId: Long = -1
                var leagueNumberOfGames = 0
                var leagueTotal = 0
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val newId = cursor.getLong(cursor.getColumnIndex("lid"))
                        if (newId != lastId && lastId != -1L) {
                            cursor.moveToPrevious()

                            val id = cursor.getLong(cursor.getColumnIndex("lid"))
                            val name = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME))
                            val isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1
                            val additionalPinfall = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_PINFALL))
                            val additionalGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_GAMES))
                            val gamesPerSeries = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES))

                            val average = Average.getAdjustedAverage(
                                    leagueTotal,
                                    leagueNumberOfGames,
                                    additionalPinfall,
                                    additionalGames)

                            val league = League(
                                    name,
                                    average,
                                    id,
                                    isEvent,
                                    additionalPinfall,
                                    additionalGames,
                                    gamesPerSeries)

                            leagues.add(league)
                            leagueTotal = 0
                            leagueNumberOfGames = 0

                            cursor.moveToNext()
                        }
                        val score = cursor.getShort(cursor.getColumnIndex(GameEntry.COLUMN_SCORE))
                        if (score > 0) {
                            leagueTotal += score.toInt()
                            leagueNumberOfGames++
                        }

                        lastId = newId
                        cursor.moveToNext()
                    }
                    cursor.moveToPrevious()
                    val id = cursor.getLong(cursor.getColumnIndex("lid"))
                    val name = cursor.getString(cursor.getColumnIndex(LeagueEntry.COLUMN_LEAGUE_NAME))
                    val isEvent = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_IS_EVENT)) == 1
                    val additionalPinfall = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_PINFALL))
                    val additionalGames = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_ADDITIONAL_GAMES))
                    val average = Average.getAdjustedAverage(leagueTotal, leagueNumberOfGames, additionalPinfall, additionalGames)
                    val gamesPerSeries = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES))

                    val league = League(
                            name,
                            average,
                            id,
                            isEvent,
                            additionalPinfall,
                            additionalGames,
                            gamesPerSeries)

                    leagues.add(league)
                }

                cursor.close()

                leagues
            }
        }
    }
}