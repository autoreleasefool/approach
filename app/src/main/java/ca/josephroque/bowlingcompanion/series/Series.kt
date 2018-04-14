package ca.josephroque.bowlingcompanion.series

import android.content.ContentValues
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.KParcelable
import ca.josephroque.bowlingcompanion.common.parcelableCreator
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.utils.BCError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import android.util.Log
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.IDeletable
import ca.josephroque.bowlingcompanion.common.IIdentifiable
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.utils.DateUtils

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A series of games in a [League].
 */
data class Series(val league: League,
                  override var id: Long,
                  var date: String,
                  var numberOfGames: Int,
                  var scores: List<Int>,
                  var matchPlay: List<Byte>
): IIdentifiable, IDeletable, KParcelable {

    /** @Override */
    override var isDeleted: Boolean = false

    /** Beautifies the date to be displayed. */
    val prettyDate: String
        get() = DateUtils.formattedDateToPrettyCompact(date)

    /**
     * Construct [Series] from a [Parcel]
     */
    private constructor(p: Parcel): this(
            league = p.readParcelable<League>(League::class.java.classLoader),
            id = p.readLong(),
            date = p.readString(),
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

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(league, 0)
        writeLong(id)
        writeString(date)
        writeInt(numberOfGames)
        writeInt(scores.size)
        writeIntArray(scores.toIntArray())
        writeInt(matchPlay.size)
        writeByteArray(matchPlay.toByteArray())
    }

    /**
     * Save this series to the database.
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
     * Create a new [SeriesEntry] in the database.
     *
     * @param context to get database instance
     * @return [BCError] only if an error occurred
     */
    private fun createNewAndSave(context: Context): Deferred<BCError?> {
        return async(CommonPool) {
            val database = DatabaseHelper.getInstance(context).writableDatabase
            var values = ContentValues().apply {
                put(SeriesEntry.COLUMN_SERIES_DATE, date)
                put(SeriesEntry.COLUMN_LEAGUE_ID, league.id)
            }

            database.beginTransaction()
            try {
                val seriesId = database.insert(SeriesEntry.TABLE_NAME, null, values)

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
                                    database.insert(FrameEntry.TABLE_NAME, null, values)
                                }
                            }
                        } else {
                            throw IllegalStateException("Game was not saved, ID is -1")
                        }
                    }
                }

                this@Series.id = seriesId
                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Could not create a new series")
                return@async BCError(
                        context.resources.getString(R.string.error_saving_series),
                        context.resources.getString(R.string.error_series_not_saved),
                        BCError.Severity.Error
                )
            } finally {
                database.endTransaction()
            }

            null
        }
    }

    /**
     * Update the [SeriesEntry] in the database.
     *
     * @param context to get database instance
     * @return [BCError] only if an error occurred
     */
    private fun update(context: Context): Deferred<BCError?> {
        return async(CommonPool) {
            val database = DatabaseHelper.getInstance(context).writableDatabase
            val values = ContentValues().apply {
                put(SeriesEntry.COLUMN_SERIES_DATE, date)
            }

            database.beginTransaction()
            try {
                database.update(SeriesEntry.TABLE_NAME, values, "${SeriesEntry._ID}=?", arrayOf(id.toString()))
                database.setTransactionSuccessful()
            } catch (ex: Exception) {
                Log.e(TAG, "Error updating series details ($id, $date)", ex)
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

    companion object {
        /** Logging identifier. */
        private const val TAG = "Series"

        /** Creator, required by [Parcelable]. */
        @JvmField val CREATOR = parcelableCreator(::Series)

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
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val rawSeriesQuery = ("SELECT "
                        + "series.${SeriesEntry._ID} AS sid, "
                        + "${SeriesEntry.COLUMN_SERIES_DATE}, "
                        + "${GameEntry.COLUMN_SCORE}, "
                        + "${GameEntry.COLUMN_GAME_NUMBER}, "
                        + GameEntry.COLUMN_MATCH_PLAY
                        + " FROM ${SeriesEntry.TABLE_NAME} AS series"
                        + " INNER JOIN ${GameEntry.TABLE_NAME}"
                        + " ON sid=${GameEntry.COLUMN_SERIES_ID}"
                        + " WHERE ${SeriesEntry.COLUMN_LEAGUE_ID}=?"
                        + " ORDER BY ${SeriesEntry.COLUMN_SERIES_DATE} DESC, "
                        + GameEntry.COLUMN_GAME_NUMBER)

                var lastId: Long = -1
                var scores: MutableList<Int> = ArrayList()
                var matchPlay: MutableList<Byte> = ArrayList()

                val cursor = database.rawQuery(rawSeriesQuery, arrayOf(league.id.toString()))
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val newId = cursor.getLong(cursor.getColumnIndex("sid"))
                        if (newId != lastId && lastId != -1L) {
                            cursor.moveToPrevious()

                            val id = cursor.getLong(cursor.getColumnIndex("sid"))
                            val seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE))
                            val series = Series(
                                    league,
                                    id,
                                    seriesDate,
                                    scores.size,
                                    scores,
                                    matchPlay
                            )

                            seriesList.add(series)
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
                    val id = cursor.getLong(cursor.getColumnIndex("sid"))
                    val seriesDate = cursor.getString(cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_DATE))
                    val series = Series(
                            league,
                            id,
                            seriesDate,
                            scores.size,
                            scores,
                            matchPlay
                    )

                    seriesList.add(series)
                }
                cursor.close()

                seriesList
            }
        }
    }
}