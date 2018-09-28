package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Parent class for statistics which are calculated based on the user throwing at a
 * full deck of pins.
 */
abstract class FirstBallStatistic(override var numerator: Int = 0, override var denominator: Int = 0) : PercentageStatistic {

    override fun isModifiedBy(frame: StatFrame) = true
    override val category = StatisticsCategory.FirstBall
    override val secondaryGraphDataLabelId = R.string.statistic_total_shots_at_middle

    // MARK: Statistic

    override fun modify(frame: StatFrame) {
        // This function has a similar construction to `StrikeMiddleHitsStatistic.modify(StatFrame)
        // and the two should remain aligned

        // Every frame adds 1 possible hit
        denominator++
        numerator += if (isModifiedBy(frame.pinState[0])) 1 else 0
        if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
            // In the 10th frame, for each time the first or second ball cleared the lane, add
            // another middle hit chance, and check if the statistic is modified
            if (frame.pinState[0].arePinsCleared) {
                denominator++
                numerator += if (isModifiedBy(frame.pinState[1])) 1 else 0
            }

            if (frame.pinState[1].arePinsCleared) {
                denominator++
                numerator += if (isModifiedBy(frame.pinState[2])) 1 else 0
            }
        }
    }

    // MARK: FirstBallStatistic

    abstract fun isModifiedBy(deck: Deck): Boolean
}
