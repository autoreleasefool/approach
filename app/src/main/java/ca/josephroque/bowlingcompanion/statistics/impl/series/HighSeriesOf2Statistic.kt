package ca.josephroque.bowlingcompanion.statistics.impl.series

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Highest series of 2 games.
 */
class HighSeriesOf2Statistic(value: Int) : HighSeriesStatistic(value) {

    // MARK: Overrides

    override val seriesSize = 2
    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::HighSeriesOf2Statistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_high_series_of_2
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(value = p.readInt())
}
