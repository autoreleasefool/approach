package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isLeftTwelve

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible left twelves which the user successfully spared
 */
class LeftTwelvesSparedStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    // MARK: Statistic

    override fun isModifiedByFirstBall(deck: Deck) = deck.isLeftTwelve
    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    override val titleId = Id
    override val id = Id.toLong()
    override val secondaryGraphDataLabelId = R.string.statistic_left_twelves

    // MARK: Parcelable

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftTwelvesSparedStatistic)

        const val Id = R.string.statistic_left_twelves_spared
    }

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
