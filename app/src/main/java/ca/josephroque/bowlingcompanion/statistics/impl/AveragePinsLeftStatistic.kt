package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.statistics.AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Average pins left on deck per game.
 */
class AveragePinsLeftStatistic(override var total: Int, override var divisor: Int) : AverageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: Frame) {
        total += frame.pinsLeftOnDeck
    }

    /** @Override */
    override fun modify(game: Game) {
        divisor++
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.PinsOnDeck
    override fun isModifiedBy(frame: Frame) = true
    override fun isModifiedBy(game: Game) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField
        val CREATOR = parcelableCreator(::AveragePinsLeftStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_average_pins_left
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel) : this(total = p.readInt(), divisor = p.readInt())
}
