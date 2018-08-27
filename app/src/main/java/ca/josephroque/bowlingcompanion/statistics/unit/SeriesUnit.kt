package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Series] whose statistics can be loaded and displayed.
 */
class SeriesUnit(val series: Series, initialStatistics: MutableList<StatisticListItem>? = null) : StatisticsUnit(initialStatistics) {

    // MARK: Overrides

    override val name: String = series.prettyDate
    override val excludedCategories: Set<StatisticsCategory> = setOf(StatisticsCategory.Average, StatisticsCategory.Series)
    override val excludedStatisticIds: Set<Int> = setOf(GameNameStatistic.Id)

    // MARK: StatisticsUnit

    /** @Override */
    override fun buildStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        // TODO: build series statistics
        return async(CommonPool) {
            return@async emptyList<StatisticListItem>().toMutableList()
        }
    }

    // MARK: KParcelable

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(series, 0)

        val statistics = cachedStatistics
        if (statistics != null) {
            writeBoolean(true)
            writeInt(statistics.size)
            writeIntArray(statistics.map { (it as Statistic).titleId }.toIntArray())
            for (statistic in statistics) {
                writeParcelable((statistic as Statistic), 0)
            }
        } else {
            writeBoolean(false)
        }
    }

    /**
     * Construct a [SeriesUnit] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            series = p.readParcelable(Series::class.java.classLoader),
            initialStatistics = if (p.readBoolean()) {
                ArrayList<StatisticListItem>().apply {
                    val statisticsSize = p.readInt()
                    val statisticTypes = IntArray(statisticsSize)
                    p.readIntArray(statisticTypes)

                    for (i in 0 until statisticsSize) {
                        this.add(Statistic.readParcelable(p, statisticTypes[i]))
                    }
                }
            } else {
                null
            }
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
