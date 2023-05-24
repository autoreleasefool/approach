package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.arePinsCleared
import ca.josephroque.bowlingcompanion.games.lane.isHeadPin
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of possible head pins which the user successfully spared.
 */
class HeadPinsSparedStatistic(numerator: Int = 0, denominator: Int = 0) : SecondBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::HeadPinsSparedStatistic)

        const val Id = R.string.statistic_head_pins_spared
    }

    override val titleId = Id
    override val id = Id.toLong()
    override val secondaryGraphDataLabelId = R.string.statistic_total_head_pins

    private var countH2asH: Boolean = Settings.BooleanSetting.CountH2AsH.default

    // MARK: Statistic

    override fun isModifiedByFirstBall(deck: Deck) = deck.isHeadPin(countH2asH)

    override fun isModifiedBySecondBall(deck: Deck) = deck.arePinsCleared

    override fun updatePreferences(preferences: SharedPreferences) {
        countH2asH = Settings.BooleanSetting.CountH2AsH.getValue(preferences)
    }

    // MARK: Constructors

    private constructor(p: Parcel): this(numerator = p.readInt(), denominator = p.readInt())
}
