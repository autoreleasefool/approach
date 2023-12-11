package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.bottomBorder
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.endBorder
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.startBorder
import ca.josephroque.bowlingcompanion.core.model.ScoringFrame
import ca.josephroque.bowlingcompanion.core.model.ScoringGame
import ca.josephroque.bowlingcompanion.core.model.ScoringRoll
import ca.josephroque.bowlingcompanion.core.model.isFirstFrame
import ca.josephroque.bowlingcompanion.core.model.isFirstRoll
import ca.josephroque.bowlingcompanion.core.model.isLastRoll
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ScoreSheet(
	state: ScoreSheetUiState,
	onAction: (ScoreSheetUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val listState = rememberLazyListState()
	val coroutineScope = rememberCoroutineScope()

	LaunchedEffect(state.selection, state.game?.frames?.size) {
		coroutineScope.launch {
			listState.animateScrollToItem(index = state.selection.frameIndex)
		}
	}

	LazyRow (
		state = listState,
		modifier = modifier
			.clip(RoundedCornerShape(16.dp)),
	) {
		items(
			items = state.game?.frames ?: emptyList(),
			key = { it.index },
		) { frame ->
			val isFrameSelected = state.selection.frameIndex == frame.index

			Column(
				modifier = Modifier.fillParentMaxWidth(0.33f),
			) {
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
					style = state.configuration.style,
					isSelected = isFrameSelected,
					modifier = Modifier.fillMaxWidth(),
				) {
					onAction(ScoreSheetUiAction.FrameClicked(frame.index))
				}
			}
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
		modifier = modifier
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
					if (isSelected)
						style.backgroundHighlightColor
					else
						style.backgroundColor
				)
			)
			.bottomBorder(2.dp, colorResource(style.borderColor))
			.then(
				if (roll.isLastRoll()) Modifier else Modifier.endBorder(
					2.dp,
					colorResource(style.borderColor)
				)
			)
			.then(
				if (!isFirstFrame && roll.isFirstRoll()) Modifier.startBorder(
					2.dp,
					colorResource(style.borderColor)
				) else Modifier
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
				}
			),
			modifier = Modifier.align(Alignment.Center),
		)
	}
}

@Composable
private fun FrameCell(
	modifier: Modifier = Modifier,
	frame: ScoringFrame,
	style: ScoreSheetConfiguration.Style,
	isSelected: Boolean,
	onClick: () -> Unit,
) {
	Column(
		modifier = modifier
			.clickable(onClick = onClick)
			.background(
				colorResource(
					if (isSelected)
						style.backgroundHighlightColor
					else
						style.backgroundColor
				)
			)
			.then(
				if (frame.isFirstFrame()) Modifier else Modifier.startBorder(
					2.dp,
					colorResource(style.borderColor)
				)
			),
	) {
		Text(
			text = frame.display ?: " ",
			style = MaterialTheme.typography.bodyMedium,
			textAlign = TextAlign.Center,
			color = colorResource(if (isSelected)
				style.textHighlightColorOnBackground
			else
				style.textColorOnBackground
			),
			modifier = Modifier
				.padding(vertical = 8.dp)
				.align(Alignment.CenterHorizontally),
		)

		RailCell(
			frameIndex = frame.index,
			style = style,
			isSelected = isSelected,
			modifier = Modifier.fillMaxWidth(),
		)
	}
}

@Composable
private fun RailCell(
	modifier: Modifier = Modifier,
	frameIndex: Int,
	isSelected: Boolean,
	style: ScoreSheetConfiguration.Style,
) {
	Box(
		modifier = modifier
			.background(
				colorResource(
					if (isSelected)
						style.railBackgroundHighlightColor
					else
						style.railBackgroundColor
				)
			)
			.then(
				if (frameIndex == 0) Modifier else Modifier.startBorder(
					2.dp,
					colorResource(style.borderColor)
				)
			)
			.padding(vertical = 4.dp),
	) {
		Text(
			text = (frameIndex + 1).toString(),
			style = MaterialTheme.typography.bodySmall,
			color = colorResource(if (isSelected)
				style.textHighlightColorOnRail
			else
				style.textColorOnRail
			),
			modifier = Modifier.align(Alignment.Center),
		)
	}
}

@Preview
@Composable
private fun ScoreSheetPreview() {
	Surface {
		ScoreSheet(
			state = ScoreSheetUiState(
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
				)
			),
			onAction = {},
		)
	}
}