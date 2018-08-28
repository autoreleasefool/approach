package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isAce
import ca.josephroque.bowlingcompanion.games.lane.isHeadPin
import ca.josephroque.bowlingcompanion.games.lane.isSplit
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.impl.firstball.SecondBallStatistic

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible spares which the user successfully spared.
 */
class SpareConversionsStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    // MARK: Modifiers

    /** @Override */
    override fun isModifiedByFirstBall(firstBall: Deck, secondBall: Deck): Boolean {
        // Don't add a spare chance if the first ball was a split / head pin / aces, unless the second shot was a spare
        return !firstBall.arePinsCleared && ((!firstBall.isAce && !firstBall.isHeadPin && !firstBall.isSplit) || secondBall.arePinsCleared)
    }

    /** @Override */
    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.Overall

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::SpareConversionsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_spare_conversion
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
