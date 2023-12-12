package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton

@Composable
internal fun Header(
	state: GameDetailsUiState.HeaderUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
	) {
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

		RoundIconButton(
			onClick = {
				state.nextElement ?: return@RoundIconButton
				onAction(GameDetailsUiAction.NextGameElementClicked(state.nextElement))
			},
			modifier = Modifier.alpha(if (state.nextElement != null) 1f else 0f),
		) {
			Icon(
				painter = painterResource(R.drawable.ic_chevron_right),
				contentDescription = when (state.nextElement) {
					is NextGameEditableElement.Roll -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_roll, state.nextElement.rollIndex + 1)
					is NextGameEditableElement.Frame -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_frame, state.nextElement.frameIndex + 1)
					is NextGameEditableElement.Game -> stringResource(ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R.string.game_editor_next_game, state.nextElement.gameIndex + 1)
					null -> null
				},
				tint = MaterialTheme.colorScheme.onSurface,
			)
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