package ca.josephroque.bowlingcompanion.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

fun monthDays(year: Int, month: Month): Int {
	val start = LocalDate(year, month, 1)
	val end = start.plus(value = 1, unit = DateTimeUnit.MONTH)
	return start.until(end, DateTimeUnit.DAY)
}

fun firstDayOfMonth(year: Int, month: Month): DayOfWeek {
	val start = LocalDate(year, month, 1)
	return start.dayOfWeek
}

fun Instant.toLocalDate(): LocalDate =
	toLocalDateTime(TimeZone.currentSystemDefault()).date