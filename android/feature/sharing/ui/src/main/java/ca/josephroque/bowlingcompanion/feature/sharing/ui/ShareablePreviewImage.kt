package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.HorizontalShareableGamesImage
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.ShareableSeriesImage

@Composable
fun ShareablePreviewImage(state: SharingData, modifier: Modifier = Modifier) {
	when (state) {
		is SharingData.Series -> ShareableSeriesImage(
			series = state.series,
			configuration = state.configuration,
			modifier = modifier,
		)
		is SharingData.Games -> HorizontalShareableGamesImage(
			games = state.games,
			configuration = state.configuration,
			modifier = modifier,
		)
		SharingData.Statistic -> TODO()
		SharingData.TeamSeries -> TODO()
	}
}
