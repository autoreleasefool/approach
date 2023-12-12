package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
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
		// TODO: Implement ball rolled
		Spacer(modifier = Modifier.weight(1f))

		AssistChip(
			onClick = { onAction(RollEditorUiAction.FoulToggled(!state.didFoulRoll)) },
			colors = if (state.didFoulRoll) {
				AssistChipDefaults.assistChipColors(
					labelColor = Color.White,
					containerColor = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive).copy(alpha = 0.7f),
				)
			} else {
	 			AssistChipDefaults.assistChipColors(
					labelColor = Color.White,
			  )
		  },
			border = if (state.didFoulRoll) {
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
				if (state.didFoulRoll) {
					Icon(
						Icons.Default.Check,
						contentDescription = null,
						tint = Color.White,
					)
				}
			}
		)
	}
}

@Preview
@Composable
private fun RollEditorPreview() {
	Surface(color = Color.Black) {
		val balls = listOf(
			GearListItem(
				id = UUID.randomUUID(),
				name = "Red",
				kind = GearKind.BOWLING_BALL,
				ownerName = null,
				avatar = Avatar.default()
			),
			GearListItem(
				id = UUID.randomUUID(),
				name = "Red",
				kind = GearKind.BOWLING_BALL,
				ownerName = null,
				avatar = Avatar.default()
			),
		)

		RollEditor(
			state = RollEditorUiState(
				recentBalls = balls,
				selectedBall = balls.first().id,
				didFoulRoll = true,
			),
			onAction = {},
		)
	}
}