package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import java.util.UUID

@Composable
fun GamesEditor(
	modifier: Modifier = Modifier,
	frameEditorState: FrameEditorUiState,
	rollEditorState: RollEditorUiState,
	onDownedPinsChanged: (Set<Pin>) -> Unit,
	onSelectBall: (UUID) -> Unit,
	onToggleFoul: (Boolean) -> Unit,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
	) {
		BackgroundImage()

		BlackCover()

		Column(
			modifier = Modifier.fillMaxSize(),
		) {
			Spacer(modifier = Modifier.weight(1f))

			FrameEditor(
				frameEditorState = frameEditorState,
				onDownedPinsChanged = onDownedPinsChanged,
				modifier = Modifier.padding(horizontal = 16.dp),
			)

			RollEditor(
				rollEditorState = rollEditorState,
				onSelectBall = onSelectBall,
				onToggleFoul = onToggleFoul,
			)

			Spacer(modifier = Modifier.weight(1f))
		}
	}
}

@Composable
private fun BlackCover() {
	val colorStops = arrayOf(
		0.0f to Color.Black,
		0.125f to Color.Black.copy(alpha = 0.3f),
		0.4f to Color.Black.copy(alpha = 0.5f),
		0.5f to Color.Black,
		0.57f to Color.Black.copy(alpha = 0.3f),
		0.875f to Color.Black.copy(alpha = 0.6f),
		1.0f to Color.Black,
	)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Brush.verticalGradient(colorStops = colorStops))
	)
}

@Composable
private fun BackgroundImage() {
	Column(
		modifier = Modifier.fillMaxSize(),
	) {

		Image(
			painter = painterResource(R.drawable.galaxy_pin),
			contentDescription = null,
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		)

		Image(
			painter = painterResource(R.drawable.wood_alley),
			contentDescription = null,
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f),
		)
	}
}

@Preview
@Composable
private fun GamesEditorPreview() {
	GamesEditor(
		frameEditorState = FrameEditorUiState.Edit(
			downedPins = setOf(),
			lockedPins = setOf(),
		),
		rollEditorState = RollEditorUiState.Edit(
			recentBalls = listOf(),
			selectedBall = null,
			didFoulRoll = false,
		),
		onDownedPinsChanged = {},
		onSelectBall = {},
		onToggleFoul = {},
	)
}