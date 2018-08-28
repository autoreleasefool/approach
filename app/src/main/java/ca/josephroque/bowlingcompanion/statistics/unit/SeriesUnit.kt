package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readDate
import ca.josephroque.bowlingcompanion.common.interfaces.writeDate
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.utils.DateUtils
import kotlinx.coroutines.experimental.Deferred
import java.util.Date

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Series] whose statistics can be loaded and displayed.
 */
class SeriesUnit(
    val bowlerName: String,
    val leagueName: String,
    val seriesId: Long,
    val seriesDate: Date,
    parcel: Parcel? = null
) : StatisticsUnit(parcel) {

    // MARK: Overrides

    override val name: String
        get() = DateUtils.dateToPretty(seriesDate)
    override val excludedCategories: Set<StatisticsCategory> = setOf(StatisticsCategory.Average, StatisticsCategory.Series)
    override val excludedStatisticIds: Set<Int> = setOf(GameNameStatistic.Id)

    // MARK: StatisticsUnit

    /** @Override */
    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForSeries(context, seriesId)
    }

    // MARK: KParcelable

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(bowlerName)
        writeString(leagueName)
        writeLong(seriesId)
        writeDate(seriesDate)
        writeStatisticsToParcel(this)
    }

    /**
     * Construct a [SeriesUnit] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            bowlerName = p.readString(),
            leagueName = p.readString(),
            seriesId = p.readLong(),
            seriesDate = p.readDate()!!,
            parcel = p
    )

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "SeriesUnit"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::SeriesUnit)
    }
}
