package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.LeagueNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.SeriesNameStatistic
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Bowler] whose statistics can be loaded and displayed.
 */
class BowlerUnit(val bowlerId: Long, bowlerName: String, parcel: Parcel? = null) : StatisticsUnit(parcel) {

    override val name: String = bowlerName
    override val excludedCategories: Set<StatisticsCategory> = emptySet()
    override val excludedStatisticIds: Set<Int> = setOf(LeagueNameStatistic.Id, SeriesNameStatistic.Id, GameNameStatistic.Id)
    override val canShowGraphs = true

    // MARK: Constructors

    private constructor(p: Parcel): this(
            bowlerId = p.readLong(),
            bowlerName = p.readString()!!,
            parcel = p
    )

    // MARK: StatisticsUnit

    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForBowler(context, bowlerId)
    }

    // MARK: KParcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(bowlerId)
        writeString(name)
        writeCacheToParcel(this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "BowlerUnit"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::BowlerUnit)
    }
}
