package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
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

    // MARK: Constructors

    private constructor(p: Parcel): this(
            bowlerName = p.readString(),
            leagueName = p.readString(),
            seriesId = p.readLong(),
            seriesDate = p.readDate()!!,
            parcel = p
    )

    // MARK: StatisticsUnit

    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForSeries(context, seriesId)
    }

    // MARK: KParcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(bowlerName)
        writeString(leagueName)
        writeLong(seriesId)
        writeDate(seriesDate)
        writeCacheToParcel(this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "SeriesUnit"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::SeriesUnit)
    }
}
