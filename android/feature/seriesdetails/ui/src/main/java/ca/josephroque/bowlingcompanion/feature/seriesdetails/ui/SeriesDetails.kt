package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.charts.ui.SeriesHeader
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesList

@Composable
fun SeriesDetails(
	state: SeriesDetailsUiState,
	onAction: (SeriesDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	GamesList(
		state = state.gamesList,
		onAction = { onAction(SeriesDetailsUiAction.GamesList(it)) },
		modifier = modifier,
		header = {
			SeriesHeader(
				preBowl = state.details.preBowl,
				preBowledDate = state.details.appliedDate?.let { state.details.date },
				numberOfGames = state.details.numberOfGames,
				seriesTotal = state.details.total,
				scores = state.scores,
				seriesLow = state.seriesLow,
				seriesHigh = state.seriesHigh,
				isShowingPlaceholder = state.isShowingPlaceholder,
				modifier = Modifier.padding(bottom = 8.dp),
			)
		},
	)
}
