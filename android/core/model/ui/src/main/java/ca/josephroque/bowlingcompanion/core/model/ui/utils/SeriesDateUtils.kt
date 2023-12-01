package ca.josephroque.bowlingcompanion.core.model.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.ui.R
import kotlinx.datetime.LocalDate

@Composable
fun LocalDate?.seriesDate(recurrence: LeagueRecurrence): String = if (this == null) {
	stringResource(R.string.league_series_missing)
} else {
	when (recurrence) {
		LeagueRecurrence.ONCE -> stringResource(
			R.string.league_bowled_once,
			this.simpleFormat()
		)
		LeagueRecurrence.REPEATING -> stringResource(
			R.string.league_latest_series,
			this.simpleFormat()
		)
	}
}
