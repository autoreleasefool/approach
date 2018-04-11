package ca.josephroque.bowlingcompanion.leagues

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.*
import ca.josephroque.bowlingcompanion.database.Contract.*
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.scoring.Average
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.text.SimpleDateFormat
import java.util.*
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single League, which has a set of series.
 */
data class League(
        val bowler: Bowler,
        override var id: Long,
        override var name: String,
        override var average: Double,
        val isEvent: Boolean,
        val gamesPerSeries: Int,
        var additionalPinfall: Int,
        var additionalGames: Int
): INameAverage, KParcelable {

    override var isDeleted: Boolean = false

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
            additionalGames = p.readInt()
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
    }

    /**
     * Save the current bowler to the database.
     *
     * @param context to get database instance
     * @return [BCError] only if an error occurred
     */
    fun save(context: Context): Deferred<BCError?> {
        return if (id < 0) {
            createNewAndSave(context)
        } else {
            update(context)
        }
    }

    /**
     * Create a new [LeagueEntry] in the database.
     *
     * @param context to get database instance
     * @return [BCError] only if an error occurred
     */
    private fun createNewAndSave(context: Context): Deferred<BCError?> {
        return async(CommonPool) {
            val error = validateSavePreconditions(context).await()
            if (error != null) {
                return@async error
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            val currentDate = dateFormat.format(Date())

            var values = ContentValues().apply {
                put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                put(LeagueEntry.COLUMN_DATE_MODIFIED, currentDate)
                put(LeagueEntry.COLUMN_BOWLER_ID, bowler.id)
                put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
                put(LeagueEntry.COLUMN_NUMBER_OF_GAMES, gamesPerSeries)
                put(LeagueEntry.COLUMN_IS_EVENT, isEvent)
            }

            database.beginTransaction()
            try {
                val leagueId = database.insert(LeagueEntry.TABLE_NAME, null, values)

                if (leagueId != -1L && isEvent) {
                    /*
                     * If the new entry is an event, its series is also created at this time
                     * since there is only a single series to an event
                     */

                    values = ContentValues().apply {
                        put(SeriesEntry.COLUMN_SERIES_DATE, currentDate)
                        put(SeriesEntry.COLUMN_LEAGUE_ID, leagueId)
                    }
                    val seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values)

                    if (seriesId != -1L) {
                        for (i in 0 until gamesPerSeries) {
                            values = ContentValues()
                            values.put(GameEntry.COLUMN_GAME_NUMBER, i + 1)
                            values.put(GameEntry.COLUMN_SCORE, 0)
                            values.put(GameEntry.COLUMN_SERIES_ID, seriesId)
                            val gameId = database.insert(GameEntry.TABLE_NAME, null, values)

                            if (gameId != -1L) {
                                for (j in 0 until Game.NUMBER_OF_FRAMES) {
                                    values = ContentValues()
                                    values.put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1)
                                    values.put(FrameEntry.COLUMN_GAME_ID, gameId)
                                    database.insert(FrameEntry.TABLE_NAME, null, values)
                                }
                            } else {
                                throw IllegalStateException("Game was not saved, ID is -1")
                            }
                        }
                    } else {
                        throw IllegalStateException("Series was not saved, ID is -1")
                    }
                }

                this@League.id = leagueId
                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Could not create a new league")
                return@async BCError(
                        context.resources.getString(R.string.error_saving_league),
                        context.resources.getString(R.string.error_league_not_saved),
                        BCError.Severity.Error
                )
            } finally {
                database.endTransaction()
            }

            null
        }
    }

    /**
     * Update the [LeagueEntry] in the database.
     *
     * @param context context to get database instance
     * @return [BCError] only if an error occurred
     */
    private fun update(context: Context): Deferred<BCError?> {
        return async(CommonPool) {
            val error = validateSavePreconditions(context).await()
            if (error != null) {
                return@async error
            }

            val database = DatabaseHelper.getInstance(context).writableDatabase
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            val currentDate = dateFormat.format(Date())

            val values = ContentValues().apply {
                put(LeagueEntry.COLUMN_LEAGUE_NAME, name)
                put(LeagueEntry.COLUMN_ADDITIONAL_PINFALL, additionalPinfall)
                put(LeagueEntry.COLUMN_ADDITIONAL_GAMES, additionalGames)
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

            null
        }
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

    private fun validateSavePreconditions(context: Context): Deferred<BCError?> {
        return async(CommonPool) {
            val errorTitle = if (isEvent) R.string.error_saving_event else R.string.error_saving_league
            if (!isLeagueNameValid(name)) {
                val errorMessage = if (isEvent) R.string.error_event_name_invalid else R.string.error_league_name_invalid
                return@async BCError(
                        context.resources.getString(errorTitle),
                        context.resources.getString(errorMessage),
                        BCError.Severity.Error
                )
            } else if (!isLeagueNameUnique(context, name, id).await()) {
                val errorMessage = if (isEvent) R.string.error_event_name_in_use else R.string.error_league_name_in_use
                return@async BCError(
                        context.resources.getString(errorTitle),
                        context.resources.getString(errorMessage),
                        BCError.Severity.Error
                )
            } else if (
                    (isEvent && (additionalPinfall != 0 || additionalGames != 0)) ||
                    (additionalPinfall < 0 || additionalGames < 0) ||
                    (additionalPinfall > 0 && additionalGames == 0) ||
                    (additionalPinfall.toDouble() / additionalGames.toDouble() > 450)
            ) {
                val errorMessage = R.string.error_league_additional_info_unbalanced
                return@async BCError(
                        context.resources.getString(errorTitle),
                        context.resources.getString(errorMessage),
                        BCError.Severity.Error
                )
            }

            return@async null
        }
    }

    companion object {
        /** Logging identifier. */
        private const val TAG = "League"

        /** Valid regex for a name. */
        private val REGEX_NAME = Bowler.REGEX_NAME

        /** Creator, required by [Parcelable]. */
        @JvmField val CREATOR = parcelableCreator(::League)

        /** Name of the "Open" league. */
        @Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
        const val OPEN_LEAGUE_NAME = "Open"

        /** Name of the "Practice" league. */
        const val PRACTICE_LEAGUE_NAME = "Practice"

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
         * Get all of the leagues and events belonging to the [Bowler].
         *
         * @param context to get database instance
         * @param bowler the bowler whose leagues to retrieve
         * @param includeLeagues true to include [League] instances which are leagues
         * @param includeEvents true to include [League] instances which are events
         * @return a [MutableList] of [League] instances from the database.
         */
        fun fetchAll(context: Context, bowler: Bowler, includeLeagues: Boolean = true, includeEvents: Boolean = false): Deferred<MutableList<League>> {
            return async (CommonPool) {
                val leagues: MutableList<League> = ArrayList()
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val rawLeagueEventQuery = ("SELECT "
                        + "league." + LeagueEntry._ID + " AS lid, "
                        + LeagueEntry.COLUMN_LEAGUE_NAME + ", "
                        + LeagueEntry.COLUMN_IS_EVENT + ", "
                        + LeagueEntry.COLUMN_ADDITIONAL_PINFALL + ", "
                        + LeagueEntry.COLUMN_ADDITIONAL_GAMES + ", "
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
                            val average = Average.getAdjustedAverage(leagueTotal, leagueNumberOfGames, additionalPinfall, additionalGames)

                            val league = League(
                                    bowler,
                                    id,
                                    name,
                                    average,
                                    isEvent,
                                    gamesPerSeries,
                                    additionalPinfall,
                                    additionalGames)

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
                            additionalGames)

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