package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isSplit
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are splits.
 */
class SplitsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::SplitsStatistic)

        const val Id = R.string.statistic_splits
    }

    override val titleId = Id
    override val id = Id.toLong()

    private var countS2asS: Boolean = Settings.BooleanSetting.CountS2AsS.default

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isSplit(countS2asS)

    override fun updatePreferences(preferences: SharedPreferences) {
        countS2asS = Settings.BooleanSetting.CountS2AsS.getValue(preferences)
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
