package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isRightSplit
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are right splits.
 */
class RightSplitsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::RightSplitsStatistic)

        const val Id = R.string.statistic_right_splits
    }

    override val titleId = Id
    override val id = Id.toLong()

    private var countS2asS: Boolean = Settings.CountS2AsS.booleanDefault

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isRightSplit(countS2asS)

    override fun updatePreferences(preferences: SharedPreferences) {
        countS2asS = preferences.getBoolean(Settings.CountS2AsS.prefName, Settings.CountS2AsS.booleanDefault)
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
