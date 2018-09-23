package ca.josephroque.bowlingcompanion.common.interfaces

import android.content.Context
import android.support.v7.preference.PreferenceManager
import ca.josephroque.bowlingcompanion.settings.Settings
import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enforces objects to have a name and average.
 */
interface INameAverage : IDeletable, IIdentifiable {

    companion object {
        @Suppress("unused")
        private const val TAG = "INameAverage"

        private val decimalFormatter = DecimalFormat("0.#")
    }

    val name: String
    val average: Double

    fun getDisplayAverage(context: Context): String {
        val averageAsDecimal = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Settings.AverageAsDecimal.prefName, Settings.AverageAsDecimal.booleanDefault)
        return if (averageAsDecimal) {
            decimalFormatter.format(average)
        } else {
            average.roundToInt().toString()
        }
    }
}
