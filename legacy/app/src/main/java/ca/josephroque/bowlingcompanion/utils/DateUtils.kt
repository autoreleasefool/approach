package ca.josephroque.bowlingcompanion.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility methods for date manipulation and display.
 */
object DateUtils {

    // MARK: DateUtils

    /**
     * Convert a series date [String] to a [Date].
     *
     * @param seriesDate date to format
     * @return a date object
     */
    fun seriesDateToDate(seriesDate: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
        return formatter.parse(seriesDate)
    }

    /**
     * Convert a [Date] to a series date [String].
     *
     * @param date date to format
     * @return a string suitable for a series
     */
    fun dateToSeriesDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
        return formatter.format(date)
    }

    /**
     * Converts [Date] to a cleaner format.
     *
     * @param date date to format
     * @return prettier format of string with full month name
     */
    fun dateToPretty(date: Date): String {
        val formatter = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA)
        return formatter.format(date)
    }

    /**
     * Converts [Date] to a shorter format.
     *
     * @param date date to format
     * @return shorter format of string with month and day
     */
    fun dateToShort(date: Date): String {
        val formatter = SimpleDateFormat("MM/dd", Locale.CANADA)
        return formatter.format(date)
    }
}
