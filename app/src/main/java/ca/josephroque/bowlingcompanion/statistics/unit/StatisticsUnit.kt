package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticHelper
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.average.PerGameAverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesStatistic
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import ca.josephroque.bowlingcompanion.utils.DateUtils
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.util.Calendar

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single unit which provides a list of statistics to be displayed.
 */
abstract class StatisticsUnit(initialSeries: List<StatSeries>? = null, initialStatistics: MutableList<StatisticListItem>? = null) : KParcelable {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "StatisticsUnit"
    }

    /** Name of the unit. */
    abstract val name: String

    /** Set of [StatisticsCategory]s which must not be displayed for the unit. */
    abstract val excludedCategories: Set<StatisticsCategory>

    /** Set of ids for [Statistic]s which must not be displayed for the unit. */
    abstract val excludedStatisticIds: Set<Int>

    /** Cache of series. */
    protected var cachedSeries: List<StatSeries>? = initialSeries

    /** Cache of statistics. */
    protected var cachedStatistics: MutableList<StatisticListItem>? = initialStatistics

    /**
     * Get a list of [StatSeries] for building the statistics.
     *
     * @param context to get access to the database
     * @return a list of [StatSeries]
     */
    protected abstract fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>>

    /**
     * Get the unit's statistics to be displayed.
     *
     * @param context to get access to the database
     * @return a list of [Statistic]s
     */
    fun getStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            if (cachedStatistics == null) {
                cachedStatistics = buildStatistics(context).await()
            }

            return@async cachedStatistics!!
        }
    }

    /**
     * Build the graph entries for a single statistic from the unit/
     *
     * @param context to get access to the database
     * @param statisticId id of the statistic to build
     * @param accumulative true to accumulate statistic over all time, false for week by week
     * @return the list of [Entry] items to display the statistic in a graph
     */
    fun getStatisticGraphData(context: Context, statisticId: Long, accumulative: Boolean): Deferred<Pair<List<List<Entry>>, List<String>>> {
        return async (CommonPool) {
            val graphData: MutableList<MutableList<Entry>> = ArrayList()
            val graphLabels: MutableList<String> = ArrayList()

            val seriesList = this@StatisticsUnit.cachedSeries ?: getSeriesForStatistics(context).await()
            val statistic = StatisticHelper.getStatistic(statisticId)

            if (!statistic.canBeGraphed || seriesList.isEmpty()) {
                return@async Pair(graphData, graphLabels)
            }

            // To determine current week and when to add a new entry to the chart
            val calendar = Calendar.getInstance()
            calendar.time = seriesList[0].date
            var lastDate = seriesList[0].date
            var lastYear = calendar.get(Calendar.YEAR)
            var lastWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            var xPos = 0F

            for (series in seriesList) {
                calendar.time = series.date
                val newDate = series.date
                val newYear = calendar.get(Calendar.YEAR)
                val newWeek = calendar.get(Calendar.WEEK_OF_YEAR)

                // Either the year or week has incremented, so an entry should be added to the graph
                if (newYear > lastYear || newWeek > lastWeek) {
                    addGraphEntries(graphData, xPos, statistic)
                    graphLabels.add(DateUtils.dateToShort(lastDate))
                    if (!accumulative) statistic.zero()
                    xPos++
                }

                adjustStatisticBySeries(statistic, series)

                lastDate = newDate
                lastYear = newYear
                lastWeek = newWeek
            }

            // Add the final entry
            addGraphEntries(graphData, xPos, statistic)
            graphLabels.add(DateUtils.dateToShort(lastDate))

            return@async Pair(graphData, graphLabels)
        }
    }

    /**
     * Build the unit's statistics. This should return a new list each time it is called.
     * Caching for this method exists in the [StatisticsUnit] abstract class.
     *
     * @param context to get access to the database
     * @return a list of [Statistic]s
     */
    private fun buildStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            val seriesList = this@StatisticsUnit.cachedSeries ?: getSeriesForStatistics(context).await()
            val statistics = StatisticHelper.getFreshStatistics()
            val statisticListItems: MutableList<StatisticListItem> = ArrayList(statistics.size + StatisticsCategory.values().size)

            // Filter out categories which the unit does not accept
            for (category in excludedCategories) {
                statistics.removeAll { it.category == category }
            }

            // Filter out statistics which the unit does not accept
            for (statisticId in excludedStatisticIds) {
                statistics.removeAll { it.titleId == statisticId }
            }

            // Only allow the [StatisticsUnit] to modify each stat once
            for (statistic in statistics) {
                if (statistic.isModifiedBy(this@StatisticsUnit)) {
                    statistic.modify(this@StatisticsUnit)
                }
            }

            // Parse the remaining statistics and update as per the unit/series/game/frame
            for (series in seriesList) {
                for (statistic in statistics) {
                    adjustStatisticBySeries(statistic, series)
                }
            }

            // Filter invalid statistics
            statistics.removeAll {
                (it is HighSeriesStatistic && it.value == 0) ||
                (it is PerGameAverageStatistic && it.total == 0)
            }

            // Add categories in place in the list
            var lastCategory: StatisticsCategory? = null
            for (statistic in statistics) {
                if (statistic.category != lastCategory) {
                    statisticListItems.add(statistic.category)
                    lastCategory = statistic.category
                }
                statisticListItems.add(statistic)
            }

            return@async statisticListItems
        }
    }

    /**
     * Iterate over the series and its games/frames to modify the statistic in place accordingly.
     *
     * @param statistic the statistic to adjust
     * @param series the series that will modify the statistic
     */
    private fun adjustStatisticBySeries(statistic: Statistic, series: StatSeries) {
        if (statistic.isModifiedBy(series)) {
            statistic.modify(series)
        }

        for (game in series.games) {
            if (statistic.isModifiedBy(game)) {
                statistic.modify(game)
            }

            // Don't process frames for games with score 0 or manual games
            if (game.isManual || game.score == 0) {
                continue
            }

            for (frame in game.frames) {
                if (frame.isAccessed && statistic.isModifiedBy(frame)) {
                    statistic.modify(frame)
                }
            }
        }
    }

    /**
     * Add entries to [graphData] from the statistic's current state.
     *
     * @param graphData to add entries to
     * @param xPos x position of the entries to add
     * @param statistic to get entry y positions
     */
    private fun addGraphEntries(graphData: MutableList<MutableList<Entry>>, xPos: Float, statistic: Statistic) {
        statistic.primaryGraphY?.let {
            if (graphData.size < 1) { graphData.add(ArrayList()) }
            graphData[0].add(Entry(xPos, it))
        }

        statistic.secondaryGraphY?.let {
            if (graphData.size < 2) { graphData.add(ArrayList()) }
            graphData[1].add(Entry(xPos, it))
        }
    }

    // MARK: KParcelable

    /**
     * Construct a [StatisticsUnit] from a [Parcel].
     */
    protected constructor(p: Parcel? = null): this(
            initialSeries = if (p != null && p.readBoolean()) {
                ArrayList<StatSeries>().apply {
                    val array = p.readParcelableArray(StatSeries::class.java.classLoader)
                    for (i in 0 until array.size) {
                        add(array[i] as StatSeries)
                    }
                }
            } else {
                null
            },
            initialStatistics = if (p != null && p.readBoolean()) {
                ArrayList<StatisticListItem>().apply {
                    val statisticsSize = p.readInt()
                    val statisticTypes = IntArray(statisticsSize)
                    p.readIntArray(statisticTypes)

                    for (i in 0 until statisticsSize) {
                        this.add(StatisticHelper.readParcelable(p, statisticTypes[i]))
                    }
                }
            } else {
                null
            }
    )

    /**
     * Write the unit's cached series and statistics to the [Parcel].
     *
     * @param p the parcel to write to
     */
    protected fun writeCacheToParcel(p: Parcel) = with(p) {
        val series = cachedSeries
        if (series != null) {
            writeBoolean(true)
            writeParcelableArray(series.toTypedArray(), 0)
        } else {
            writeBoolean(false)
        }

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
}
