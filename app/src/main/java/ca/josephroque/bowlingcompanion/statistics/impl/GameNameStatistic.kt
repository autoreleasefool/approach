package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.StringStatistic
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Name of the game whose statistics are on display.
 */
class GameNameStatistic(override var value: String = "") : StringStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(unit: StatisticsUnit) {
        value = unit.name
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.General

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GameNameStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_game
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(value = p.readString())

    // MARK: Overrides

    /** @Override */
    override fun isModifiedBy(frame: Frame) = false

    /** @Override */
    override fun isModifiedBy(game: Game) = false

    /** @Override */
    override fun isModifiedBy(unit: StatisticsUnit) = true
}
