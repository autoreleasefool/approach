package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isMiddleHit
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.FirstBallStatistic

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which hit the middle pin.
 */
class MiddleHitsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::MiddleHitsStatistic)

        const val Id = R.string.statistic_middle_hits
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.Overall

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isMiddleHit

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
