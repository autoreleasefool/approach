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
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheet
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditor
import java.util.UUID

@Composable
fun GamesEditor(
	state: GamesEditorUiState,
	onAction: (GamesEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.scoreEditor?.let { scoreEditor ->
		ScoreEditor(
			state = scoreEditor,
			onAction = { onAction(GamesEditorUiAction.ScoreEditor(it)) },
		)
	}

	Column(modifier = modifier.fillMaxSize()) {
		Box(modifier = modifier.weight(1f)) {
			BackgroundImage()

			BlackCover()

			Column(
				modifier = Modifier.fillMaxSize(),
			) {
				Spacer(modifier = Modifier.weight(1f))

				FrameEditor(
					state = state.frameEditor,
					onAction = { onAction(GamesEditorUiAction.FrameEditor(it)) },
					modifier = Modifier.padding(horizontal = 16.dp),
				)

				Spacer(modifier = Modifier.weight(1f))
			}
		}

		Box(modifier = Modifier.background(Color.Black)) {
			RollEditor(
				state = state.rollEditor,
				onAction = { onAction(GamesEditorUiAction.RollEditor(it)) },
			)

			ScoreSheet(
				state = state.scoreSheet,
				onAction = { onAction(GamesEditorUiAction.ScoreSheet(it)) },
				modifier = Modifier
					.padding(horizontal = 8.dp)
					.padding(top = 8.dp, bottom = 16.dp),
			)
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
		state = GamesEditorUiState(
			frameEditor = FrameEditorUiState(
				downedPins = setOf(),
				lockedPins = setOf(),
			),
			rollEditor = RollEditorUiState(
				recentBalls = emptyList(),
				selectedBall = null,
				didFoulRoll = false,
			),
			scoreSheet = ScoreSheetUiState(
				configuration = ScoreSheetConfiguration(ScoreSheetConfiguration.Style.PLAIN),
				selection = ScoreSheetUiState.Selection(frameIndex = 0, rollIndex = 0),
				game = ScoringGame(
					id = UUID.randomUUID(),
					index = 0,
					frames = listOf(
						ScoringFrame(
							index = 0,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "A"),
								ScoringRoll(index = 1, didFoul = false, display = "2"),
								ScoringRoll(index = 2, didFoul = false, display = "2"),
							),
							score = 0,
						),
						ScoringFrame(
							index = 1,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "HS"),
								ScoringRoll(index = 1, didFoul = false, display = "5"),
								ScoringRoll(index = 2, didFoul = false, display = "2"),
							),
							score = 0,
						),
						ScoringFrame(
							index = 2,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "12"),
								ScoringRoll(index = 1, didFoul = false, display = "/"),
								ScoringRoll(index = 2, didFoul = false, display = "12"),
							),
							score = 12,
						),
						ScoringFrame(
							index = 3,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "12"),
								ScoringRoll(index = 1, didFoul = false, display = "/"),
								ScoringRoll(index = 2, didFoul = false, display = "10"),
							),
							score = 22,
						),
						ScoringFrame(
							index = 4,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O"),
								ScoringRoll(index = 1, didFoul = false, display = "-"),
								ScoringRoll(index = 2, didFoul = false, display = "5"),
							),
							score = 37,
						),
						ScoringFrame(
							index = 5,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O"),
								ScoringRoll(index = 1, didFoul = false, display = "/"),
								ScoringRoll(index = 2, didFoul = false, display = "10"),
							),
							score = 62,
						),
						ScoringFrame(
							index = 6,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O"),
								ScoringRoll(index = 1, didFoul = false, display = "-"),
								ScoringRoll(index = 2, didFoul = false, display = "5"),
							),
							score = 77,
						),
						ScoringFrame(
							index = 7,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "HS"),
								ScoringRoll(index = 1, didFoul = false, display = "/"),
								ScoringRoll(index = 2, didFoul = false, display = "11"),
							),
							score = 103,
						),
						ScoringFrame(
							index = 8,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "A"),
								ScoringRoll(index = 1, didFoul = false, display = "/"),
								ScoringRoll(index = 2, didFoul = false, display = "15"),
							),
							score = 133,
						),
						ScoringFrame(
							index = 9,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "X"),
								ScoringRoll(index = 1, didFoul = false, display = "HS"),
								ScoringRoll(index = 2, didFoul = false, display = "/"),
							),
							score = 163,
						),
					),
				),
			),
			scoreEditor = null,
		),
		onAction = {},
	)
}