package ca.josephroque.bowlingcompanion.matchplay

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Data about Match Play for a single game.
 */
class MatchPlay(
    val gameId: Long,
    override val id: Long,
    var opponentName: String,
    var opponentScore: Int,
    var result: MatchPlayResult
) : IIdentifiable, KParcelable {

    /**
     * Construct a [MatchPlay] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            gameId = p.readLong(),
            id = p.readLong(),
            opponentName = p.readString(),
            opponentScore = p.readInt(),
            result = MatchPlayResult.fromInt(p.readInt())!!
    )

    /**
     * Construct a [MatchPlay] from a [MatchPlay].
     */
    private constructor(other: MatchPlay): this(
            gameId = other.gameId,
            id = other.id,
            opponentName = other.opponentName,
            opponentScore = other.opponentScore,
            result = other.result
    )

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(gameId)
        writeLong(id)
        writeString(opponentName)
        writeInt(opponentScore)
        writeInt(result.ordinal)
    }

    /**
     * Create a deep copy of this match play.
     *
     * @return a new instance of [MatchPlay]
     */
    fun deepCopy(): MatchPlay {
        return MatchPlay(this)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "MatchPlay"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::MatchPlay)
    }
}
