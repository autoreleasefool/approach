package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesList

@Composable
fun LeagueDetails(
	state: LeagueDetailsUiState,
	onAction: (LeagueDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	SeriesList(
		state = state.seriesList,
		onAction = { onAction(LeagueDetailsUiAction.SeriesList(it)) },
		modifier = modifier,
		header = {
			Text(
				text = stringResource(R.string.series_list_title),
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)
		}
	)
}