package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingData

@Composable
fun ShareableGamesImage(state: SharingData.Games, graphicsLayer: GraphicsLayer, modifier: Modifier = Modifier) {
	when (state.configuration.layout) {
		GamesSharingConfigurationUiState.Layout.Vertical -> {
			val game = state.games.firstOrNull { state.configuration.singleGame?.id == it.id }
				?: state.games.firstOrNull { state.configuration.includeGame(it) }
			game?.let {
				VerticalShareableGamesImage(
					game = it,
					configuration = state.configuration,
					graphicsLayer = graphicsLayer,
					modifier = modifier,
				)
			}
		}
		GamesSharingConfigurationUiState.Layout.Horizontal ->
			HorizontalShareableGamesImage(
				games = state.games,
				configuration = state.configuration,
				graphicsLayer = graphicsLayer,
				modifier = modifier,
			)
	}
}
