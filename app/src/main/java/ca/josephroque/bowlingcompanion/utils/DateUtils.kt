package ca.josephroque.bowlingcompanion.utils

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Utility methods for date manipulation and display.
 */
object DateUtils {

    /**
     * Given an integer from 1-12, returns a 3-4 character string representing that month.
     *
     * @param month value representing one of 12 months
     * @return 3-4 character string representing month, or null if month is not between 1 and 12 (inclusive).
     */
    private fun intToMonthCompact(month: Int): String? {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "Aug"
            9 -> "Sept"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> null
        }
    }

    /**
     * Returns an index from 1-12 representing the month which was passed.
     *
     * @param strMonth string containing at least first 3 letters of a month
     * @return index from 1-12 if strMonth represents a month, -1 otherwise
     */
    private fun monthToInt(strMonth: String): Int {
        return when {
            strMonth.startsWith("Jan") -> 1
            strMonth.startsWith("Feb") -> 2
            strMonth.startsWith("Mar") -> 3
            strMonth.startsWith("Apr") -> 4
            strMonth.startsWith("May") -> 5
            strMonth.startsWith("Jun") -> 6
            strMonth.startsWith("Jul") -> 7
            strMonth.startsWith("Aug") -> 8
            strMonth.startsWith("Sep") -> 9
            strMonth.startsWith("Oct") -> 10
            strMonth.startsWith("Nov") -> 11
            strMonth.startsWith("Dec") -> 12
            else -> -1
        }
    }

    /**
     * Converts a string containing a pretty formatted date into numerical values representing month, day, year (in that
     * order).
     *
     * @param pretty string formatted by formattedDateToPrettyCompact
     * @return month, day, year of pretty (in that order)
     */
    fun prettyCompactToFormattedDate(pretty: String): IntArray {
        val date = IntArray(3)
        if (pretty.matches("[A-Z]\\w{2,3} \\d{1,2}, \\d{4}".toRegex())) {
            date[0] = monthToInt(pretty.substring(0, 3))
            date[1] = Integer.parseInt(pretty.substring(pretty.indexOf(" ") + 1, pretty.indexOf(",")))
            date[2] = Integer.parseInt(pretty.substring(pretty.indexOf(",") + 2))
        } else {
            throw IllegalArgumentException("String must match the pattern: [A-Z]\\w{2,3} \\d{1,2}, \\d{4}")
        }

        return date
    }

    /**
     * Converts string of the form "YYYY-mm-DD" to a cleaner format, where YYYY stands for the year, mm stands for the
     * month and DD stands for the day.
     *
     * @param formattedDate formatted string to be converted
     * @return prettier format of string with full month name
     */
    fun formattedDateToPrettyCompact(formattedDate: String): String {
        val pretty = formattedDate.substring(0, 10)

        if (pretty.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            val month = Integer.parseInt(pretty.substring(5, 7))
            val year = pretty.substring(0, 4)
            val day = pretty.substring(8)

            return "${intToMonthCompact(month)} $day, $year"
        } else {
            throw IllegalArgumentException("String must be formatted as YYYY-mm-DD")
        }
    }
}
