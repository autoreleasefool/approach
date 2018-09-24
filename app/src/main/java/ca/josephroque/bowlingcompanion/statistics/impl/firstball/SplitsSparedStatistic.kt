package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isSplit
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible splits which the user successfully spared.
 */
class SplitsSparedStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::SplitsSparedStatistic)

        const val Id = R.string.statistic_splits_spared
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val secondaryGraphDataLabelId = R.string.statistic_total_splits

    private var countS2asS: Boolean = Settings.CountS2AsS.booleanDefault

    // MARK: Statistic

    override fun isModifiedByFirstBall(deck: Deck) = deck.isSplit(countS2asS)

    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    override fun updatePreferences(preferences: SharedPreferences) {
        countS2asS = preferences.getBoolean(Settings.CountS2AsS.prefName, Settings.CountS2AsS.booleanDefault)
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
