package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.headPin
import ca.josephroque.bowlingcompanion.games.lane.right2Pin
import ca.josephroque.bowlingcompanion.games.lane.right3Pin
import ca.josephroque.bowlingcompanion.games.lane.value

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are right chop offs.
 */
class RightChopOffsStatistic(numerator: Int, denominator: Int) : FirstBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedBy(deck: Deck): Boolean = isRightChopOff(deck)

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::RightChopOffsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_right_chops

        /**
         * Check for a left chop off.
         *
         * @param deck the deck to check
         */
        fun isRightChopOff(deck: Deck): Boolean = deck.value(true) == 10 && deck.right2Pin.isDown && deck.right3Pin.isDown && deck.headPin.isDown
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
