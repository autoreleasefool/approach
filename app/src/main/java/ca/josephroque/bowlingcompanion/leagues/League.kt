package ca.josephroque.bowlingcompanion.leagues

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.*
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
                  val gamesPerSeries: Int): INameAverage, KParcelable {

    override val name: String
        get() = leagueName

    override val average: Double
        get() = leagueAverage

    override val id: Long
        get() = leagueId

    /**
     * Construct [League] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            leagueName = p.readString(),
            leagueAverage = p.readDouble(),
            leagueId = p.readLong(),
            isEvent = p.readBoolean(),
            gamesPerSeries = p.readInt()
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeDouble(average)
        writeLong(id)
        writeBoolean(isEvent)
        writeInt(gamesPerSeries)
    }

    companion object {
        /** Logging identifier. */
        private val TAG = "League"

        /** Creator, required by [Parcelable]. */
        @JvmField val CREATOR = parcelableCreator(::League)

        /** Name of the "Open" league. */
        @Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
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
                            gamesPerSeries)

                    leagues.add(league)
                }

                cursor.close()

                leagues
            }
        }
    }
}