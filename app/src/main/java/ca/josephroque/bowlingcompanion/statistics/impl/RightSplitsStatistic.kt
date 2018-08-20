package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.headPin
import ca.josephroque.bowlingcompanion.games.lane.right3Pin
import ca.josephroque.bowlingcompanion.games.lane.value

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are right splits.
 */
class RightSplitsStatistic(numerator: Int, denominator: Int) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck): Boolean = isRightSplit(deck)

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::RightSplitsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_right_splits

        /**
         * Check for a right split.
         *
         * @param deck the deck to check
         */
        fun isRightSplit(deck: Deck): Boolean = deck.value(true) == 8 && deck.headPin.isDown && deck.right3Pin.isDown
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
