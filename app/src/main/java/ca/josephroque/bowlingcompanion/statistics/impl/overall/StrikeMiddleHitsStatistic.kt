package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
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
class StrikeMiddleHitsStatistic(override var numerator: Int, override var denominator: Int) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: StatFrame) {
        if (frame.pinState[0].isMiddleHit) {
            denominator++

            if (frame.pinState[0].arePinsCleared) {
                numerator++
            }
        }
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.Overall
    override fun isModifiedBy(frame: StatFrame) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::StrikeMiddleHitsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_strike_middle_hits
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
