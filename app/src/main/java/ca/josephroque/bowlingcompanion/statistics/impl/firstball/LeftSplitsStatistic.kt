package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isLeftSplit
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are left splits.
 */
class LeftSplitsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::LeftSplitsStatistic)

        const val Id = R.string.statistic_left_splits
    }

    override val titleId = Id
    override val id = Id.toLong()

    private var countS2asS: Boolean = Settings.CountS2AsS.booleanDefault

    // MARK: Statistics

    override fun isModifiedBy(deck: Deck) = deck.isLeftSplit(countS2asS)

    override fun updatePreferences(preferences: SharedPreferences) {
        countS2asS = preferences.getBoolean(Settings.CountS2AsS.prefName, Settings.CountS2AsS.booleanDefault)
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
