package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Parent class for statistics which are calculated based on the user throwing a second ball
 * at a deck of pins.
 */
abstract class SecondBallStatistic(override var numerator: Int = 0, override var denominator: Int = 0, private var incompatible: Int = 0) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: StatFrame) {
        if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
            // In the 10th frame, for each time the first or second ball cleared the lane,
            // add another second ball opportunity and check if the statistic is modified
            if (!frame.pinState[0].arePinsCleared && isModifiedByFirstBall(frame.pinState[0], frame.pinState[1])) {
                denominator++
                numerator += if (isModifiedBySecondBall(frame.pinState[1])) 1 else 0
            } else if (!frame.pinState[1].arePinsCleared && isModifiedByFirstBall(frame.pinState[1], frame.pinState[2])) {
                denominator++
                numerator += if (isModifiedBySecondBall(frame.pinState[2])) 1 else 0
            } else if (!frame.pinState[2].arePinsCleared && isModifiedByFirstBall(frame.pinState[2])) {
                incompatible++
            }
        } else {
            // Every frame which is not a strike adds 1 possible hit
            if (!frame.pinState[0].arePinsCleared && isModifiedByFirstBall(frame.pinState[0], frame.pinState[1])) {
                denominator++
                numerator += if (isModifiedBySecondBall(frame.pinState[1])) 1 else 0
            }
        }
    }

    // MARK: SecondBallStatistic

    /**
     * Indicates if this statistic will be modified by a given [Deck] which is the first ball of a frame.
     * Also provides the second ball for special cases. By default calls `isModifiedByFirstBall(deck)`.
     *
     * @param firstBall the first ball of the two shots
     * @param secondBall the second ball of the two shots
     * @return true if the first ball provides the right conditions for the statistic to be valid, false otherwise
     */
    open fun isModifiedByFirstBall(firstBall: Deck, secondBall: Deck): Boolean {
        return isModifiedByFirstBall(firstBall)
    }

    /** Indicates if this statistic will be modified by a given [Deck] which is the first ball of a frame. */
    open fun isModifiedByFirstBall(deck: Deck): Boolean {
        return false
    }

    /** Indicates if this statistic will be modified by a given [Deck] which is the second ball of a frame. */
    abstract fun isModifiedBySecondBall(deck: Deck): Boolean

    // MARK: Overrides

    override val category = StatisticsCategory.FirstBall
    override fun isModifiedBy(frame: StatFrame) = true

    /** @Override */
    override fun getSubtitle(): String? {
        return if (incompatible > 0) {
            "$incompatible thrown in 10th frame on the last ball could not be spared"
        } else {
            null
        }
    }
}
