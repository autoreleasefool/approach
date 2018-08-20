package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Frame
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of match play games tied.
 */
class GamesTiedStatistic(override var numerator: Int, override var denominator: Int) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(game: Game) {
        denominator++
        if (game.matchPlay.result == MatchPlayResult.TIED) {
            numerator++
        }
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.MatchPlay

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GamesTiedStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_games_tied
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())

    // MARK: Overrides

    /** @Override */
    override fun isModifiedBy(frame: Frame) = false

    /** @Override */
    override fun isModifiedBy(game: Game) = true

    /** @Override */
    override fun isModifiedBy(unit: StatisticsUnit) = false
}
