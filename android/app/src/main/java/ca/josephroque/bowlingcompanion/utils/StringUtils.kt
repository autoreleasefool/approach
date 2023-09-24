package ca.josephroque.bowlingcompanion.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

fun Double?.formatAsAverage(): String = if (this == null) {
	""
} else {
	val df = DecimalFormat("#.#")
	df.format(this)
}

@Composable
fun Instant?.seriesDate(recurrence: LeagueRecurrence): String = if (this == null) {
	stringResource(R.string.league_list_item_series_missing)
} else {
	when (recurrence) {
		LeagueRecurrence.ONCE -> stringResource(
			R.string.league_list_item_bowled_once,
			this.toLocalDateTime(TimeZone.currentSystemDefault())
				.format("MMMM d, yyyy")
		)
		LeagueRecurrence.REPEATING -> stringResource(
			R.string.league_list_item_latest_series,
			this.toLocalDateTime(TimeZone.currentSystemDefault())
				.format("MMMM d, yyyy")
		)
	}
}
fun LocalDateTime.format(format: String): String =
	DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())