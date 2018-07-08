package ca.josephroque.bowlingcompanion.games

import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single frame in a game.
 */
data class Frame(
        val gameId: Long,
        override val id: Long,
        val ordinal: Int
): IIdentifiable, KParcelable {

    /** Indicates if the frame has been accessed or not. */
    var isAccessed: Boolean = false

    /** Current state of each pin, for each ball. True is down, false is up. */
    var pinState: Array<BooleanArray> = Array(NUMBER_OF_BALLS, {
        return@Array BooleanArray(Game.NUMBER_OF_PINS)
    })

    /** Foul state for each ball. True is fouled, false is no foul. */
    var ballFouled: BooleanArray = BooleanArray(NUMBER_OF_BALLS)

    /**
     * Construct a [Frame] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            gameId = p.readLong(),
            id = p.readLong(),
            ordinal = p.readInt()
    ) {
        isAccessed = p.readBoolean()
        for (i in 0 until NUMBER_OF_BALLS) {
            p.readBooleanArray(pinState[i])
        }
        p.readBooleanArray(ballFouled)
    }

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(gameId)
        writeLong(id)
        writeInt(ordinal)
        writeBoolean(isAccessed)
        for (i in 0 until NUMBER_OF_BALLS) {
            writeBooleanArray(pinState[i])
        }
        writeBooleanArray(ballFouled)
    }

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
    }
}