package ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult

@Composable
fun MatchPlayEditor(
	state: MatchPlayEditorUiState,
	onAction: (MatchPlayEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxSize(),
	) {
		PickableResourceCard(
			resourceName = stringResource(R.string.match_play_editor_opponent),
			selectedName = state.opponent?.name ?: stringResource(R.string.match_play_editor_opponent_none),
			onClick = { onAction(MatchPlayEditorUiAction.OpponentClicked) },
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		OutlinedTextField(
			value = state.opponentScore?.toString() ?: "",
			onValueChange = { onAction(MatchPlayEditorUiAction.OpponentScoreChanged(it)) },
			singleLine = true,
			label = {
				Text(
					stringResource(R.string.match_play_editor_opponent_score),
					style = MaterialTheme.typography.bodyMedium,
				)
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)

		Divider()

		FormRadioGroup(
			title = stringResource(R.string.match_play_result_outcome),
			subtitle = stringResource(R.string.match_play_result_outcome_description),
			options = MatchPlayResult.entries.toTypedArray(),
			allowNullableSelection = true,
			selected = state.result,
			titleForOption = {
				when (it) {
					MatchPlayResult.WON -> stringResource(ca.josephroque.bowlingcompanion.core.model.ui.R.string.match_play_result_won)
					MatchPlayResult.LOST -> stringResource(ca.josephroque.bowlingcompanion.core.model.ui.R.string.match_play_result_lost)
					MatchPlayResult.TIED -> stringResource(ca.josephroque.bowlingcompanion.core.model.ui.R.string.match_play_result_tied)
					null -> stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.none)
				}
			},
			onOptionSelected = { onAction(MatchPlayEditorUiAction.ResultChanged(it)) },
		)
	}
}