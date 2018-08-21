package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isLeft

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible lefts which the user successfully spared.
 */
class LeftsSparedStatistic(numerator: Int, denominator: Int) : SecondBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedByFirstBall(deck: Deck) = deck.isLeft

    /** @Override */
    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftsSparedStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_lefts_spared
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
