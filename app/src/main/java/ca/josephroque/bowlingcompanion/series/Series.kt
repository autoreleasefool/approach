package ca.josephroque.bowlingcompanion.series

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.IDeletable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readDate
import ca.josephroque.bowlingcompanion.common.interfaces.writeDate
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.DateUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.Date

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A series of games in a [League].
 */
class Series(
    val league: League,
    override val id: Long,
    val date: Date,
    val numberOfGames: Int,
    val scores: List<Int>,
    val matchPlay: List<Byte>
) : IIdentifiable, IDeletable, KParcelable {

    /** Private field to indicate if the item is deleted. */
    private var _isDeleted: Boolean = false
    /** @Override */
    override val isDeleted: Boolean
        get() = _isDeleted

    /** Beautifies the date to be displayed. */
    val prettyDate: String
        get() = DateUtils.dateToPretty(date)

    /**
     * Construct [Series] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            league = p.readParcelable<League>(League::class.java.classLoader),
            id = p.readLong(),
            date = p.readDate()!!,
            numberOfGames = p.readInt(),
            scores = arrayListOf<Int>().apply {
                val size = p.readInt()
                val scoresArray = IntArray(size)
                p.readIntArray(scoresArray)
                this.addAll(scoresArray.toList())
            },
            matchPlay = arrayListOf<Byte>().apply {
                val size = p.readInt()
                val matchesArray = kotlin.ByteArray(size)
                p.readByteArray(matchesArray)
                this.addAll(matchesArray.toList())
            }
    )

    /**
     * Construct [Series] from a [Series].
     */
    constructor(series: Series): this(
            league = series.league,
            id = series.id,
            date = series.date,
            numberOfGames = series.numberOfGames,
            scores = series.scores,
            matchPlay = series.matchPlay
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(league, 0)
        writeLong(id)
        writeDate(date)
        writeInt(numberOfGames)
        writeInt(scores.size)
        writeIntArray(scores.toIntArray())
        writeInt(matchPlay.size)
        writeByteArray(matchPlay.toByteArray())
    }

    /** @Override */
    override fun markForDeletion(): Series {
        val newInstance = Series(this)
        newInstance._isDeleted = true
        return newInstance
    }

    /** @Override */
    override fun cleanDeletion(): Series {
        val newInstance = Series(this)
        newInstance._isDeleted = false
        return newInstance
    }

    /** @Override */
    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            val database = Saviour.instance.getWritableDatabase(context).await()
            database.beginTransaction()
            try {
                database.delete(SeriesEntry.TABLE_NAME,
                    "${SeriesEntry._ID}=?",
                    arrayOf(id.toString()))
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                // Does nothing
                // If there's an error deleting this series, then it just remains in the
                // user's data and no harm is done.
            } finally {
                database.endTransaction()
            }
        }
    }

    /**
     * Load the list of games for this series.
     *
     * @param context to get database instance
     * @return the list of games for the series
     */
    fun fetchGames(context: Context): Deferred<MutableList<Game>> {
        return Game.fetchSeriesGames(context, this)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Series"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Series)

        /** Argument identifier for showing condensed or expanded view of series. */
        const val PREFERRED_VIEW = "series_preferred_view"

        /**
         * View to display series as.
         */
        enum class View {
            Expanded, Condensed;

            companion object {
                private val map = View.values().associateBy(View::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        /**
         * Save this series to the database.
         *
         * @param context to get database instance
         * @param id -1 to create a new series, or id of the series to update
         * @param league league that owns the series
         * @param date date of the series
         * @param numberOfGames number of games in the series
         * @param scores scores of the games in the series
         * @param matchPlay match play results of the games in the series
         * @param inTransaction if true, the method should not handle the database transaction as one
         *                      has already been created in another context
         * @return [BCError] only if an error occurred
         */
        fun save(
            context: Context,
            league: League,
            id: Long,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            inTransaction: Boolean = false
        ): Deferred<Pair<Series?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, league, date, numberOfGames, scores, matchPlay, inTransaction)
            } else {
                update(context, id, league, date, numberOfGames, scores, matchPlay, inTransaction)
            }
        }

        /**
         * Create a new [SeriesEntry] in the database.
         *
         * @param context to get database instance
         * @param league league that owns the series
         * @param date date of the series
         * @param numberOfGames number of games in the series
         * @param scores scores of the games in the series
         * @param matchPlay match play results of the games in the series
         * @param inTransaction if true, the method should not handle the database transaction as one
         *                      has already been created in another context
         * @return [BCError] only if an error occurred
         */
        private fun createNewAndSave(
            context: Context,
            league: League,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            inTransaction: Boolean = false
        ): Deferred<Pair<Series?, BCError?>> {
            return async(CommonPool) {
                val database = Saviour.instance.getWritableDatabase(context).await()
                var values = ContentValues().apply {
                    put(SeriesEntry.COLUMN_SERIES_DATE, DateUtils.dateToSeriesDate(date))
                    put(SeriesEntry.COLUMN_LEAGUE_ID, league.id)
                }

                val seriesId: Long
                if (!inTransaction) {
                    database.beginTransaction()
                }
                try {
                    seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values)

                    if (seriesId != -1L) {
                        for (i in 0 until numberOfGames) {
                            values = ContentValues().apply {
                                put(GameEntry.COLUMN_GAME_NUMBER, i + 1)
                                put(GameEntry.COLUMN_SCORE, 0)
                                put(GameEntry.COLUMN_SERIES_ID, seriesId)
                            }
                            val gameId = database.insert(GameEntry.TABLE_NAME, null, values)

                            if (gameId != -1L) {
                                for (j in 0 until Game.NUMBER_OF_FRAMES) {
                                    values = ContentValues().apply {
                                        put(FrameEntry.COLUMN_FRAME_NUMBER, j + 1)
                                        put(FrameEntry.COLUMN_GAME_ID, gameId)
                                    }
                                    database.insert(FrameEntry.TABLE_NAME, null, values)
                                }
                            } else {
                                throw IllegalStateException("Game was not saved, ID is -1")
                            }
                        }
                    }

                    if (!inTransaction) {
                        database.setTransactionSuccessful()
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not create a new series")
                    return@async Pair(
                            null,
                            BCError(R.string.error_saving_series, R.string.error_series_not_saved)
                    )
                } finally {
                    if (!inTransaction) {
                        database.endTransaction()
                    }
                }

                Pair(Series(
                        league = league,
                        id = seriesId,
                        date = date,
                        numberOfGames = numberOfGames,
                        scores = scores,
                        matchPlay = matchPlay
                ), null)
            }
        }

        /**
         * Update the [SeriesEntry] in the database.
         *
         * @param context to get database instance
         * @param id id of the series to update
         * @param league league that owns the series
         * @param date date of the series
         * @param numberOfGames number of games in the series
         * @param scores scores of the games in the series
         * @param matchPlay match play results of the games in the series
         * @param inTransaction if true, the method should not handle the database transaction as one
         *                      has already been created in another context
         * @return [BCError] only if an error occurred
         */
        private fun update(
            context: Context,
            id: Long,
            league: League,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            inTransaction: Boolean = false
        ): Deferred<Pair<Series?, BCError?>> {
            return async(CommonPool) {
                val database = Saviour.instance.getWritableDatabase(context).await()
                val values = ContentValues().apply {
                    put(SeriesEntry.COLUMN_SERIES_DATE, DateUtils.dateToSeriesDate(date))
                }

                if (!inTransaction) {
                    database.beginTransaction()
                }
                try {
                    database.update(SeriesEntry.TABLE_NAME, values, "${SeriesEntry._ID}=?", arrayOf(id.toString()))
                    if (!inTransaction) {
                        database.setTransactionSuccessful()
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Error updating series details ($id, $date)", ex)
                } finally {
                    if (!inTransaction) {
                        database.endTransaction()
                    }
                }

                Pair(Series(league, id, date, numberOfGames, scores, matchPlay), null)
            }
        }

        /**
         * Get all of the series belonging to the [League].
         *
         * @param context to get database instance
         * @param league the league whose series to retrieve
         * @return a [MutableList] of [Series] instances from the database
         */
        fun fetchAll(context: Context, league: League): Deferred<MutableList<Series>> {
            return async(CommonPool) {
                val seriesList: MutableList<Series> = ArrayList()
                val database = Saviour.instance.getReadableDatabase(context).await()

                val rawSeriesQuery = ("SELECT " +
                        "series.${SeriesEntry._ID} AS sid, " +
                        "${SeriesEntry.COLUMN_SERIES_DATE}, " +
                        "${GameEntry.COLUMN_SCORE}, " +
                        "${GameEntry.COLUMN_GAME_NUMBER}, " +
                        "${GameEntry.COLUMN_MATCH_PLAY} " +
                        "FROM ${SeriesEntry.TABLE_NAME} AS series " +
                        "INNER JOIN ${GameEntry.TABLE_NAME} " +
                        "ON sid=${GameEntry.COLUMN_SERIES_ID} " +
                        "WHERE ${SeriesEntry.COLUMN_LEAGUE_ID}=? " +
                        "ORDER BY ${SeriesEntry.COLUMN_SERIES_DATE} DESC, ${GameEntry.COLUMN_GAME_NUMBER}")

                var lastId: Long = -1
                var scores: MutableList<Int> = ArrayList()
                var matchPlay: MutableList<Byte> = ArrayList()

                /**
                 * Build a new [Series] instance from a cursor to the database.
                 *
                 * @param cursor database accessor
                 * @return a new series
                 */
                fun buildSeriesFromCursor(cursor: Cursor): Series {
                    val id = cursor.getLong(cursor.getColumnIndex("sid"))
                    val seriesDate = DateUtils.seriesDateToDate(cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE)))
                    return Series(
                            league,
                            id,
                            seriesDate,
                            scores.size,
                            scores,
                            matchPlay
                    )
                }

                val cursor = database.rawQuery(rawSeriesQuery, arrayOf(league.id.toString()))
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val newId = cursor.getLong(cursor.getColumnIndex("sid"))
                        if (newId != lastId && lastId != -1L) {
                            cursor.moveToPrevious()

                            seriesList.add(buildSeriesFromCursor(cursor))

                            scores = ArrayList()
                            matchPlay = ArrayList()
                            cursor.moveToNext()
                        }

                        scores.add(cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_SCORE)))
                        matchPlay.add(cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY)).toByte())

                        lastId = newId
                        cursor.moveToNext()
                    }

                    cursor.moveToPrevious()
                    seriesList.add(buildSeriesFromCursor(cursor))
                }
                cursor.close()

                seriesList
            }
        }
    }
}
