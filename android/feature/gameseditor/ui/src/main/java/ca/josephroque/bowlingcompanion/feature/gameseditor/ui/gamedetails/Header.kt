package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailRow

@Composable
internal fun Header(
	state: GameDetailsUiState.HeaderUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	DetailRow(modifier = modifier.padding(bottom = 8.dp)) {
		Column(
			horizontalAlignment = Alignment.Start,
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = state.bowlerName,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Black,
			)
			Text(
				text = state.leagueName,
				style = MaterialTheme.typography.bodyMedium
			)
		}

		if (state.nextElement != null) {
			RoundIconButton(onClick = { onAction(GameDetailsUiAction.NextGameElementClicked(state.nextElement)) }) {
				Icon(
					painter = painterResource(R.drawable.ic_chevron_right),
					contentDescription = when (state.nextElement) {
						is NextGameEditableElement.Roll -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_roll, state.nextElement.rollIndex + 1)
						is NextGameEditableElement.Frame -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_frame, state.nextElement.frameIndex + 1)
						is NextGameEditableElement.Game -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_game, state.nextElement.gameIndex + 1)
					},
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	}
}



@Preview
@Composable
private fun HeaderPreview() {
	Surface {
		Header(
			state = GameDetailsUiState.HeaderUiState(
				bowlerName = "Joseph",
				leagueName = "Majors 22/23",
				nextElement = null
			),
			onAction = {},
		)
	}
}