package ca.josephroque.bowlingcompanion.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility methods for date manipulation and display.
 */
object DateUtils {

    /** Date formatter for series dates. */
    private val seriesDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)

    /** Date formatter for pretty dates. */
    private val prettyDateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.CANADA)

    /**
     * Convert a series date [String] to a [Date].
     *
     * @param seriesDate date to format
     * @return a date object
     */
    fun seriesDateToDate(seriesDate: String): Date {
        return seriesDateFormatter.parse(seriesDate)
    }

    /**
     * Convert a [Date] to a series date [String].
     *
     * @param date date to format
     * @return a string suitable for a series
     */
    fun dateToSeriesDate(date: Date): String {
        return seriesDateFormatter.format(date)
    }

    /**
     * Converts [Date] to a cleaner format.
     *
     * @param date date to format
     * @return prettier format of string with full month name
     */
    fun dateToPretty(date: Date): String {
        return prettyDateFormatter.format(date)
    }
}
