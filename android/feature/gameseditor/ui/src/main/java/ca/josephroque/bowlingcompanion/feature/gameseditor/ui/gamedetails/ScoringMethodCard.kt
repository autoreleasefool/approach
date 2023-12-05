package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.NavigationButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.SectionHeader

@Composable
internal fun ScoringMethodCard(
	state: GameDetailsUiState.ScoringMethodCardUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		SectionHeader(
			title = stringResource(R.string.game_editor_scoring_method_title),
			subtitle = stringResource(R.string.game_editor_scoring_method_subtitle),
			action = {
				RoundIconButton(onClick = { onAction(GameDetailsUiAction.ManageScoreClicked) }) {
					Icon(
						Icons.Default.Edit,
						contentDescription = stringResource(RCoreDesign.string.action_manage),
						tint = MaterialTheme.colorScheme.onSurface,
					)
				}
			},
			modifier = Modifier.padding(bottom = 16.dp),
		)

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			NavigationButton(
				title = stringResource(when (state.scoringMethod) {
					GameScoringMethod.MANUAL -> R.string.scoring_method_manual
					GameScoringMethod.BY_FRAME -> R.string.scoring_method_frame_by_frame
				}),
				icon = {
					Icon(
						painter = painterResource(when (state.scoringMethod) {
							GameScoringMethod.MANUAL -> R.drawable.ic_manual_scoring
							GameScoringMethod.BY_FRAME -> RCoreDesign.drawable.ic_bowling_ball
						}),
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier.size(24.dp),
					)
				},
				onClick = { onAction(GameDetailsUiAction.ManageScoreClicked) },
				trailingIcon = {
					Text(
						text = state.score.toString(),
						style = MaterialTheme.typography.bodyMedium,
						fontWeight = FontWeight.ExtraBold,
						fontSize = 24.sp,
					)
				}
			)
		}
	}
}

@Preview
@Composable
private fun ScoringMethodCardPreview() {
	Surface {
		ScoringMethodCard(
			state = GameDetailsUiState.ScoringMethodCardUiState(
				score = 234,
				scoringMethod = GameScoringMethod.BY_FRAME,
			),
			onAction = {},
		)
	}
}