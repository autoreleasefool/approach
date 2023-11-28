package ca.josephroque.bowlingcompanion.core.common.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.until
import java.time.format.DateTimeFormatter

fun monthDays(year: Int, month: Month): Int {
	val start = LocalDate(year, month, 1)
	val end = start.plus(value = 1, unit = DateTimeUnit.MONTH)
	return start.until(end, DateTimeUnit.DAY)
}

fun firstDayOfMonth(year: Int, month: Month): DayOfWeek {
	val start = LocalDate(year, month, 1)
	return start.dayOfWeek
}

fun LocalDate.format(format: String): String =
	DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDate())

fun LocalDate.simpleFormat(): String =
	this.format("MMMM d, yyyy")