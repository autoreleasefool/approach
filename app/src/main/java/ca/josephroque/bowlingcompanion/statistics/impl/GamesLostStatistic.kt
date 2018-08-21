package ca.josephroque.bowlingcompanion.statistics.impl

import android.os.Parcel
import android.os.Parcelable
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.Game
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.statistics.PercentageStatistic
import ca.josephroque.bowlingcompanion.statistics.StatisticsCategory

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of match play games lost.
 */
class GamesLostStatistic(override var numerator: Int, override var denominator: Int) : PercentageStatistic {

    // MARK: Modifiers

    /** @Override */
    override fun modify(game: Game) {
        denominator++
        if (game.matchPlay.result == MatchPlayResult.LOST) {
            numerator++
        }
    }

    // MARK: Overrides

    override val titleId = Id
    override val id = Id.toLong()
    override val category = StatisticsCategory.MatchPlay
    override fun isModifiedBy(game: Game) = true

    // MARK: Parcelable

    companion object {
        /** Creator, required by [Parcelable]. */
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::GamesLostStatistic)

        /** Unique ID for the statistic. */
        const val Id = R.string.statistic_games_lost
    }

    /**
     * Construct this statistic from a [Parcel].
     */
    constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
