package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isRightTwelve

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible right twelves which the user successfully spared
 */
class RightTwelvesSparedStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    // MARK: Statistic

    override fun isModifiedByFirstBall(deck: Deck) = deck.isRightTwelve
    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    override val titleId = Id
    override val id = Id.toLong()
    override val secondaryGraphDataLabelId = R.string.statistic_right_twelves

    // MARK: Parcelable

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::RightTwelvesSparedStatistic)

        const val Id = R.string.statistic_right_twelves_spared
    }

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
