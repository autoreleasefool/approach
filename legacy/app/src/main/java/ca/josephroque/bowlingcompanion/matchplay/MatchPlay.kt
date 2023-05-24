package ca.josephroque.bowlingcompanion.matchplay

import android.os.Parcel
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

    // MARK: Constructors

    private constructor(p: Parcel): this(
            gameId = p.readLong(),
            id = p.readLong(),
            opponentName = p.readString()!!,
            opponentScore = p.readInt(),
            result = MatchPlayResult.fromInt(p.readInt())!!
    )

    private constructor(other: MatchPlay): this(
            gameId = other.gameId,
            id = other.id,
            opponentName = other.opponentName,
            opponentScore = other.opponentScore,
            result = other.result
    )

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(gameId)
        writeLong(id)
        writeString(opponentName)
        writeInt(opponentScore)
        writeInt(result.ordinal)
    }

    fun deepCopy(): MatchPlay {
        return MatchPlay(this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "MatchPlay"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::MatchPlay)
    }
}
