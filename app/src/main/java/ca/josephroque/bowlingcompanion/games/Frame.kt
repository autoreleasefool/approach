package ca.josephroque.bowlingcompanion.games

import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.Pin
import ca.josephroque.bowlingcompanion.games.lane.toBooleanArray
import ca.josephroque.bowlingcompanion.scoring.Fouls

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single frame in a game.
 */
class Frame(
    val gameId: Long,
    override val id: Long,
    val ordinal: Int,
    var isAccessed: Boolean,
    var pinState: Array<Deck>,
    var ballFouled: BooleanArray
) : IIdentifiable, KParcelable {

    companion object {
        @Suppress("unused")
        private const val TAG = "Frame"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Frame)

        const val NUMBER_OF_BALLS = 3
        const val LAST_BALL = NUMBER_OF_BALLS - 1
        const val MAX_VALUE = 15
    }

    val zeroBasedOrdinal: Int
        get() = ordinal - 1

    /** LEGACY: garbage method of serializing fouls to database. */
    private val dbFoulString: String
        get() {
            val builder = StringBuilder()
            ballFouled.forEachIndexed { index, foul -> if (foul) { builder.append(index + 1) } }
            if (builder.isEmpty()) { builder.append(0) }
            return builder.toString()
        }

    /** LEGACY: garbage method of serializing fouls to database. */
    val dbFouls: Int
        get() = Fouls.foulStringToInt(dbFoulString)

    val pinsLeftOnDeck: Int
        get() = pinState[Frame.NUMBER_OF_BALLS].sumBy { if (it.onDeck) it.value else 0 }

    // MARK: Constructors

    private constructor(p: Parcel): this(
            gameId = p.readLong(),
            id = p.readLong(),
            ordinal = p.readInt(),
            isAccessed = p.readBoolean(),
            pinState = Array(NUMBER_OF_BALLS) {
                val pins = BooleanArray(Game.NUMBER_OF_PINS)
                p.readBooleanArray(pins)
                return@Array Pin.deckFromBooleanArray(pins)
            },
            ballFouled = BooleanArray(NUMBER_OF_BALLS).apply {
                p.readBooleanArray(this)
            }
    )

    private constructor(other: Frame): this(
            gameId = other.gameId,
            id = other.id,
            ordinal = other.ordinal,
            isAccessed = other.isAccessed,
            pinState = Array(NUMBER_OF_BALLS) {
                return@Array other.pinState[it].map { pin -> pin.deepCopy() }.toTypedArray()
            },
            ballFouled = other.ballFouled.clone()
    )

    // MARK: Frame

    fun deepCopy(): Frame {
        return Frame(this)
    }

    // MARK: Parcelable

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
}
