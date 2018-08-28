package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.KParcelable
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A single unit which provides a list of statistics to be displayed.
 */
abstract class StatisticsUnit(initialStatistics: MutableList<StatisticListItem>? = null) : KParcelable {

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
     * Build the unit's statistics. This should return a new list each time it is called.
     * Caching for this method exists in the [StatisticsUnit] abstract class.
     *
     * @param context to get access to the database
     * @return a list of [Statistic]s
     */
    private fun buildStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            val seriesList = getSeriesForStatistics(context).await()
            val statistics = Statistic.getFreshStatistics()
            val statisticListItems: MutableList<StatisticListItem> = ArrayList(statistics.size + StatisticsCategory.values().size)

            // Filter out categories which the unit does not accept
            for (category in excludedCategories) {
                statistics.removeAll { it.category == category }
            }

            // Filter out statistics which the unit does not accept
            for (statisticId in excludedStatisticIds) {
                statistics.removeAll { it.titleId == statisticId }
            }

            // Parse the remaining statistics and update as per the unit/series/game/frame
            for (series in seriesList) {
                for (statistic in statistics) {
                    // Only allow the [StatisticsUnit] to modify each stat once
                    if (series == seriesList.first()) {
                        if (statistic.isModifiedBy(this@StatisticsUnit)) {
                            statistic.modify(this@StatisticsUnit)
                        }
                    }

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
            }

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

    // MARK: KParcelable

    /**
     * Construct a [StatisticsUnit] from a [Parcel].
     */
    protected constructor(p: Parcel? = null): this(
            initialStatistics = if (p != null && p.readBoolean()) {
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

    /**
     * Write the unit's cached statistics to the [Parcel].
     *
     * @param p the parcel to write to
     */
    protected fun writeStatisticsToParcel(p: Parcel) = with(p) {
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
