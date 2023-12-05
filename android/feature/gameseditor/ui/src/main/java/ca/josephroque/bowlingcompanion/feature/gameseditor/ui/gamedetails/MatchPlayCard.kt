package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.ui.contentDescription
import ca.josephroque.bowlingcompanion.core.model.ui.icon
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.NavigationButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.SectionHeader

@Composable
internal fun MatchPlayCard(
	state: GameDetailsUiState.MatchPlayCardUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		SectionHeader(
			title = stringResource(R.string.game_editor_match_play_title),
			subtitle = stringResource(R.string.game_editor_match_play_subtitle),
			action = {
				RoundIconButton(onClick = { onAction(GameDetailsUiAction.ManageMatchPlayClicked) }) {
					Icon(
						Icons.Default.Edit,
						contentDescription = stringResource(RCoreDesign.string.action_manage),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}
			},
			modifier = Modifier.padding(bottom = 16.dp),
		)

		NavigationButton(
			title = stringResource(R.string.game_editor_match_play_opponent),
			subtitle = state.opponentName ?: stringResource(R.string.game_editor_match_play_no_opponent),
			onClick = { onAction(GameDetailsUiAction.ManageMatchPlayClicked) },
			icon = {
				Icon(
					painter = painterResource(if (state.opponentName == null) R.drawable.ic_person_none else R.drawable.ic_person),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		)

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.height(IntrinsicSize.Min)
				.fillMaxWidth()
				.padding(top = 16.dp),
		) {
			NavigationButton(
				title = stringResource(R.string.game_editor_match_play_result),
				subtitle = state.result?.contentDescription() ?: stringResource(R.string.game_editor_match_play_result_not_recorded),
				onClick = { onAction(GameDetailsUiAction.ManageMatchPlayClicked) },
				icon = {
					Icon(
						painter = state.result.icon(),
						contentDescription = state.result.contentDescription(),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				},
				modifier = Modifier.weight(1f).fillMaxHeight(),
			)

			NavigationButton(
				title = stringResource(R.string.game_editor_match_play_score),
				subtitle = state.opponentScore?.toString() ?: stringResource(R.string.game_editor_match_play_score_not_recorded),
				onClick = { onAction(GameDetailsUiAction.ManageMatchPlayClicked) },
				modifier = Modifier.weight(1f).fillMaxHeight(),
			)
		}
	}
}

@Composable
private fun Label(
	modifier: Modifier = Modifier,
	title: String,
	value: String?,
	placeholder: String,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
	Column(
		horizontalAlignment = horizontalAlignment,
		modifier = modifier,
	) {
		if (value == null) {
			Text(
				text = placeholder,
				style = MaterialTheme.typography.bodyLarge,
				fontStyle = FontStyle.Italic,
			)
		} else {
			Text(
				text = title,
				style = MaterialTheme.typography.labelSmall,
			)

			Text(
				text = value,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}

@Preview
@Composable
private fun MatchPlayCardPreview() {
	Surface {
		MatchPlayCard(
			state = GameDetailsUiState.MatchPlayCardUiState(
				opponentName = "Joseph",
				opponentScore = 250,
				result = MatchPlayResult.WON,
			),
			onAction = {},
		)
	}
}