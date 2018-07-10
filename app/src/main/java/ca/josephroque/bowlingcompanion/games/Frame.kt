package ca.josephroque.bowlingcompanion.games

import android.os.Parcelable
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.*
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.Pin
import ca.josephroque.bowlingcompanion.games.lane.toBooleanArray

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single frame in a game.
 */
data class Frame(
        val gameId: Long,
        override val id: Long,
        val ordinal: Int,
        var isAccessed: Boolean,
        var pinState: Array<Deck>,
        var ballFouled: BooleanArray
): IIdentifiable, KParcelable {

    /**
     * Construct a [Frame] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            gameId = p.readLong(),
            id = p.readLong(),
            ordinal = p.readInt(),
            isAccessed = p.readBoolean(),
            pinState = Array(NUMBER_OF_BALLS, {
                val pins = BooleanArray(Game.NUMBER_OF_PINS)
                p.readBooleanArray(pins)
                return@Array Pin.deckFromBooleanArray(pins)
            }),
            ballFouled = BooleanArray(NUMBER_OF_BALLS).apply {
                p.readBooleanArray(this)
            }
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(gameId)
        writeLong(id)
        writeInt(ordinal)
        writeBoolean(isAccessed)
        for (i in 0 until NUMBER_OF_BALLS) {
            writeBooleanArray(pinState[i].toBooleanArray())
        }
        writeBooleanArray(ballFouled)
    }

    /** Ordinal of the frame, zero based. Frames are numbered 0 to 9 this way. */
    val zeroBasedOrdinal: Int
        get() = ordinal - 1

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "Frame"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Frame)

        /** Number of balls in a frame. */
        const val NUMBER_OF_BALLS = 3

        /** Index of the last ball in a frame. */
        const val LAST_BALL = NUMBER_OF_BALLS - 1

        /** Max value of a single frame. */
        const val MAX_VALUE = 15
    }
}