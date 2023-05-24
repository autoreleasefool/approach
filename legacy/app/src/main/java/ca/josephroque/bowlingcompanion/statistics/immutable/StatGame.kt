package ca.josephroque.bowlingcompanion.statistics.immutable

import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * An immutable Game for calculating statistics, created from the database.
 */
class StatGame(
    override val id: Long,
    val ordinal: Int,
    val score: Int,
    val isManual: Boolean,
    val frames: List<StatFrame>,
    val matchPlay: MatchPlayResult
) : IIdentifiable, KParcelable {

    // MARK: Constructor

    private constructor(p: Parcel): this(
            id = p.readLong(),
            ordinal = p.readInt(),
            score = p.readInt(),
            isManual = p.readBoolean(),
            frames = arrayListOf<StatFrame>().apply {
                val parcelableArray = p.readParcelableArray(StatFrame::class.java.classLoader)!!
                this.addAll(parcelableArray.map {
                    return@map it as StatFrame
                })
            },
            matchPlay = MatchPlayResult.fromInt(p.readInt())!!
    )

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeInt(ordinal)
        writeInt(score)
        writeBoolean(isManual)
        writeParcelableArray(frames.toTypedArray(), 0)
        writeInt(matchPlay.ordinal)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "StatGame"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StatGame)

        val QUERY_FIELDS = arrayOf(
            "game.${GameEntry._ID} as gid",
            "game.${GameEntry.COLUMN_GAME_NUMBER}",
            "game.${GameEntry.COLUMN_SCORE}",
            "game.${GameEntry.COLUMN_IS_MANUAL}",
            "game.${GameEntry.COLUMN_MATCH_PLAY}"
        )
    }
}
