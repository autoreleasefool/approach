package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isTwelve

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of first ball shots which scored 12 points.
 */
class TwelvesStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isTwelve

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TwelvesStatistic)

        const val Id = R.string.statistic_twelves
    }

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
