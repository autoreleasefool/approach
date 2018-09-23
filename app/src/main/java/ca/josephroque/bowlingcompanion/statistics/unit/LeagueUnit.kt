package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.SeriesNameStatistic
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [League] whose statistics can be loaded and displayed.
 */
class LeagueUnit(val bowlerName: String, val leagueId: Long, leagueName: String, parcel: Parcel? = null) : StatisticsUnit(parcel) {

    // MARK: Overrides

    override val name: String = leagueName
    override val excludedCategories: Set<StatisticsCategory> = emptySet()
    override val excludedStatisticIds: Set<Int> = setOf(SeriesNameStatistic.Id, GameNameStatistic.Id)

    // MARK: Constructors

    private constructor(p: Parcel): this(
            bowlerName = p.readString(),
            leagueId = p.readLong(),
            leagueName = p.readString(),
            parcel = p
    )

    // MARK: StatisticsUnit

    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForLeague(context, leagueId)
    }

    // MARK: Parcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(bowlerName)
        writeLong(leagueId)
        writeString(name)
        writeCacheToParcel(this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "LeagueUnit"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeagueUnit)
    }
}
