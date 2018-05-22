package ca.josephroque.bowlingcompanion.leagues

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.interfaces.*
import ca.josephroque.bowlingcompanion.database.Contract.*
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.scoring.Average
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single League, which has a set of series.
 */
data class League(
        val bowler: Bowler,
        override val id: Long,
        override val name: String,
        override val average: Double,
        val isEvent: Boolean,
        val gamesPerSeries: Int,
        val additionalPinfall: Int,
        val additionalGames: Int,
        val gameHighlight: Int,
        val seriesHighlight: Int
) : INameAverage, KParcelable {

    /** Private field to indicate if the item is deleted. */
    private var _isDeleted: Boolean = false
    /** @Override */
    override val isDeleted: Boolean
        get() = _isDeleted

    /**
     * Construct [League] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            bowler = p.readParcelable<Bowler>(Bowler::class.java.classLoader),
            id = p.readLong(),
            name = p.readString(),
            average = p.readDouble(),
            isEvent = p.readBoolean(),
            gamesPerSeries = p.readInt(),
            additionalPinfall = p.readInt(),
            additionalGames = p.readInt(),
            gameHighlight = p.readInt(),
            seriesHighlight = p.readInt()
    )

    /**
     * Construct [League] from a [League]
     */
    constructor(league: League): this(
            bowler = league.bowler,
            id = league.id,
            name = league.name,
            average = league.average,
            isEvent = league.isEvent,
            gamesPerSeries = league.gamesPerSeries,
            additionalPinfall = league.additionalPinfall,
            additionalGames = league.additionalGames,
            gameHighlight = league.gameHighlight,
            seriesHighlight = league.seriesHighlight
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(bowler, 0)
        writeLong(id)
        writeString(name)
        writeDouble(average)
        writeBoolean(isEvent)
        writeInt(gamesPerSeries)
        writeInt(additionalPinfall)
        writeInt(additionalGames)
        writeInt(gameHighlight)
        writeInt(seriesHighlight)
    }

    /** @Override */
    override fun markForDeletion(): League {
        val newInstance = League(this)
        newInstance._isDeleted = true
        return newInstance
    }

    /** @Override */
    override fun cleanDeletion(): League {
        val newInstance = League(this)
        newInstance._isDeleted = false
        return this
    }

    /**
     * Get all [Series] instances belonging to this [League].
     *
     * @param context to get database instance
     * @return a [MutableList] of [Series] items
     */
    fun fetchSeries(context: Context): Deferred<MutableList<Series>> {
        return Series.fetchAll(context, this)
    }

    /** @Override */
    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            database.beginTransaction()
            try {
                database.delete(LeagueEntry.TABLE_NAME, LeagueEntry._ID + "=?", arrayOf(id.toString()))
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                // Does nothing
                // If there's an error deleting this league, then they just remain in the
                // user's data and no harm is done.
            } finally {
                database.endTransaction()
            }
        }
    }

    companion object {
        /** Logging identifier. */
        private const val TAG = "League"

        /** Valid regex for a name. */
        private val REGEX_NAME = Bowler.REGEX_NAME

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::League)

        /** Name of the "Open" league. */
        @Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
        const val OPEN_LEAGUE_NAME = "Open"

        /** Name of the "Practice" league. */
        const val PRACTICE_LEAGUE_NAME = "Practice"

        /** Maximum number of games in a league or event. */
        const val MAX_NUMBER_OF_GAMES = 20

        /** Minimum number of games in a league or event. */
        const val MIN_NUMBER_OF_GAMES = 1

        /** Default number of games in a league or event. */
        const val DEFAULT_NUMBER_OF_GAMES = 1

        /** Default minimum game score to highlight when a minimum is not defined. */
        const val DEFAULT_GAME_HIGHLIGHT = 300

        /** Default minimum series total to highlight when a minimum is not defined. */
        val DEFAULT_SERIES_HIGHLIGHT = intArrayOf(250, 500, 750, 1000, 1250, 1500, 1750, 2000, 2250, 2500, 2750, 3000, 3250, 3500, 3750, 4000, 4250, 4500, 4750, 5000)

        /**
         * Order by which to sort bowlers.
         */
        enum class Sort {
            Alphabetically,
            LastModified;

            companion object {
                private val map = Sort.values().associateBy(Sort::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        /**
         * Check if a name is a valid [League] name.
         *
         * @param name name to check
         * @return true if the name is valid, false otherwise
         */
        private fun isLeagueNameValid(name: String): Boolean = REGEX_NAME.matches(name)

        /**
         * Check if a name is unique in the League database.
         *
         * @param context to get database instance
         * @param name name to check
         * @param id id of the existing league, so if the name is unchanged it can be saved
         * @return true if the name is not already in the database, false otherwise
         */
        private fun isLeagueNameUnique(context: Context, name: String, id: Long = -1): Deferred<Boolean> {
            return async(CommonPool) {
                val database = DatabaseHelper.getInstance(context).readableDatabase

                var cursor: Cursor? = null
                try {
                    cursor = database.query(
                            LeagueEntry.TABLE_NAME,
                            arrayOf(LeagueEntry.COLUMN_LEAGUE_NAME),
                            "${LeagueEntry.COLUMN_LEAGUE_NAME}=? AND ${LeagueEntry._ID}!=?",
                            arrayOf(name, id.toString()),
                            "",
                            "",
                            ""
                    )

                    if ((cursor?.count ?: 0) > 0) {
                        return@async false
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed) {
                        cursor.close()
                    }
                }

                true
            }
        }

        /**
         * Check if conditions to save a league are met before saving.
         *
         * @param context to get resources for error messages if there are any
         * @param id -1 to create a new league, or the league id to update
         * @param name name of the league
         * @param isEvent true to create an event, false to create a league
         * @param gamesPerSeries number of games per series in the league
         * @param additionalPinfall additional total pinfall not recorded in the app for the league
         * @param additionalGames additional games not recorded in the app for the league
         * @param gameHighlight minimum score to highlight when viewing games
         * @param seriesHighlight minimum series total to highlight when viewing series
         * @return an error to show if preconditions fail
         */
        private fun validateSavePreconditions(
                context: Context,
                id: Long,
                name: String,
                isEvent: Boolean,
                gamesPerSeries: Int,
                additionalPinfall: Int,
                additionalGames: Int,
                gameHighlight: Int,
                seriesHighlight: Int
        ): Deferred<BCError?> {
            return async(CommonPool) {
                val errorTitle = if (isEvent) R.string.issue_saving_event else R.string.issue_saving_league
                val errorMessage: Int?
                if (!isLeagueNameValid(name)) {
                    errorMessage = if (isEvent) R.string.error_event_name_invalid else R.string.error_league_name_invalid
                } else if (!isLeagueNameUnique(context, name, id).await()) {
                    errorMessage = if (isEvent) R.string.error_event_name_in_use else R.string.error_league_name_in_use
                } else if (name == PRACTICE_LEAGUE_NAME) {
                    errorMessage = R.string.error_cannot_edit_practice_league
                } else if (
                        (isEvent && (additionalPinfall != 0 || additionalGames != 0)) ||
                        (additionalPinfall < 0 || additionalGames < 0) ||
                        (additionalPinfall > 0 && additionalGames == 0) ||
                        (additionalPinfall.toDouble() / additionalGames.toDouble() > 450)
                ) {
                    errorMessage = R.string.error_league_additional_info_unbalanced
                } else if (
                        gameHighlight < 0 || gameHighlight > Game.MAX_SCORE ||
                        seriesHighlight < 0 || seriesHighlight > Game.MAX_SCORE * gamesPerSeries
                ) {
                    errorMessage = R.string.error_league_highlight_invalid
                } else if (gamesPerSeries < MIN_NUMBER_OF_GAMES || gamesPerSeries > MAX_NUMBER_OF_GAMES) {
                    errorMessage = R.string.error_league_number_of_games_invalid
                } else {
                    errorMessage = null
                }

                return@async if (errorMessage != null) {
                    BCError(errorTitle, errorMessage, BCError.Severity.Warning)
                } else {
                    null
                }
            }
        }

        /**
         * Save the league to the database.
         *
         * @param context to get database instance
         * @param bowler the bowler that will own the league
         * @param id -1 to create a new league, or the league id to update
         * @param name name of the league
         * @param isEvent true to create an event, false to create a league
         * @param gamesPerSeries number of games per series in the league
         * @param additionalPinfall additional total pinfall not recorded in the app for the league
         * @param additionalGames additional games not recorded in the app for the league
         * @param gameHighlight minimum score to highlight when viewing games
         * @param seriesHighlight minimum series total to highlight when viewing series
         * @param average average of the league, defaults to 0.0
         * @return the saved [League] or [BCError] if an error occurred
         */
        fun save(
                context: Context,
                bowler: Bowler,
                id: Long,
                name: String,
                isEvent: Boolean,
                gamesPerSeries: Int,
                additionalPinfall: Int,
                additionalGames: Int,
                gameHighlight: Int,
                seriesHighlight: Int,
                average: Double = 0.0
        ): Deferred<Pair<League?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, bowler, name, isEvent, gamesPerSeries, additionalPinfall, additionalGames, gameHighlight, seriesHighlight)
            } else {
                update(context, bowler, id, name, average, isEvent, gamesPerSeries, additionalPinfall, additionalGames, gameHighlight, seriesHighlight)
            }
        }

        /**
         * Create a new [LeagueEntry] in the database.
         *
         * @param context to get database instance
         * @param bowler id the bowler that will own the league
         * @param name name of the league
         * @param isEvent true to create an event, false to create a league
         * @param gamesPerSeries number of games per series in the league
         * @param additionalPinfall additional total pinfall not recorded in the app for the league
         * @param additionalGames additional games not recorded in the app for the league
         * @param gameHighlight minimum score to highlight when viewing games
         * @param seriesHighlight minimum series total to highlight when viewing series
         * @return the saved [League] or [BCError] if an error occurred
         */
        private fun createNewAndSave(
                context: Context,
                bowler: Bowler,
                name: String,
                isEvent: Boolean,
                gamesPerSeries: Int,
                additionalPinfall: Int,
                additionalGames: Int,
                gameHighlight: Int,
                seriesHighlight: Int
        ): Deferred<Pair<League?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(
                        context = context,
                        id = -1,
                        name = name,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight

                ).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = DatabaseHelper.getInstance(context).writableDatabase
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                val values = ContentValues().apply {
                    put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                    put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                    put(LeagueEntry.COLUMN_BOWLER_ID, bowler.id)
                    put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                    put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
                    put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, gamesPerSeries)
                    put(LeagueEntry.COLUMN_IS_EVENT, isEvent)
                    put(LeagueEntry.COLUMN_GAME_HIGHLIGHT, gameHighlight)
                    put(LeagueEntry.COLUMN_SERIES_HIGHLIGHT, seriesHighlight)
                }

                val league: League
                database.beginTransaction()
                try {
                    val leagueId = database.insert(LeagueEntry.TABLE_NAME, null, values)
                    league = League(
                            bowler = bowler,
                            id = -1,
                            name = name,
                            average = 0.0,
                            isEvent = isEvent,
                            gamesPerSeries = gamesPerSeries,
                            additionalPinfall = additionalPinfall,
                            additionalGames = additionalGames,
                            gameHighlight = gameHighlight,
                            seriesHighlight = seriesHighlight
                    )

                    if (leagueId != -1L && isEvent) {


                        /*
                         * If the new entry is an event, its series is also created at this time
                         * since there is only a single series to an event
                         */

                        val series = Series(
                                league,
                                -1,
                                Date(),
                                gamesPerSeries,
                                IntArray(gamesPerSeries).toList(),
                                ByteArray(gamesPerSeries).toList()
                        )

                        val seriesError = series.save(context).await()
                        if (seriesError != null || series.id == -1L) {
                            throw IllegalStateException("Series was not saved.")
                        }
                    }

                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not create a new league")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_league, R.string.error_league_not_saved)
                    )
                } finally {
                    database.endTransaction()
                }

                Pair(league, null)
            }
        }

        /**
         * Update the league entry in the database.
         *
         * @param context to get database instance
         * @param bowler the bowler that will own the league
         * @param id the league id to update
         * @param name name of the league
         * @param average average of the league
         * @param isEvent true to create an event, false to create a league
         * @param gamesPerSeries number of games per series in the league
         * @param additionalPinfall additional total pinfall not recorded in the app for the league
         * @param additionalGames additional games not recorded in the app for the league
         * @param gameHighlight minimum score to highlight when viewing games
         * @param seriesHighlight minimum series total to highlight when viewing series
         * @return the saved [League] or [BCError] if an error occurred
         */
        fun update(
                context: Context,
                bowler: Bowler,
                id: Long,
                name: String,
                average: Double,
                isEvent: Boolean,
                gamesPerSeries: Int,
                additionalPinfall: Int,
                additionalGames: Int,
                gameHighlight: Int,
                seriesHighlight: Int
        ): Deferred<Pair<League?, BCError?>> {
            return async(CommonPool) {
                val error = validateSavePreconditions(
                        context = context,
                        id = id,
                        name = name,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight

                ).await()
                if (error != null) {
                    return@async Pair(null, error)
                }

                val database = DatabaseHelper.getInstance(context).writableDatabase
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
                val currentDate = dateFormat.format(Date())

                val values = ContentValues().apply {
                    put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                    put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                    put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
                    put(LeagueEntry.COLUMN_GAME_HIGHLIGHT, gameHighlight)
                    put(LeagueEntry.COLUMN_SERIES_HIGHLIGHT, seriesHighlight)
                    put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                }

                database.beginTransaction()
                try {
                    database.update(LeagueEntry.TABLE_NAME, values, LeagueEntry._ID + "=?", arrayOf(id.toString()))
                    database.setTransactionSuccessful()
                } catch (ex: Exception) {
                    Log.e(TAG, "Error updating league/event details ($name, $additionalPinfall, $additionalGames)", ex)
                } finally {
                    database.endTransaction()
                }

                Pair(League(
                        bowler = bowler,
                        id = id,
                        name = name,
                        average = average,
                        isEvent = isEvent,
                        gamesPerSeries = gamesPerSeries,
                        additionalPinfall = additionalPinfall,
                        additionalGames = additionalGames,
                        gameHighlight = gameHighlight,
                        seriesHighlight = seriesHighlight
                ), null)
            }
        }

        /**
         * Get all of the leagues and events belonging to the [Bowler].
         *
         * @param context to get database instance
         * @param bowler the bowler whose leagues to retrieve
         * @param includeLeagues true to include [League] instances which are leagues
         * @param includeEvents true to include [League] instances which are events
         * @return a [MutableList] of [League] instances from the database.
         */
        fun fetchAll(
                context: Context,
                bowler: Bowler,
                includeLeagues: Boolean = true,
                includeEvents: Boolean = false
        ): Deferred<MutableList<League>> {
            return async (CommonPool) {
                val leagues: MutableList<League> = ArrayList()
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val rawLeagueEventQuery = ("SELECT "
                        + "league." + LeagueEntry._ID + " AS lid, "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_IS_EVENT + ", "
                        + LeagueEntry.COLUMN_ADDITIONAL_PINFALL + ", "
                        + LeagueEntry.COLUMN_ADDITIONAL_GAMES + ", "
                        + LeagueEntry.COLUMN_GAME_HIGHLIGHT + ", "
                        + LeagueEntry.COLUMN_SERIES_HIGHLIGHT + ", "
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
                            val gameHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_GAME_HIGHLIGHT))
                            val seriesHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_SERIES_HIGHLIGHT))
                            val gamesPerSeries = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES))
                            val average = Average.getAdjustedAverage(leagueTotal, leagueNumberOfGames, additionalPinfall, additionalGames)

                            val league = League(
                                    bowler,
                                    id,
                                    name,
                                    average,
                                    isEvent,
                                    gamesPerSeries,
                                    additionalPinfall,
                                    additionalGames,
                                    gameHighlight,
                                    seriesHighlight)

                            if ((includeEvents && isEvent) || (includeLeagues && !isEvent)) {
                                leagues.add(league)
                            }

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
                    val gameHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_GAME_HIGHLIGHT))
                    val seriesHighlight = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_SERIES_HIGHLIGHT))
                    val average = Average.getAdjustedAverage(leagueTotal, leagueNumberOfGames, additionalPinfall, additionalGames)
                    val gamesPerSeries = cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NUMBER_OF_GAMES))

                    val league = League(
                            bowler,
                            id,
                            name,
                            average,
                            isEvent,
                            gamesPerSeries,
                            additionalPinfall,
                            additionalGames,
                            gameHighlight,
                            seriesHighlight)

                    if ((includeEvents && isEvent) || (includeLeagues && !isEvent)) {
                        leagues.add(league)
                    }
                }

                cursor.close()

                leagues
            }
        }
    }
}
