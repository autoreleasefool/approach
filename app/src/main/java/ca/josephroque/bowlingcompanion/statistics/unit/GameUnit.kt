package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.HighSingleStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.NumberOfGamesStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.overall.TotalPinfallStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.pinsleftondeck.AveragePinsLeftStatistic
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Game] whose statistics can be loaded and displayed.
 */
class GameUnit(val seriesId: Long, val gameId: Long, val gameOrdinal: Int, parcel: Parcel? = null) : StatisticsUnit(parcel) {

    // MARK: Overrides

    override val name = "Game $gameOrdinal"
    override val excludedCategories = setOf(StatisticsCategory.Average, StatisticsCategory.MatchPlay, StatisticsCategory.Series)
    override val excludedStatisticIds = setOf(AveragePinsLeftStatistic.Id, GameAverageStatistic.Id, HighSingleStatistic.Id, TotalPinfallStatistic.Id, NumberOfGamesStatistic.Id)

    // MARK: StatisticsUnit

    /** @Override */
    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForSeries(context, seriesId)
    }

    // MARK: KParcelable

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(seriesId)
        writeLong(gameId)
        writeInt(gameOrdinal)
        writeStatisticsToParcel(this)
    }

    /**
     * Construct a [GameUnit] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            seriesId = p.readLong(),
            gameId = p.readLong(),
            gameOrdinal = p.readInt(),
            parcel = p
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
