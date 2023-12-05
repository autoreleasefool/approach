package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailCard

@Composable
internal fun ScoringMethodCard(
	state: GameDetailsUiState.ScoringMethodCardUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	DetailCard(
		title = stringResource(R.string.game_editor_scoring_method_title),
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp),
		) {
			Icon(
				painter = painterResource(when (state.scoringMethod) {
					GameScoringMethod.MANUAL -> R.drawable.ic_manual_scoring
					GameScoringMethod.BY_FRAME -> RCoreDesign.drawable.ic_bowling_ball
				}), 
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(24.dp),
			)
			
			Text(
				text = stringResource(when (state.scoringMethod) {
					GameScoringMethod.MANUAL -> R.string.scoring_method_manual
					GameScoringMethod.BY_FRAME -> R.string.scoring_method_frame_by_frame
				}),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.weight(1f),
			)
			
			Text(
				text = state.score.toString(),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.ExtraBold,
				fontSize = 24.sp,
			)
		}

		TextButton(onClick = { onAction(GameDetailsUiAction.ManageScoreClicked) }) {
			Text(
				text = stringResource(when (state.scoringMethod) {
					GameScoringMethod.BY_FRAME -> R.string.game_editor_scoring_method_set_manual_score
					GameScoringMethod.MANUAL -> R.string.game_editor_scoring_method_edit_manual_score
				}),
			)
		}
	}
}

@Preview
@Composable
private fun ScoringMethodCardPreview() {
	ScoringMethodCard(
		state = GameDetailsUiState.ScoringMethodCardUiState(
			score = 234,
			scoringMethod = GameScoringMethod.BY_FRAME,
		),
		onAction = {},
	)
}