package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isLeft

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are lefts.
 */
class LeftsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_lefts
    }

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isLeft

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
