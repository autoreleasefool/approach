package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.ui.AvatarImage
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import java.util.UUID

@Composable
fun RollEditor(
	state: RollEditorUiState,
	onAction: (RollEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
	) {
		BallPicker(
			balls = state.recentBalls,
			selectedBall = state.selectedBall,
			onBallSelected = { onAction(RollEditorUiAction.BallClicked(it)) },
			onEmptySlotSelected = { onAction(RollEditorUiAction.PickBallClicked) },
			modifier = Modifier.weight(1f),
		)

		FoulChip(
			isEnabled = state.didFoulRoll,
			onClick = { onAction(RollEditorUiAction.FoulToggled(!state.didFoulRoll)) },
		)
	}
}

@Composable
private fun BallPicker(
	balls: List<FrameEdit.Gear>,
	selectedBall: FrameEdit.Gear?,
	onBallSelected: (FrameEdit.Gear) -> Unit,
	onEmptySlotSelected: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		val selectableBalls = remember(balls, selectedBall) {
			if (selectedBall != null && !balls.contains(selectedBall)) {
				balls.plus(selectedBall).sortedBy { it.name }
			} else {
				balls
			}
		}

		if (selectableBalls.isNotEmpty()) {
			selectableBalls.forEachIndexed { index, ball ->
				AvatarImage(
					avatar = ball.avatar,
					modifier = Modifier
						.clickable(onClick = { onBallSelected(ball) })
						.padding(start = if (index == 0) 0.dp else 8.dp)
						.border(
							width = 2.dp,
							color = if (ball.id == selectedBall?.id) Color.White else Color.Transparent,
							shape = CircleShape,
						)
						.size(32.dp),
				)
			}
		}
		
		IconButton(onClick = onEmptySlotSelected) {
			Icon(
				painter = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_circle),
				contentDescription = stringResource(R.string.cd_select_ball),
				modifier = Modifier.size(32.dp),
				tint = Color.White.copy(alpha = 0.7f),
			)
		}
	}
}

@Composable
private fun FoulChip(
	isEnabled: Boolean,
	onClick: () -> Unit,
) {
	AssistChip(
		onClick = onClick,
		colors = if (isEnabled) {
			AssistChipDefaults.assistChipColors(
				labelColor = Color.White,
				containerColor = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive).copy(alpha = 0.7f),
			)
		} else {
			AssistChipDefaults.assistChipColors(
				labelColor = Color.White,
			)
		},
		border = if (isEnabled) {
			AssistChipDefaults.assistChipBorder(
				borderColor = Color.Transparent,//colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
			)
		} else {
			AssistChipDefaults.assistChipBorder(
				borderColor = Color.White,
			)
		},
		label = {
			Text(
				text = stringResource(R.string.game_editor_foul),
				style = MaterialTheme.typography.labelMedium,
			)
		},
		leadingIcon = {
			if (isEnabled) {
				Icon(
					Icons.Default.Check,
					contentDescription = null,
					tint = Color.White,
				)
			}
		}
	)
}

@Preview
@Composable
private fun RollEditorPreview() {
	Surface(color = Color.Black) {
		val balls = listOf(
			FrameEdit.Gear(
				id = UUID.randomUUID(),
				name = "Red",
				kind = GearKind.BOWLING_BALL,
				avatar = Avatar.default()
			),
			FrameEdit.Gear(
				id = UUID.randomUUID(),
				name = "Red",
				kind = GearKind.BOWLING_BALL,
				avatar = Avatar.default()
			),
		)

		RollEditor(
			state = RollEditorUiState(
				recentBalls = balls,
				selectedBall = balls.first(),
				didFoulRoll = true,
			),
			onAction = {},
		)
	}
}