package ca.josephroque.bowlingcompanion.statistics.immutable

import android.os.Parcel
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

    val pinsLeftOnDeck: Int
        get() = pinState[Frame.LAST_BALL].sumBy { if (it.onDeck) it.value else 0 }

    val zeroBasedOrdinal: Int
        get() = ordinal - 1

    // MARK: Constructor

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

    // MARK: Parcelable

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
        @Suppress("unused")
        private const val TAG = "StatFrame"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StatFrame)

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
