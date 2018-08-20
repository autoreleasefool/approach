package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.headPin
import ca.josephroque.bowlingcompanion.games.lane.left3Pin
import ca.josephroque.bowlingcompanion.games.lane.value

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are left splits.
 */
class LeftSplitsStatistic(numerator: Int, denominator: Int) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck): Boolean = isLeftSplit(deck)

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftSplitsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_left_splits

        /**
         * Check for a left split.
         *
         * @param deck the deck to check
         */
        fun isLeftSplit(deck: Deck): Boolean = deck.value(true) == 8 && deck.headPin.isDown && deck.left3Pin.isDown
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
