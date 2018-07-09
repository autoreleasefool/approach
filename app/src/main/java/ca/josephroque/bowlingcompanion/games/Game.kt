package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.*
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.MatchPlayEntry
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.matchplay.MatchPlay
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.scoring.Fouls
import ca.josephroque.bowlingcompanion.scoring.Pin
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single game recording.
 */
data class Game(
        val series: Series,
        override val id: Long,
        val ordinal: Int,
        var score: Int,
        var isLocked: Boolean,
        var isManual: Boolean,
        val frames: List<Frame>,
        val matchPlay: MatchPlay
): IIdentifiable, KParcelable {

    /**
     * Construct a [Game] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            series = p.readParcelable<Series>(Series::class.java.classLoader),
            id = p.readLong(),
            ordinal = p.readInt(),
            score = p.readInt(),
            isLocked = p.readBoolean(),
            isManual = p.readBoolean(),
            frames = arrayListOf<Frame>().apply {
                val parcelableArray = p.readParcelableArray(Frame::class.java.classLoader)
                this.addAll(parcelableArray.map {
                    return@map it as Frame
                })
            },
            matchPlay = p.readParcelable<MatchPlay>(MatchPlay::class.java.classLoader)
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(series, 0)
        writeLong(id)
        writeInt(ordinal)
        writeInt(score)
        writeBoolean(isLocked)
        writeBoolean(isManual)
        writeParcelableArray(frames.toTypedArray(), 0)
        writeParcelable(matchPlay, 0)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Game"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Game)

        /** Number of frames in a single game. */
        const val NUMBER_OF_FRAMES = 10

        /** Index of the last frame in a game. */
        const val LAST_FRAME = NUMBER_OF_FRAMES - 1

        /** Number of pins used in the game. */
        const val NUMBER_OF_PINS = 5

        /** Maximum possible score. */
        const val MAX_SCORE = 450

        /**
         * Load a list of games for a series
         *
         * @param context to get database instance
         * @param series series to load games for
         * @return the list of games for the series
         */
        fun fetchSeriesGames(context: Context, series: Series): Deferred<MutableList<Game>> {
            return async(CommonPool) {
                val gameList: MutableList<Game> = ArrayList(series.numberOfGames)
                val database = DatabaseHelper.getInstance(context).readableDatabase

                val query = ("SELECT "
                        + "game.${GameEntry._ID} AS gid, "
                        + "game.${GameEntry.COLUMN_GAME_NUMBER}, "
                        + "game.${GameEntry.COLUMN_SCORE}, "
                        + "game.${GameEntry.COLUMN_IS_LOCKED}, "
                        + "game.${GameEntry.COLUMN_IS_MANUAL}, "
                        + "game.${GameEntry.COLUMN_MATCH_PLAY}, "
                        + "match.${MatchPlayEntry._ID} as mid, "
                        + "match.${MatchPlayEntry.COLUMN_OPPONENT_SCORE}, "
                        + "match.${MatchPlayEntry.COLUMN_OPPONENT_NAME}, "
                        + "frame.${FrameEntry._ID} as fid, "
                        + "frame.${FrameEntry.COLUMN_FRAME_NUMBER}, "
                        + "frame.${FrameEntry.COLUMN_IS_ACCESSED}, "
                        + "frame.${FrameEntry.COLUMN_PIN_STATE[0]}, "
                        + "frame.${FrameEntry.COLUMN_PIN_STATE[1]}, "
                        + "frame.${FrameEntry.COLUMN_PIN_STATE[2]}, "
                        + "frame.${FrameEntry.COLUMN_FOULS}"
                        + " FROM ${GameEntry.TABLE_NAME} AS game"
                        + " LEFT JOIN ${MatchPlayEntry.TABLE_NAME} as match"
                        + " ON gid=${MatchPlayEntry.COLUMN_GAME_ID}"
                        + " LEFT JOIN ${FrameEntry.TABLE_NAME} as frame"
                        + " ON gid=${FrameEntry.COLUMN_GAME_ID}"
                        + " WHERE ${GameEntry.COLUMN_SERIES_ID}=?"
                        + " GROUP BY gid"
                        + " ORDER BY game.${GameEntry.COLUMN_GAME_NUMBER}, frame.${FrameEntry.COLUMN_FRAME_NUMBER}")

                var lastId: Long = -1
                var frames: MutableList<Frame> = ArrayList(NUMBER_OF_FRAMES)

                /**
                 * Build a game from a cursor into the database.
                 *
                 * @param cursor database accessor
                 * @return a new game
                 */
                fun buildGameFromCursor(cursor: Cursor):Game {
                    val id = cursor.getLong(cursor.getColumnIndex("gid"))
                    val gameNumber = cursor.getInt(cursor.getColumnIndex("game.${GameEntry.COLUMN_GAME_NUMBER}"))
                    val score = cursor.getInt(cursor.getColumnIndex("game.${GameEntry.COLUMN_SCORE}"))
                    val isLocked = cursor.getInt(cursor.getColumnIndex("game.${GameEntry.COLUMN_IS_LOCKED}")) == 1
                    val isManual = cursor.getInt(cursor.getColumnIndex("game.${GameEntry.COLUMN_IS_MANUAL}")) == 1
                    val matchPlayResult = MatchPlayResult.fromInt(cursor.getInt(cursor.getColumnIndex("game.${GameEntry.COLUMN_MATCH_PLAY}")))

                    return Game(
                            series,
                            id,
                            gameNumber,
                            score,
                            isLocked,
                            isManual,
                            frames,
                            MatchPlay(
                                    id,
                                    cursor.getLong(cursor.getColumnIndex("match.${MatchPlayEntry._ID}")),
                                    cursor.getString(cursor.getColumnIndex("match.${MatchPlayEntry.COLUMN_OPPONENT_NAME}")),
                                    cursor.getInt(cursor.getColumnIndex("match.${MatchPlayEntry.COLUMN_OPPONENT_SCORE}")),
                                    matchPlayResult!!
                            )
                    )
                }

                val cursor = database.rawQuery(query, arrayOf(series.id.toString()))
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast) {
                        val newId = cursor.getLong(cursor.getColumnIndex("gid"))
                        if (newId != lastId && lastId != -1L) {
                            cursor.moveToPrevious()

                            gameList.add(buildGameFromCursor(cursor))

                            frames = ArrayList(NUMBER_OF_FRAMES)
                            cursor.moveToNext()
                        }

                        frames.add(Frame(
                                newId,
                                cursor.getLong(cursor.getColumnIndex("frame.${FrameEntry._ID}")),
                                cursor.getInt(cursor.getColumnIndex("frame.${FrameEntry.COLUMN_FRAME_NUMBER}")),
                                cursor.getInt(cursor.getColumnIndex("frame.${FrameEntry.COLUMN_IS_ACCESSED}")) == 1,
                                Array(NUMBER_OF_FRAMES, {
                                    return@Array Pin.ballIntToBoolean(cursor.getInt(cursor.getColumnIndex("frame.${FrameEntry.COLUMN_PIN_STATE[it]}")))
                                }),
                                BooleanArray(Frame.NUMBER_OF_BALLS, {
                                    return@BooleanArray Fouls.foulIntToString(cursor.getInt(cursor.getColumnIndex("frame.${FrameEntry.COLUMN_FOULS}"))).contains(it.toString())
                                })
                        ))

                        lastId = newId
                        cursor.moveToNext()
                    }

                    cursor.moveToPrevious()
                    gameList.add(buildGameFromCursor(cursor))
                }
                cursor.close()

                return@async gameList
            }
        }
    }

}
