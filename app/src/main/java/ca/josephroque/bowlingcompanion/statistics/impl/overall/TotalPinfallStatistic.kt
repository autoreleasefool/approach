package ca.josephroque.bowlingcompanion.statistics.impl.overall

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.IntegerStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatGame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Total pinfall.
 */
class TotalPinfallStatistic(override var value: Int = 0) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(game: StatGame) {
        value += game.score
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.General
    override fun isModifiedBy(game: StatGame) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TotalPinfallStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_total_pinfall
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(value = p.readInt())
}
