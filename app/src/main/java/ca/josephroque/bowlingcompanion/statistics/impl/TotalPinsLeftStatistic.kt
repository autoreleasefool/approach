package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.IntegerStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Totals pins left on deck across all games.
 */
class TotalPinsLeftStatistic(override var value: Int) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: Frame) {
        value += frame.pinsLeftOnDeck
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.PinsOnDeck

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::TotalPinsLeftStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_pins_left
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
