package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.support.v7.preference.PreferenceManager
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticHelper
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.graph.StatisticsGraphLine
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.average.PerGameAverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.series.HighSeriesStatistic
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import ca.josephroque.bowlingcompanion.utils.Analytics
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
        @Suppress("unused")
        private const val TAG = "StatisticsUnit"
    }

    abstract val name: String
    abstract val excludedCategories: Set<StatisticsCategory>
    abstract val excludedStatisticIds: Set<Int>

    private var cachedSeries: List<StatSeries>? = initialSeries
    private var cachedStatistics: MutableList<StatisticListItem>? = initialStatistics

    // MARK: Constructors

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

    // MARK: StatisticsUnit

    protected abstract fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>>

    fun clearCache() {
        cachedSeries = null
        cachedStatistics = null
    }

    fun getStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            if (cachedStatistics == null) {
                cachedStatistics = buildStatistics(context).await()
            }

            return@async cachedStatistics!!
        }
    }

    fun getStatisticGraphData(context: Context, statisticId: Long, accumulative: Boolean): Deferred<Pair<List<StatisticsGraphLine>, List<String>>> {
        return async(CommonPool) {
            val graphData: MutableList<MutableList<Entry>> = ArrayList()
            val graphLabels: MutableList<String> = ArrayList()

            val seriesList = this@StatisticsUnit.cachedSeries ?: getSeriesForStatistics(context).await()
            val statistic = StatisticHelper.getStatistic(statisticId)

            if (!statistic.canBeGraphed || seriesList.isEmpty()) {
                return@async Pair(emptyList<StatisticsGraphLine>(), graphLabels)
            }

            // To determine current week and when to add a new entry to the chart
            val calendar = Calendar.getInstance()
            calendar.time = seriesList[0].date
            var lastDate = seriesList[0].date
            var lastYear = calendar.get(Calendar.YEAR)
            var lastWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            var xPos = 0F

            fun updateGraph() {
                addGraphEntries(graphData, xPos, statistic)
                graphLabels.add(DateUtils.dateToShort(lastDate))
                if (!accumulative) statistic.zero()
                xPos++
            }

            for (series in seriesList) {
                calendar.time = series.date
                val newDate = series.date
                val newYear = calendar.get(Calendar.YEAR)
                val newWeek = calendar.get(Calendar.WEEK_OF_YEAR)

                // Either the year or week has incremented, so an entry should be added to the graph
                if (newYear > lastYear || newWeek > lastWeek) {
                    updateGraph()
                }

                adjustStatisticBySeries(statistic, series)

                lastDate = newDate
                lastYear = newYear
                lastWeek = newWeek
            }

            // Add the final entry
            updateGraph()

            val lines: List<StatisticsGraphLine> = graphData.mapIndexed { index, data ->
                val label = when (index) {
                    0 -> context.resources.getString(statistic.primaryGraphDataLabelId)
                    1 -> context.resources.getString(statistic.secondaryGraphDataLabelId!!)
                    else -> throw IllegalAccessException("Only up to 2 lines are available for a statistic.")
                }
                return@mapIndexed StatisticsGraphLine(label, data)
            }

            return@async Pair(lines, graphLabels)
        }
    }

    // MARK: Private functions

    private fun buildStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            Analytics.trackStatisticsLoaded(Analytics.Companion.EventTime.Begin)

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

            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            for (statistic in statistics) {
                // Update preferences for statistics
                statistic.updatePreferences(preferences)

                // Only allow the [StatisticsUnit] to modify each stat once
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

            Analytics.trackStatisticsLoaded(Analytics.Companion.EventTime.End)

            return@async statisticListItems
        }
    }

    private fun adjustStatisticBySeries(statistic: Statistic, series: StatSeries) {
        if (statistic.isModifiedBy(series)) {
            statistic.modify(series)
        }

        for (game in series.games) {
            // Don't process games with scores of 0
            if (game.score == 0) {
                continue
            }

            if (statistic.isModifiedBy(game)) {
                statistic.modify(game)
            }

            // Don't process frames for manual games
            if (game.isManual) {
                continue
            }

            for (frame in game.frames) {
                if (frame.isAccessed && statistic.isModifiedBy(frame)) {
                    statistic.modify(frame)
                }
            }
        }
    }

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
            // TODO: java.lang.ClassCastException: ca.josephroque.bowlingcompanion.statistics.StatisticsCategory cannot be cast to ca.josephroque.bowlingcompanion.statistics.Statistic
            writeIntArray(statistics.map { (it as Statistic).titleId }.toIntArray())
            for (statistic in statistics) {
                writeParcelable((statistic as Statistic), 0)
            }
        } else {
            writeBoolean(false)
        }
    }
}
