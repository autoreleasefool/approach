package ca.josephroque.bowlingcompanion.core.common.utils

import java.time.format.DateTimeFormatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalDate(): LocalDate = toLocalDateTime(TimeZone.currentSystemDefault()).date

fun LocalDate.format(format: String): String =
	DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDate())

fun LocalDate.simpleFormat(): String = this.format("MMMM d, yyyy")
