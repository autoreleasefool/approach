package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isLeftSplit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are left splits.
 */
class LeftSplitsStatistic(numerator: Int, denominator: Int) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck) = deck.isLeftSplit

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftSplitsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_left_splits
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
