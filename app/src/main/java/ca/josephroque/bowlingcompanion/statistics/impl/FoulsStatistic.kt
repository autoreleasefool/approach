package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.IntegerStatistic
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Total number of fouls.
 */
class FoulsStatistic(override var value: Int) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: Frame) {
        value += frame.ballFouled.sumBy { if (it) 1 else 0 }
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val referenceFrame = Statistic.Companion.ReferenceFrame.ByFrame
    override val category = StatisticsCategory.Fouls

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::FoulsStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_fouls
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(value = p.readInt())

    // MARK: Overrides

    /** @Override */
    override fun isModifiedBy(frame: Frame) = true

    /** @Override */
    override fun isModifiedBy(game: Game) = false

    /** @Override */
    override fun isModifiedBy(unit: StatisticsUnit) = false
}
