package ca.josephroque.bowlingcompanion.series

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Parcel
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.IDeletable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readDate
import ca.josephroque.bowlingcompanion.common.interfaces.writeDate
import ca.josephroque.bowlingcompanion.database.Annihilator
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.DatabaseManager
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.DateUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.lang.ref.WeakReference
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

    private var _isDeleted: Boolean = false
    override val isDeleted: Boolean
        get() = _isDeleted

    val prettyDate: String
        get() = DateUtils.dateToPretty(date)

    val total: Int
        get() = scores.sum()

    // MARK: Constructors

    private constructor(p: Parcel): this(
            league = p.readParcelable<League>(League::class.java.classLoader)!!,
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

    constructor(series: Series): this(
            league = series.league,
            id = series.id,
            date = series.date,
            numberOfGames = series.numberOfGames,
            scores = series.scores,
            matchPlay = series.matchPlay
    )

    // MARK: Parcelable

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

    // MARK: IDeletable

    override fun markForDeletion(): Series {
        val newInstance = Series(this)
        newInstance._isDeleted = true
        return newInstance
    }

    override fun cleanDeletion(): Series {
        val newInstance = Series(this)
        newInstance._isDeleted = false
        return newInstance
    }

    override fun delete(context: Context): Deferred<Unit> {
        return async(CommonPool) {
            if (id < 0) {
                return@async
            }

            Annihilator.instance.delete(
                    weakContext = WeakReference(context),
                    tableName = SeriesEntry.TABLE_NAME,
                    whereClause = "${SeriesEntry._ID}=?",
                    whereArgs = arrayOf(id.toString())
            )
        }
    }

    // MARK: Series

    fun fetchGames(context: Context): Deferred<MutableList<Game>> {
        return Game.fetchSeriesGames(context, this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "Series"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Series)

        const val PREFERRED_VIEW = "series_preferred_view"

        enum class View {
            Expanded, Condensed;

            companion object {
                private val map = View.values().associateBy(View::ordinal)
                fun fromInt(type: Int) = map[type]
            }
        }

        fun save(
            context: Context,
            league: League,
            id: Long,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            openDatabase: SQLiteDatabase? = null
        ): Deferred<Pair<Series?, BCError?>> {
            return if (id < 0) {
                createNewAndSave(context, league, date, numberOfGames, scores, matchPlay, openDatabase)
            } else {
                update(context, id, league, date, numberOfGames, scores, matchPlay, openDatabase)
            }
        }

        private fun createNewAndSave(
            context: Context,
            league: League,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            openDatabase: SQLiteDatabase? = null
        ): Deferred<Pair<Series?, BCError?>> {
            return async(CommonPool) {
                val database = openDatabase ?: DatabaseManager.getWritableDatabase(context).await()
                val inTransaction = openDatabase != null && openDatabase.inTransaction()
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
                        for (gameIdx in 0 until numberOfGames) {
                            values = ContentValues().apply {
                                put(GameEntry.COLUMN_GAME_NUMBER, gameIdx + 1)
                                put(GameEntry.COLUMN_SCORE, 0)
                                put(GameEntry.COLUMN_SERIES_ID, seriesId)
                            }
                            val gameId = database.insert(GameEntry.TABLE_NAME, null, values)

                            if (gameId != -1L) {
                                for (frameIdx in 0 until Game.NUMBER_OF_FRAMES) {
                                    values = ContentValues().apply {
                                        put(FrameEntry.COLUMN_FRAME_NUMBER, frameIdx + 1)
                                        put(FrameEntry.COLUMN_GAME_ID, gameId)
                                        put(FrameEntry.COLUMN_IS_ACCESSED, gameIdx == 0 && frameIdx == 0)
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

        private fun update(
            context: Context,
            id: Long,
            league: League,
            date: Date,
            numberOfGames: Int,
            scores: List<Int>,
            matchPlay: List<Byte>,
            openDatabase: SQLiteDatabase? = null
        ): Deferred<Pair<Series?, BCError?>> {
            return async(CommonPool) {
                val database = openDatabase ?: DatabaseManager.getWritableDatabase(context).await()
                val inTransaction = openDatabase != null && openDatabase.inTransaction()
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

        fun fetchAll(context: Context, league: League): Deferred<MutableList<Series>> {
            return async(CommonPool) {
                val seriesList: MutableList<Series> = ArrayList()
                val database = DatabaseManager.getReadableDatabase(context).await()

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
