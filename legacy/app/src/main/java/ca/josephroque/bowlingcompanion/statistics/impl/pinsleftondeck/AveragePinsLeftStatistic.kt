package ca.josephroque.bowlingcompanion.statistics.impl.pinsleftondeck

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.statistics.AverageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.immutable.StatFrame
import ca.josephroque.bowlingcompanion.statistics.immutable.StatGame

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Average pins left on deck per game.
 */
class AveragePinsLeftStatistic(override var total: Int = 0, override var divisor: Int = 0) : AverageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(frame: StatFrame) {
        total += frame.pinsLeftOnDeck
    }

    /** @Override */
    override fun modify(game: StatGame) {
        divisor++
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.PinsOnDeck
    override fun isModifiedBy(frame: StatFrame) = true
    override fun isModifiedBy(game: StatGame) = !game.isManual && game.score > 0

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
    private constructor(p: Parcel) : this(total = p.readInt(), divisor = p.readInt())
}
