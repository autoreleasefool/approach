package ca.josephroque.bowlingcompanion.statistics.impl.firstball

import android.content.SharedPreferences
import android.os.Parcel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.interfaces.parcelableCreator
import ca.josephroque.bowlingcompanion.games.lane.Deck
import ca.josephroque.bowlingcompanion.games.lane.isHeadPin
import ca.josephroque.bowlingcompanion.settings.Settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Percentage of shots which are head pins.
 */
class HeadPinsStatistic(numerator: Int = 0, denominator: Int = 0) : FirstBallStatistic(numerator, denominator) {

    companion object {
        @Suppress("unused")
        @JvmField val CREATOR = parcelableCreator(::HeadPinsStatistic)

        const val Id = R.string.statistic_head_pins
    }

    private var countH2asH: Boolean = Settings.BooleanSetting.CountH2AsH.default

    override val titleId = Id
    override val id = Id.toLong()

    // MARK: Statistic

    override fun isModifiedBy(deck: Deck) = deck.isHeadPin(countH2asH)

    override fun updatePreferences(preferences: SharedPreferences) {
        countH2asH = Settings.BooleanSetting.CountH2AsH.getValue(preferences)
    }

    // MARK: Constructors

    private constructor(p: Parcel) : this(numerator = p.readInt(), denominator = p.readInt())
}
