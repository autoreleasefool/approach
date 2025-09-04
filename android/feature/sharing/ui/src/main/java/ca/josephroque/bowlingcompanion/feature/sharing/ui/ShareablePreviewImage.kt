package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.ShareableGamesImage
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.ShareableSeriesImage

@Composable
fun ShareablePreviewImage(state: SharingData, graphicsLayer: GraphicsLayer, modifier: Modifier = Modifier) {
	when (state) {
		is SharingData.Series -> ShareableSeriesImage(
			series = state.series,
			configuration = state.configuration,
			graphicsLayer = graphicsLayer,
			modifier = modifier,
		)
		is SharingData.Games -> ShareableGamesImage(
			state = state,
			modifier = modifier,
			graphicsLayer = graphicsLayer,
		)
		SharingData.Statistic -> TODO()
		SharingData.TeamSeries -> TODO()
	}
}
