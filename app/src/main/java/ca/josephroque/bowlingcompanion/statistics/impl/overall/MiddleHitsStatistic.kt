package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isMiddleHit
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.FirstBallStatistic

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which hit the middle pin.
 */
class MiddleHitsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck) = deck.isMiddleHit

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::MiddleHitsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_middle_hits
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
