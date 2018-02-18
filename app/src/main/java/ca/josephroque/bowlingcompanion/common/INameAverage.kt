package ca.josephroque.bowlingcompanion.common

import java.text.DecimalFormat

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Enforces objects which have a name, average, and ID.
 */
interface INameAverage {

    /** Name of the object. */
    val name: String

    /** Average of the object. */
    val average: Double

    /** Unique ID of the object. */
    val id: Long

    fun getRoundedAverage(count: Int): String {
        if (count < 0) {
            throw IllegalArgumentException("Cannot be negative.")
        }

        val df = DecimalFormat("#." + "#".repeat(count))
        return df.format(average)
    }
}
