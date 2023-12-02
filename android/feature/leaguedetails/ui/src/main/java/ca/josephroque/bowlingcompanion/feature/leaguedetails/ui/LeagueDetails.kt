package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
	)
}