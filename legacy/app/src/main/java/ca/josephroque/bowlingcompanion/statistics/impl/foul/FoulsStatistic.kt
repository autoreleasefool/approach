package ca.josephroque.bowlingcompanion.statistics.impl.foul

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.IntegerStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Total number of fouls.
 */
class FoulsStatistic(override var value: Int = 0) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: StatFrame) {
        value += frame.ballFouled.sumBy { if (it) 1 else 0 }
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.Fouls
    override fun isModifiedBy(frame: StatFrame) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::FoulsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_fouls
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(value = p.readInt())
}
