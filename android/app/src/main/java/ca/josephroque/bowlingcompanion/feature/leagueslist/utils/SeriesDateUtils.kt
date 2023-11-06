package ca.josephroque.bowlingcompanion.feature.leagueslist.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import kotlinx.datetime.LocalDate

@Composable
fun LocalDate?.seriesDate(recurrence: LeagueRecurrence): String = if (this == null) {
	stringResource(R.string.league_list_item_series_missing)
} else {
	when (recurrence) {
		LeagueRecurrence.ONCE -> stringResource(
			R.string.league_list_item_bowled_once,
			this.format("MMMM d, yyyy")
		)
		LeagueRecurrence.REPEATING -> stringResource(
			R.string.league_list_item_latest_series,
			this.format("MMMM d, yyyy")
		)
	}
}
