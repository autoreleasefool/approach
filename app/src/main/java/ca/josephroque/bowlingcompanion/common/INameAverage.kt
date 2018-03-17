package ca.josephroque.bowlingcompanion.common

import java.text.DecimalFormat

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enforces objects to have a name and average.
 */
interface INameAverage: IDeletable, IIdentifiable {

    /** Name of the object. */
    var name: String

    /** Average of the object. */
    var average: Double

    /**
     * Round the average to a number of decimal places and return as a [String] for display
     *
     * @param count number of significant digits after the decimal place
     * @return the average, rounded
     */
    fun getRoundedAverage(count: Int): String {
        if (count < 0) {
            throw IllegalArgumentException("Cannot be negative.")
        }

        val df = DecimalFormat("#." + "#".repeat(count))
        return df.format(average)
    }
}
