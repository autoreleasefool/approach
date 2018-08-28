package ca.josephroque.bowlingcompanion.statistics.impl.general

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.StringStatistic
import ca.josephroque.bowlingcompanion.statistics.unit.BowlerUnit
import ca.josephroque.bowlingcompanion.statistics.unit.GameUnit
import ca.josephroque.bowlingcompanion.statistics.unit.LeagueUnit
import ca.josephroque.bowlingcompanion.statistics.unit.SeriesUnit
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Name of the bowler whose statistics are on display.
 */
class BowlerNameStatistic(override var value: String = "") : StringStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(unit: StatisticsUnit) {
        when (unit) {
            is GameUnit -> value = unit.bowlerName
            is SeriesUnit -> value = unit.bowlerName
            is LeagueUnit -> value = unit.bowlerName
            is BowlerUnit -> value = unit.name
        }
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.General
    override fun isModifiedBy(unit: StatisticsUnit) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::BowlerNameStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_bowler
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    private constructor(p: Parcel): this(value = p.readString())
}
