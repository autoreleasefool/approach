package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isMiddleHit
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of middle hits which were strikes
 */
class StrikeMiddleHitsStatistic(override var numerator: Int = 0, override var denominator: Int = 0) : PercentageStatistic {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StrikeMiddleHitsStatistic)

        const val Id = R.string.statistic_strike_middle_hits
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.Overall
    override val secondaryGraphDataLabelId = R.string.statistic_total_shots_at_middle
    override fun isModifiedBy(frame: StatFrame) = true

    // MARK: Statistic

    override fun modify(frame: StatFrame) {
        // This function has a similar construction to `FirstBallStatistic.modify(StatFrame)
        // and the two should remain aligned

        // Every frame adds 1 possible hit
        denominator += if (frame.pinState[0].isMiddleHit) 1 else 0
        numerator += if (frame.pinState[0].arePinsCleared) 1 else 0
        if (frame.zeroBasedOrdinal == Game.LAST_FRAME) {
            // In the 10th frame, for each time the first or second ball cleared the lane,
            // check if the statistic is modified
            if (frame.pinState[0].arePinsCleared) {
                denominator += if (frame.pinState[1].isMiddleHit) 1 else 0
                numerator += if (frame.pinState[1].arePinsCleared) 1 else 0
            }

            if (frame.pinState[1].arePinsCleared) {
                denominator += if (frame.pinState[2].isMiddleHit) 1 else 0
                numerator += if (frame.pinState[2].arePinsCleared) 1 else 0
            }
        }
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
