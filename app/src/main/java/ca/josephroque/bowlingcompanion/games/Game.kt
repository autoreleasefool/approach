package ca.josephroque.bowlingcompanion.games

import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.*
import ca.josephroque.bowlingcompanion.matchplay.MatchPlay
import ca.josephroque.bowlingcompanion.series.Series

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single game recording.
 */
data class Game(
        val series: Series,
        override val id: Long,
        val ordinal: Int,
        val frames: List<Frame>,
        val matchPlay: MatchPlay
): IIdentifiable, KParcelable {

    /** Indicates if the game is locked and cannot be edited. */
    var isLocked: Boolean = false

    /** Indicates if the game's score is manually set. */
    var isManual: Boolean = false

    /** Score of the game. */
    var score: Int = 0

    /**
     * Construct a [Game] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            series = p.readParcelable<Series>(Series::class.java.classLoader),
            id = p.readLong(),
            ordinal = p.readInt(),
            frames = arrayListOf<Frame>().apply {
                val parcelableArray = p.readParcelableArray(Frame::class.java.classLoader)
                this.addAll(parcelableArray.map {
                    return@map it as Frame
                })
            },
            matchPlay = p.readParcelable<MatchPlay>(MatchPlay::class.java.classLoader)
    ) {
        isLocked = p.readBoolean()
        isManual = p.readBoolean()
        score = p.readInt()
    }

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(series, 0)
        writeLong(id)
        writeInt(ordinal)
        writeInt(score)
        writeParcelableArray(frames.toTypedArray(), 0)
        writeParcelable(matchPlay, 0)
        writeBoolean(isLocked)
        writeBoolean(isManual)
        writeInt(score)
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
    }

}
