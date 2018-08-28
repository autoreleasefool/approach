package ca.josephroque.bowlingcompanion.statistics.immutable

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.Pin
import ca.josephroque.bowlingcompanion.games.lane.toBooleanArray

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * An immutable Frame for calculating statistics, loaded from the database.
 */
class StatFrame(
    override val id: Long,
    val ordinal: Int,
    val isAccessed: Boolean,
    val pinState: Array<Deck>,
    val ballFouled: BooleanArray
) : IIdentifiable, KParcelable {

    /** Number of pins left on deck at the end of the frame. */
    val pinsLeftOnDeck: Int
        get() = pinState[Frame.LAST_BALL].sumBy { if (it.onDeck) it.value else 0 }

    /** Ordinal of the frame, zero based. Frames are numbered 0 to 9 this way. */
    val zeroBasedOrdinal: Int
        get() = ordinal - 1

    // MARK: KParcelable

    /**
     * Construct a [StatFrame] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            id = p.readLong(),
            ordinal = p.readInt(),
            isAccessed = p.readBoolean(),
            pinState = Array(Frame.NUMBER_OF_BALLS) {
                val pins = BooleanArray(Game.NUMBER_OF_PINS)
                p.readBooleanArray(pins)
                return@Array Pin.deckFromBooleanArray(pins)
            },
            ballFouled = BooleanArray(Frame.NUMBER_OF_BALLS).apply {
                p.readBooleanArray(this)
            }
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeInt(ordinal)
        writeBoolean(isAccessed)
        for (i in 0 until Frame.NUMBER_OF_BALLS) {
            writeBooleanArray(pinState[i].toBooleanArray())
        }
        writeBooleanArray(ballFouled)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatFrame"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StatFrame)

        /** Fields to query to create a [StatFrame]. */
        val QUERY_FIELDS = arrayOf(
            "frame.${FrameEntry._ID} as fid",
            "frame.${FrameEntry.COLUMN_FRAME_NUMBER}",
            "frame.${FrameEntry.COLUMN_IS_ACCESSED}",
            "frame.${FrameEntry.COLUMN_FOULS}",
            "frame.${FrameEntry.COLUMN_PIN_STATE[0]}",
            "frame.${FrameEntry.COLUMN_PIN_STATE[1]}",
            "frame.${FrameEntry.COLUMN_PIN_STATE[2]}"
        )
    }
}
