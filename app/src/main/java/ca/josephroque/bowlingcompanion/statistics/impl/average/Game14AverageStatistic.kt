package ca.josephroque.bowlingcompanion.statistics.impl.average

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Average score in the 14th game of a series.
 */
class Game14AverageStatistic(total: Int, divisor: Int) : PerGameAverageStatistic(total, divisor) {

    // MARK: Overrides

    override val gameNumber = 14
    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::Game14AverageStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_average_14
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(total = p.readInt(), divisor = p.readInt())
}
