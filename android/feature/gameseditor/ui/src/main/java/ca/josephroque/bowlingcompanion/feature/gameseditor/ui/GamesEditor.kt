package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheet
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.AnimatedFrameEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import java.util.UUID

@Composable
fun GamesEditor(
	state: GamesEditorUiState,
	onAction: (GamesEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxSize()) {
		Box(modifier = Modifier.weight(1f)) {
			BackgroundImage()

			BlackCover()

			Column(
				modifier = Modifier.fillMaxSize(),
			) {
				Spacer(modifier = Modifier.weight(1f))

				if (state.manualScore == null) {
					AnimatedFrameEditor(
						state = state.frameEditor,
						onAction = { onAction(GamesEditorUiAction.FrameEditor(it)) },
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				} else {
					ManualScoreCard(
						score = state.manualScore,
						onAction = onAction,
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				}

				Spacer(modifier = Modifier.weight(1f))
			}
		}

		if (state.manualScore == null) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.background(Color.Black),
			) {
				RollEditor(
					state = state.rollEditor,
					onAction = { onAction(GamesEditorUiAction.RollEditor(it)) },
					modifier = Modifier.padding(horizontal = 16.dp),
				)

				ScoreSheet(
					state = state.scoreSheet,
					onAction = { onAction(GamesEditorUiAction.ScoreSheet(it)) },
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp)
						.height(100.dp)
						.clip(RoundedCornerShape(16.dp)),
				)
			}
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
			.background(Brush.verticalGradient(colorStops = colorStops)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualScoreCard(
	score: Int,
	onAction: (GamesEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
	) {
		Spacer(modifier = Modifier.weight(1f))

		Card(
			onClick = { onAction(GamesEditorUiAction.ManualScoreClicked) },
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier.padding(16.dp),
			) {
				Icon(
					painter = painterResource(R.drawable.ic_manual_scoring),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.size(24.dp),
				)

				Column(
					horizontalAlignment = Alignment.Start,
					verticalArrangement = Arrangement.spacedBy(2.dp),
				) {
					Text(
						text = score.toString(),
						style = MaterialTheme.typography.displayMedium,
						fontWeight = FontWeight.Black,
					)

					Text(
						text = stringResource(R.string.game_editor_score_set_manually),
						style = MaterialTheme.typography.bodyMedium,
						fontStyle = FontStyle.Italic,
					)
				}
			}
		}

		Spacer(modifier = Modifier.weight(1f))
	}
}

@Preview
@Composable
private fun GamesEditorPreview() {
	GamesEditor(
		state = GamesEditorUiState(
			manualScore = 120,
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
								ScoringRoll(index = 0, didFoul = true, display = "A", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "2", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
							),
							score = 0,
						),
						ScoringFrame(
							index = 1,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "HS", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "5", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "2", isSecondaryValue = false),
							),
							score = 0,
						),
						ScoringFrame(
							index = 2,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "12", isSecondaryValue = true),
							),
							score = 12,
						),
						ScoringFrame(
							index = 3,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = true, display = "12", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
							),
							score = 22,
						),
						ScoringFrame(
							index = 4,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
							),
							score = 37,
						),
						ScoringFrame(
							index = 5,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "10", isSecondaryValue = true),
							),
							score = 62,
						),
						ScoringFrame(
							index = 6,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "C/O", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "-", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "5", isSecondaryValue = false),
							),
							score = 77,
						),
						ScoringFrame(
							index = 7,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "HS", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "11", isSecondaryValue = true),
							),
							score = 103,
						),
						ScoringFrame(
							index = 8,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "A", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "/", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "15", isSecondaryValue = true),
							),
							score = 133,
						),
						ScoringFrame(
							index = 9,
							rolls = listOf(
								ScoringRoll(index = 0, didFoul = false, display = "X", isSecondaryValue = false),
								ScoringRoll(index = 1, didFoul = false, display = "HS", isSecondaryValue = false),
								ScoringRoll(index = 2, didFoul = false, display = "/", isSecondaryValue = false),
							),
							score = 163,
						),
					),
				),
			),
			gameId = UUID.randomUUID(),
		),
		onAction = {},
	)
}
