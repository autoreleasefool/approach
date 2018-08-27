package ca.josephroque.bowlingcompanion.statistics.unit

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
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

    // MARK: Overrides

    override val name: String = bowlerName
    override val excludedCategories: Set<StatisticsCategory> = emptySet()
    override val excludedStatisticIds: Set<Int> = setOf(LeagueNameStatistic.Id, SeriesNameStatistic.Id, GameNameStatistic.Id)

    // MARK: StatisticsUnit

    /** @Override */
    override fun getSeriesForStatistics(context: Context): Deferred<List<StatSeries>> {
        return StatSeries.loadSeriesForBowler(context, bowlerId)
    }

    // MARK: KParcelable

    /** @Override */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(bowlerId)
        writeString(name)
        writeStatisticsToParcel(this)
    }

    /**
     * Construct a [BowlerUnit] from a [Parcel].
     */
    private constructor(p: Parcel): this(
            bowlerId = p.readLong(),
            bowlerName = p.readString(),
            parcel = p
    )

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "BowlerUnit"

        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::BowlerUnit)
    }
}
