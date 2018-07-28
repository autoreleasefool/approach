package ca.josephroque.bowlingcompanion.games

import android.content.Context
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.MatchPlayEntry
import ca.josephroque.bowlingcompanion.database.Saviour
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.Ball
import ca.josephroque.bowlingcompanion.games.lane.ballValue
import ca.josephroque.bowlingcompanion.games.lane.ballValueDifference
import ca.josephroque.bowlingcompanion.games.lane.Pin
import ca.josephroque.bowlingcompanion.games.lane.value
import ca.josephroque.bowlingcompanion.games.lane.valueDifference
import ca.josephroque.bowlingcompanion.matchplay.MatchPlay
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.scoring.Fouls
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single game recording.
 */
class Game(
    val series: Series,
    override val id: Long,
    val ordinal: Int,
    var isLocked: Boolean,
    var initialScore: Int,
    var isManual: Boolean,
    val frames: List<Frame>,
    val matchPlay: MatchPlay
) : IIdentifiable, KParcelable {

    /**
     * Construct a [Game] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            series = p.readParcelable<Series>(Series::class.java.classLoader),
            id = p.readLong(),
            ordinal = p.readInt(),
            initialScore = p.readInt(),
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

    /**
     * Construct a [Game] from a [Game].
     */
    private constructor(other: Game): this(
            series = other.series,
            id = other.id,
            ordinal = other.ordinal,
            initialScore = other.score,
            isLocked = other.isLocked,
            isManual = other.isManual,
            frames = other.frames.map { it.deepCopy() },
            matchPlay = other.matchPlay.deepCopy()
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

    /**
     * Create a deep copy of this game.
     *
     * @return a new instance of [Game]
     */
    fun deepCopy(): Game {
        return Game(this)
    }

    /**
     * Get the first frame which hasn't been accessed in a game, or the last frame if all frames
     * have been accessed.
     */
    val firstNewFrame: Int
        get() {
            val firstFrameNotAccessed = frames.indexOfFirst { frame -> !frame.isAccessed }
            return if (firstFrameNotAccessed > -1) firstFrameNotAccessed else Game.LAST_FRAME
        }

    /** Marks the score as dirty and needing recalculation. */
    private var dirty: Boolean = true

    /** The score of the game. */
    var score: Int = initialScore
        get() {
            if (dirty) { frameScores.hashCode() }
            return field
        }

    /** Number of fouls in the game. */
    val fouls: Int
        get() = frames.map { frame -> frame.ballFouled.count { it } }.sum()

    /** Backing field for [frameScores]. */
    private var _frameScores: IntArray = IntArray(frames.size)
        get() {
            if (!dirty) { return field }
            val frameScores = IntArray(frames.size)
            for (frameIdx in frames.size - 1 downTo 0) {
                val frame = frames[frameIdx]

                // Score last frame differently than other frames
                if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
                    for (ballIdx in Frame.LAST_BALL downTo 0) {
                        if (ballIdx == Frame.LAST_BALL) {
                            // Always add the value of the 3rd ball
                            frameScores[frameIdx] = frame.pinState[ballIdx].value(false)
                        } else if (frame.pinState[ballIdx].arePinsCleared()) {
                            // If all pins were knocked down in a previous ball, add the full
                            // value of that ball (it's a strike/spare)
                            frameScores[frameIdx] += frame.pinState[ballIdx].value(false)
                        }
                    }
                } else {
                    val nextFrame = frames[frameIdx + 1]
                    for (ballIdx in 0 until Frame.NUMBER_OF_BALLS) {
                        if (ballIdx == Frame.LAST_BALL) {
                            // If the loop is not exited by this point, there's no strike or spare
                            // Add basic value of the frame
                            frameScores[frameIdx] += frame.pinState[ballIdx].value(false)
                        } else if (frame.pinState[ballIdx].arePinsCleared()) {
                            // Either spare or strike occurred, add ball 0 of this frame and next
                            frameScores[frameIdx] += frame.pinState[ballIdx].value(false)
                            frameScores[frameIdx] += nextFrame.pinState[0].value(false)
                            val double = frameScores[frameIdx] == Frame.MAX_VALUE * 2

                            // Strike in this frame
                            if (ballIdx == 0) {
                                if (nextFrame.zeroBasedOrdinal == Game.LAST_FRAME) {
                                    // 9th frame must get additional scoring from 10th frame only
                                    if (double) {
                                        frameScores[frameIdx] += nextFrame.pinState[1].value(false)
                                    } else {
                                        frameScores[frameIdx] += nextFrame.pinState[1].valueDifference(nextFrame.pinState[0])
                                    }
                                } else if (!double) {
                                    frameScores[frameIdx] += nextFrame.pinState[1].valueDifference(nextFrame.pinState[0])
                                } else {
                                    frameScores[frameIdx] += frames[frameIdx + 2].pinState[0].value(false)
                                }
                            }
                            break
                        }
                    }
                }
            }

            // Get the accumulative score for each frame
            var totalScore = 0
            for (i in 0 until frames.size) {
                totalScore += frameScores[i]
                frameScores[i] = totalScore
            }

            // Calculate the final score of the game
            score = totalScore - fouls * Game.FOUL_PENALTY
            dirty = false

            field = frameScores
            return frameScores
        }

    /** Scores for each frame. */
    val frameScores: IntArray
        get() = _frameScores.copyOf()

    /**
     * Gets the text for each ball of each frame from this game.
     *
     * @return the text for each ball, as a list of arrays
     */
    fun getBallTextForFrames(): Deferred<List<Array<String>>> {
        return async(CommonPool) {
            return@async frames.mapIndexed { index, frame ->
                if (!frame.isAccessed) {
                    return@mapIndexed arrayOf("", "", "")
                }

                val balls = Array(Frame.NUMBER_OF_BALLS, { "" })
                if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
                    if (frame.pinState[0].arePinsCleared()) {
                        // If the first ball is a strike, the next two could be strikes/spares
                        balls[0] = Ball.Strike.toString()
                        if (frame.pinState[1].arePinsCleared()) {
                            // If the second ball is a strike
                            balls[1] = Ball.Strike.toString()
                            balls[2] = frame.pinState[2].ballValue(2, true, false)
                        } else {
                            // Second ball is not a strike
                            balls[1] = frame.pinState[1].ballValue(1, false, false)
                            balls[2] = if (frame.pinState[2].arePinsCleared())
                                Ball.Spare.toString()
                            else
                                frame.pinState[2].ballValueDifference(frame.pinState[1], 2, false, false)
                        }
                    } else {
                        // If the first ball is not a strike, check if the second ball is a spare or not and get value
                        balls[0] = frame.pinState[0].ballValue(0, false, false)
                        if (frame.pinState[1].arePinsCleared()) {
                            balls[1] = Ball.Spare.toString()
                            balls[2] = frame.pinState[2].ballValue(2, true, true)
                        } else {
                            balls[1] = frame.pinState[1].ballValueDifference(frame.pinState[0], 1, false, false)
                            balls[2] = frame.pinState[2].ballValueDifference(frame.pinState[1], 2, false, false)
                        }
                    }
                } else {
                    val nextFrame = frames[index + 1]
                    balls[0] = frame.pinState[0].ballValue(0, false, false)
                    if (!frame.pinState[0].arePinsCleared()) {
                        // When the first ball is not a strike, the second and third ball values need to be calculated
                        if (frame.pinState[1].arePinsCleared()) {
                            balls[1] = Ball.Spare.toString()
                            balls[2] = if (nextFrame.isAccessed)
                                nextFrame.pinState[0].ballValue(0, false, true)
                            else
                                Ball.None.toString()
                        } else {
                            balls[1] = frame.pinState[1].ballValueDifference(frame.pinState[0], 1, false, false)
                            balls[2] = frame.pinState[2].ballValueDifference(frame.pinState[1], 2, false, false)
                        }
                    } else {
                        // When the first ball is a strike, show the pins knocked down in the next frame, or empty frames
                        if (nextFrame.isAccessed) {
                            balls[1] = nextFrame.pinState[0].ballValue(0, false, true)
                            if (nextFrame.pinState[0].arePinsCleared()) {
                                // When the next frame is a strike, the 3rd ball will have to come from the frame after
                                if (frame.zeroBasedOrdinal < Game.LAST_FRAME - 1) {
                                    val nextNextFrame = frames[index + 2]
                                    balls[2] = if (nextNextFrame.isAccessed)
                                        nextNextFrame.pinState[0].ballValue(0, false, true)
                                    else
                                        Ball.None.toString()
                                } else {
                                    // In the 9th frame, the 3rd ball comes from the 10th frame's second ball
                                    balls[2] = nextFrame.pinState[1].ballValue(1, false, true)
                                }
                            } else {
                                balls[2] = nextFrame.pinState[1].ballValueDifference(nextFrame.pinState[0], 1, false, true)
                            }
                        } else {
                            balls[1] = Ball.None.toString()
                            balls[2] = Ball.None.toString()
                        }
                    }
                }

                return@mapIndexed balls
            }
        }
    }

    /**
     * Gets the cumulative score of each frame of this game. The final entry in the returned array
     * is the score of the game with fouls accounted for.
     *
     * @return the cumulative scores of each frame, as strings.
     */
    fun getScoreTextForFrames(): Deferred<List<String>> {
        return async(CommonPool) {
            return@async frameScores.mapIndexed { index, score ->
                return@mapIndexed if (index <= Game.LAST_FRAME) {
                    if (!frames[index].isAccessed) "" else score.toString()
                } else {
                    score.toString()
                }
            }
        }
    }

    /**
     * Mark the game as dirty after a field is updated.
     */
    fun markDirty() {
        dirty = true
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

        /** Number of points lost for a foul. */
        const val FOUL_PENALTY = 15

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
                val database = Saviour.instance.getReadableDatabase(context).await()

                val query = ("SELECT " +
                        "game.${GameEntry._ID} AS gid, " +
                        "game.${GameEntry.COLUMN_GAME_NUMBER}, " +
                        "game.${GameEntry.COLUMN_SCORE}, " +
                        "game.${GameEntry.COLUMN_IS_LOCKED}, " +
                        "game.${GameEntry.COLUMN_IS_MANUAL}, " +
                        "game.${GameEntry.COLUMN_MATCH_PLAY}, " +
                        "match.${MatchPlayEntry._ID} as mid, " +
                        "match.${MatchPlayEntry.COLUMN_OPPONENT_SCORE}, " +
                        "match.${MatchPlayEntry.COLUMN_OPPONENT_NAME}, " +
                        "frame.${FrameEntry._ID} as fid, " +
                        "frame.${FrameEntry.COLUMN_FRAME_NUMBER}, " +
                        "frame.${FrameEntry.COLUMN_IS_ACCESSED}, " +
                        "frame.${FrameEntry.COLUMN_PIN_STATE[0]}, " +
                        "frame.${FrameEntry.COLUMN_PIN_STATE[1]}, " +
                        "frame.${FrameEntry.COLUMN_PIN_STATE[2]}, " +
                        "frame.${FrameEntry.COLUMN_FOULS} " +
                        "FROM ${GameEntry.TABLE_NAME} AS game " +
                        "LEFT JOIN ${MatchPlayEntry.TABLE_NAME} as match " +
                        "ON gid=${MatchPlayEntry.COLUMN_GAME_ID} " +
                        "LEFT JOIN ${FrameEntry.TABLE_NAME} as frame " +
                        "ON gid=${FrameEntry.COLUMN_GAME_ID} " +
                        "WHERE ${GameEntry.COLUMN_SERIES_ID}=? " +
                        "GROUP BY gid, fid " +
                        "ORDER BY game.${GameEntry.COLUMN_GAME_NUMBER}, frame.${FrameEntry.COLUMN_FRAME_NUMBER}")

                var lastId: Long = -1
                var frames: MutableList<Frame> = ArrayList(NUMBER_OF_FRAMES)

                /**
                 * Build a game from a cursor into the database.
                 *
                 * @param cursor database accessor
                 * @return a new game
                 */
                fun buildGameFromCursor(cursor: Cursor): Game {
                    val id = cursor.getLong(cursor.getColumnIndex("gid"))
                    val gameNumber = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_GAME_NUMBER))
                    val score = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_SCORE))
                    val isLocked = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_LOCKED)) == 1
                    val isManual = cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_IS_MANUAL)) == 1
                    val matchPlayResult = MatchPlayResult.fromInt(cursor.getInt(cursor.getColumnIndex(GameEntry.COLUMN_MATCH_PLAY)))

                    return Game(
                            series = series,
                            id = id,
                            ordinal = gameNumber,
                            initialScore = score,
                            isLocked = isLocked,
                            isManual = isManual,
                            frames = frames,
                            matchPlay = MatchPlay(
                                    gameId = id,
                                    id = cursor.getLong(cursor.getColumnIndex("mid")),
                                    opponentName = cursor.getString(cursor.getColumnIndex(MatchPlayEntry.COLUMN_OPPONENT_NAME)) ?: "",
                                    opponentScore = cursor.getInt(cursor.getColumnIndex(MatchPlayEntry.COLUMN_OPPONENT_SCORE)),
                                    result = matchPlayResult!!
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
                                gameId = newId,
                                id = cursor.getLong(cursor.getColumnIndex("fid")),
                                ordinal = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FRAME_NUMBER)),
                                isAccessed = cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_IS_ACCESSED)) == 1,
                                pinState = Array(Frame.NUMBER_OF_BALLS, {
                                    return@Array Pin.deckFromInt(cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_PIN_STATE[it])))
                                }),
                                ballFouled = BooleanArray(Frame.NUMBER_OF_BALLS, {
                                    return@BooleanArray Fouls.foulIntToString(cursor.getInt(cursor.getColumnIndex(FrameEntry.COLUMN_FOULS))).contains((it + 1).toString())
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
