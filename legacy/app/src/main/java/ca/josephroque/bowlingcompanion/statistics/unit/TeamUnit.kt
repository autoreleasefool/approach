package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatSeries
import ca.josephroque.bowlingcompanion.statistics.impl.general.BowlerNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.GameNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.LeagueNameStatistic
import ca.josephroque.bowlingcompanion.statistics.impl.general.SeriesNameStatistic
import ca.josephroque.bowlingcompanion.teams.Team
import kotlinx.coroutines.experimental.Deferred

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A [Team] whose statistics can be loaded and displayed.
 */
class TeamUnit(val teamId: Long, teamName: String, parcel: Parcel? = null) : StatisticsUnit(parcel) {

    override val name: String = teamName
    override val excludedCategories: Set<StatisticsCategory> = emptySet()
    override val excludedStatisticIds: Set<Int> = setOf(BowlerNameStatistic.Id, LeagueNameStatistic.Id, SeriesNameStatistic.Id, GameNameStatistic.Id)
    override val canShowGraphs = true

    // MARK: Constructors

    private constructor(p: Parcel): this(
            teamId = p.readLong(),
            teamName = p.readString()!!,
            parcel = p
    )

    // MARK: StatisticsUnit

    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForTeam(context, teamId)
    }

    // MARK: KParcelable

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(teamId)
        writeString(name)
        writeCacheToParcel(this)
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamUnit"

        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TeamUnit)
    }
}
