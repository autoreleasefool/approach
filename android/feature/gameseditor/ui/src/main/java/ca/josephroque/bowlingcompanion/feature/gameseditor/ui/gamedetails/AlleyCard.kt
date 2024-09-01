package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.model.ui.LaneRow
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.NavigationButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.SectionHeader
import java.util.UUID

@Composable
internal fun AlleyCard(
	state: GameDetailsUiState.AlleyCardUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		SectionHeader(
			title = stringResource(R.string.game_editor_alley_title),
			subtitle = stringResource(R.string.game_editor_alley_description),
			modifier = Modifier.padding(bottom = 8.dp),
		)
		
		NavigationButton(
			title = stringResource(R.string.game_editor_alley_alley),
			subtitle = state.selectedAlley?.name ?: stringResource(R.string.game_editor_alley_no_alley),
			onClick = { onAction(GameDetailsUiAction.ManageAlleyClicked) },
			icon = {
				Icon(
					painter = painterResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_alley,
					),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
				)
			},
		)

		if (state.selectedAlley != null) {
			SectionHeader(
				title = stringResource(R.string.game_editor_lanes),
				subtitle = stringResource(R.string.game_editor_lanes_description),
				modifier = Modifier.padding(top = 8.dp),
				action = {
					RoundIconButton(onClick = { onAction(GameDetailsUiAction.ManageLanesClicked) }) {
						Icon(
							Icons.Default.Edit,
							contentDescription = stringResource(
								ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_manage,
							),
							tint = MaterialTheme.colorScheme.onSurface,
						)
					}
				},
			)

			if (state.selectedLanes.isNotEmpty()) {
				Spacer(modifier = modifier.height(8.dp))
			}

			state.selectedLanes.forEach { lane ->
				LaneRow(
					label = lane.label,
					position = lane.position,
					modifier = Modifier.padding(vertical = 8.dp),
				)
			}
		}
	}
}

@Preview
@Composable
private fun AlleyCardPreview() {
	Surface {
		AlleyCard(
			state = GameDetailsUiState.AlleyCardUiState(
// 				selectedAlley = null,
				selectedAlley = AlleyDetails(
					id = AlleyID.randomID(),
					name = "Grandview Lanes",
					material = null,
					pinFall = null,
					mechanism = null,
					pinBase = null,
				),
// 				selectedLanes = emptyList(),
				selectedLanes = listOf(
					LaneListItem(
						id = UUID.randomUUID(),
						label = "1",
						position = LanePosition.LEFT_WALL,
					),
					LaneListItem(
						id = UUID.randomUUID(),
						label = "2",
						position = LanePosition.NO_WALL,
					),
				),
			),
			onAction = {},
		)
	}
}
