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
data class MatchPlay(
        val gameId: Long,
        override val id: Long,
        val opponentName: String,
        val opponentScore: Int,
        val result: MatchPlayResult
): IIdentifiable, KParcelable {

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

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(gameId)
        writeLong(id)
        writeString(opponentName)
        writeInt(opponentScore)
        writeInt(result.ordinal)
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