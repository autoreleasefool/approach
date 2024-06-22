package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.bottomBorder
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.endBorder
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.startBorder
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import ca.josephroque.bowlingcompanion.core.model.isFirstFrame
import ca.josephroque.bowlingcompanion.core.model.isFirstRoll
import ca.josephroque.bowlingcompanion.core.model.isLastRoll
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringStub

@Composable
fun ScoreSheet(
	state: ScoreSheetUiState,
	onAction: (ScoreSheetUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier.fillMaxWidth(),
	) {
		val scrollState = rememberScrollState()
		val cellWidth = if (maxWidth >= 600.dp) maxWidth / 5f else maxWidth / 3f
		val targetPositionDp = (state.selection.frameIndex - 1) * (maxWidth.value / 3f)
		val targetPositionPx = with(LocalDensity.current) { targetPositionDp.dp.toPx() }
		LaunchedEffect(state.selection) {
			scrollState.animateScrollTo(targetPositionPx.toInt())
		}

		ScoreSheetRow(
			state = state,
			onAction = onAction,
			cellWidth = cellWidth,
			modifier = Modifier.horizontalScroll(scrollState),
		)
	}
}

@Composable
fun ScoreSheetRow(
	state: ScoreSheetUiState,
	cellWidth: Dp,
	onAction: (ScoreSheetUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(modifier = modifier) {
		if (state.configuration.scorePosition.contains(ScorePosition.START)) {
			ScoreCell(
				score = state.game?.score ?: 0,
				position = ScorePosition.START,
				style = state.configuration.style,
				modifier = Modifier.width(cellWidth),
			)
		}

		state.game?.frames?.forEach { frame ->
			val isFrameSelected = state.selection.frameIndex == frame.index

			Column(
				modifier = Modifier.width(cellWidth),
			) {
				if (state.configuration.framePosition.contains(FramePosition.TOP)) {
					RailCell(
						frameIndex = frame.index,
						style = state.configuration.style,
						isSelected = isFrameSelected,
						includingBottomBorder = true,
						modifier = Modifier.fillMaxWidth(),
					)
				}

				RollCells(
					modifier = Modifier.fillMaxWidth(),
					frame = frame,
					style = state.configuration.style,
					isFrameSelected = isFrameSelected,
					selectedRollIndex = state.selection.rollIndex,
				) {
					onAction(ScoreSheetUiAction.RollClicked(frame.index, it))
				}

				FrameCell(
					frame = frame,
					config = state.configuration,
					isSelected = isFrameSelected,
					previousFrame = state.game.frames.getOrNull(frame.index - 1),
					modifier = Modifier.fillMaxWidth(),
				) {
					onAction(ScoreSheetUiAction.FrameClicked(frame.index))
				}
			}
		}

		if (state.configuration.scorePosition.contains(ScorePosition.END)) {
			ScoreCell(
				score = state.game?.score ?: 0,
				position = ScorePosition.END,
				style = state.configuration.style,
				modifier = Modifier.width(cellWidth),
			)
		}
	}
}

@Composable
private fun RollCells(
	modifier: Modifier = Modifier,
	frame: ScoringFrame,
	style: ScoreSheetConfiguration.Style,
	isFrameSelected: Boolean,
	selectedRollIndex: Int,
	onClick: (rollIndex: Int) -> Unit,
) {
	Row(
		modifier = modifier,
	) {
		frame.rolls.forEach { roll ->
			RollCell(
				roll = roll,
				isSelected = isFrameSelected && selectedRollIndex == roll.index,
				isFirstFrame = frame.isFirstFrame(),
				style = style,
				onClick = { onClick(roll.index) },
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun RollCell(
	modifier: Modifier = Modifier,
	roll: ScoringRoll,
	isSelected: Boolean,
	isFirstFrame: Boolean,
	style: ScoreSheetConfiguration.Style,
	onClick: () -> Unit,
) {
	Box(
		modifier = modifier
			.clickable(onClick = onClick)
			.background(
				colorResource(
					if (isSelected) {
						style.backgroundHighlightColor
					} else {
						style.backgroundColor
					},
				),
			)
			.bottomBorder(2.dp, colorResource(style.borderColor))
			.then(
				if (roll.isLastRoll()) {
					Modifier
				} else {
					Modifier.endBorder(
						2.dp,
						colorResource(style.borderColor),
					)
				},
			)
			.then(
				if (!isFirstFrame && roll.isFirstRoll()) {
					Modifier.startBorder(
						2.dp,
						colorResource(style.borderColor),
					)
				} else {
					Modifier
				},
			)
			.padding(vertical = 8.dp),
	) {
		Text(
			text = roll.display ?: " ",
			style = MaterialTheme.typography.bodySmall,
			maxLines = 1,
			color = colorResource(
				when {
					isSelected && roll.didFoul -> style.textHighlightFoulColorOnBackground
					isSelected && roll.isSecondaryValue -> style.textHighlightSecondaryColorOnBackground
					isSelected -> style.textHighlightColorOnBackground
					!isSelected && roll.didFoul -> style.textFoulColorOnBackground
					!isSelected && roll.isSecondaryValue -> style.textSecondaryColorOnBackground
					else -> style.textColorOnBackground
				},
			),
			modifier = Modifier.align(Alignment.Center),
		)
	}
}

@Composable
private fun FrameCell(
	modifier: Modifier = Modifier,
	frame: ScoringFrame,
	previousFrame: ScoringFrame?,
	config: ScoreSheetConfiguration,
	isSelected: Boolean,
	onClick: () -> Unit,
) {
	Column(
		modifier = modifier
			.clickable(onClick = onClick)
			.background(
				colorResource(
					if (isSelected) {
						config.style.backgroundHighlightColor
					} else {
						config.style.backgroundColor
					},
				),
			)
			.then(
				if (frame.isFirstFrame()) {
					Modifier
				} else {
					Modifier.startBorder(
						2.dp,
						colorResource(config.style.borderColor),
					)
				},
			),
	) {
		Text(
			text = if (isSelected && frame.display == null) {
				previousFrame?.display ?: " "
			} else {
				frame.display ?: " "
			},
			style = MaterialTheme.typography.bodyMedium,
			textAlign = TextAlign.Center,
			color = colorResource(
				if (isSelected) {
					config.style.textHighlightColorOnBackground
				} else {
					config.style.textColorOnBackground
				},
			),
			modifier = Modifier
				.align(Alignment.CenterHorizontally)
				.wrapContentHeight(align = Alignment.CenterVertically)
				.weight(1f),
		)

		if (config.framePosition.contains(FramePosition.BOTTOM)) {
			RailCell(
				frameIndex = frame.index,
				style = config.style,
				isSelected = isSelected,
				includingBottomBorder = false,
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}

@Composable
private fun RailCell(
	modifier: Modifier = Modifier,
	frameIndex: Int,
	isSelected: Boolean,
	includingBottomBorder: Boolean,
	style: ScoreSheetConfiguration.Style,
) {
	Box(
		modifier = modifier
			.background(
				colorResource(
					if (isSelected) {
						style.railBackgroundHighlightColor
					} else {
						style.railBackgroundColor
					},
				),
			)
			.then(
				if (!includingBottomBorder) {
					Modifier
				} else {
					Modifier.bottomBorder(2.dp, colorResource(style.borderColor))
				},
			)
			.then(
				if (frameIndex == 0) {
					Modifier
				} else {
					Modifier.startBorder(
						2.dp,
						colorResource(style.borderColor),
					)
				},
			)
			.padding(vertical = 4.dp),
	) {
		Text(
			text = (frameIndex + 1).toString(),
			style = MaterialTheme.typography.bodySmall,
			color = colorResource(
				if (isSelected) {
					style.textHighlightColorOnRail
				} else {
					style.textColorOnRail
				},
			),
			modifier = Modifier.align(Alignment.Center),
		)
	}
}

@Composable
private fun ScoreCell(
	score: Int,
	position: ScorePosition,
	style: ScoreSheetConfiguration.Style,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.background(colorResource(style.backgroundColor))
			.then(
				when (position) {
					ScorePosition.START -> Modifier.endBorder(4.dp, colorResource(style.borderColor))
					ScorePosition.END -> Modifier.startBorder(4.dp, colorResource(style.borderColor))
				},
			)
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Spacer(modifier = Modifier.weight(1f))

		Text(
			text = stringResource(R.string.scoresheet_total),
			style = MaterialTheme.typography.labelMedium,
			color = colorResource(style.textColorOnBackground),
		)

		Text(
			text = score.toString(),
			style = MaterialTheme.typography.displaySmall,
			fontWeight = FontWeight.Bold,
			color = colorResource(style.textColorOnBackground),
		)

		Spacer(modifier = Modifier.weight(1f))
	}
}

private class FramePositionPreviewParameterProvider : PreviewParameterProvider<FramePosition> {
	override val values = sequenceOf(FramePosition.TOP, FramePosition.BOTTOM)
}

@Preview
@Composable
private fun ScoreSheetPreview(
	@PreviewParameter(FramePositionPreviewParameterProvider::class) framePosition: FramePosition,
) {
	Surface {
		ScoreSheet(
			state = ScoreSheetUiState(
				configuration = ScoreSheetConfiguration(
					framePosition = setOf(framePosition),
					scorePosition = setOf(ScorePosition.START, ScorePosition.END),
					style = ScoreSheetConfiguration.Style.PLAIN,
				),
				selection = ScoreSheetUiState.Selection(frameIndex = -1, rollIndex = -1),
				game = ScoringStub.stub(),
			),
			onAction = {},
			modifier = Modifier.height(100.dp),
		)
	}
}

@Preview
@Composable
private fun ScoreCellPreview() {
	Surface {
		ScoreCell(
			score = 300,
			position = ScorePosition.END,
			style = ScoreSheetConfiguration.Style.PLAIN,
			modifier = Modifier
				.height(100.dp)
				.width(160.dp),
		)
	}
}
