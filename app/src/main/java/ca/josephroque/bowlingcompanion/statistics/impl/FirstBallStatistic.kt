package ca.josephroque.bowlingcompanion.statistics.impl

import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Parent class for statistics which are calculated based on the user throwing at a
 * full deck of pins.
 */
abstract class FirstBallStatistic(override var numerator: Int, override var denominator: Int) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: Frame) {
        // Every frame adds 1 possible hit
        denominator++
        numerator += if (isModifiedBy(frame.pinState[0])) 1 else 0
        if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
            // In the 10th frame, for each time the first or second ball cleared the lane, add
            // another middle hit chance, and check if the statistic is modified
            if (frame.pinState[0].arePinsCleared()) {
                denominator++
                numerator += if (isModifiedBy(frame.pinState[1])) 1 else 0
            }

            if (frame.pinState[1].arePinsCleared()) {
                denominator++
                numerator += if (isModifiedBy(frame.pinState[2])) 1 else 0
            }
        }
    }

    /** Indicates if this statistic will be modified by a given [Deck]. */
    abstract fun isModifiedBy(deck: Deck): Boolean

    override val category = StatisticsCategory.FirstBall

    // MARK: Overrides

    /** @Override */
    override fun isModifiedBy(frame: Frame) = frame.pinState.any { isModifiedBy(it) }

    /** @Override */
    override fun isModifiedBy(game: Game) = false

    /** @Override */
    override fun isModifiedBy(unit: StatisticsUnit) = false
}
