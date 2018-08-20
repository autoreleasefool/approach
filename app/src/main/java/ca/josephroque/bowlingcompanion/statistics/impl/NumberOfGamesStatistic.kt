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
 * Total number of games.
 */
class NumberOfGamesStatistic(override var value: Int) : IntegerStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(game: Game) {
        value++
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val referenceFrame = Statistic.Companion.ReferenceFrame.ByGame
    override val category = StatisticsCategory.Overall

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::NumberOfGamesStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_number_of_games
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(value = p.readInt())

    // MARK: Overrides

    /** @Override */
    override fun isModifiedBy(frame: Frame) = false

    /** @Override */
    override fun isModifiedBy(game: Game) = true

    /** @Override */
    override fun isModifiedBy(unit: StatisticsUnit) = false
}
