package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
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
class LeftsSparedStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftsSparedStatistic)

        const val Id = R.string.statistic_lefts_spared
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val secondaryGraphDataLabelId = R.string.statistic_total_lefts

    // MARK: Statistic

    override fun isModifiedByFirstBall(deck: Deck) = deck.isLeft

    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
