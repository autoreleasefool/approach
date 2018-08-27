package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.common.interfaces.readBoolean
import ca.josephroque.bowlingcompanion.common.interfaces.writeBoolean
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.impl.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.NumberOfGamesStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.TotalPinfallStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.pinsleftondeck.AveragePinsLeftStatistic
import ca.josephroque.bowlingcompanion.statistics.list.StatisticListItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Game] whose statistics can be loaded and displayed.
 */
class GameUnit(val game: Game, initialStatistics: MutableList<StatisticListItem>? = null) : StatisticsUnit(initialStatistics) {

    // MARK: Overrides

    override val name = "Game ${game.ordinal}"
    override val excludedCategories = setOf(StatisticsCategory.Average, StatisticsCategory.MatchPlay, StatisticsCategory.Series)
    override val excludedStatisticIds = setOf(AveragePinsLeftStatistic.Id, GameAverageStatistic.Id, HighSingleStatistic.Id, TotalPinfallStatistic.Id, NumberOfGamesStatistic.Id)

    // MARK: StatisticsUnit

    /** @Override */
    override fun buildStatistics(context: Context): Deferred<MutableList<StatisticListItem>> {
        // TODO: build game statistics
        return async(CommonPool) {
            return@async emptyList<StatisticListItem>().toMutableList()
        }
    }

    // MARK: KParcelable

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(game, 0)

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
     * Construct a [GameUnit] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            game = p.readParcelable(Game::class.java.classLoader),
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
        private const val TAG = "GameUnit"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GameUnit)
    }
}
