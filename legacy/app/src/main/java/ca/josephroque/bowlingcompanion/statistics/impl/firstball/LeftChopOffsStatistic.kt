package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isLeftChopOff

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are left chop offs.
 */
class LeftChopOffsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck) = deck.isLeftChopOff

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftChopOffsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_left_chops
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
