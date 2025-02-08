package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
fun ScoreEditor(state: ScoreEditorUiState, onAction: (ScoreEditorUiAction) -> Unit, modifier: Modifier = Modifier) {
	val focusRequester = remember { FocusRequester() }

	LaunchedEffect(state.scoringMethod) {
		when (state.scoringMethod) {
			GameScoringMethod.MANUAL -> focusRequester.requestFocus()
			GameScoringMethod.BY_FRAME -> Unit
		}
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.imePadding(),
	) {
		FormRadioGroup(
			title = stringResource(R.string.game_editor_scoring_method_title),
			subtitle = stringResource(R.string.game_editor_scoring_method_subtitle),
			options = GameScoringMethod.entries.toTypedArray(),
			selected = state.scoringMethod,
			titleForOption = {
				when (it) {
					GameScoringMethod.MANUAL -> stringResource(R.string.scoring_method_manual)
					GameScoringMethod.BY_FRAME -> stringResource(R.string.scoring_method_frame_by_frame)
					null -> ""
				}
			},
			onOptionSelected = {
				it ?: return@FormRadioGroup
				onAction(ScoreEditorUiAction.ScoringMethodChanged(it))
			},
		)

		when (state.scoringMethod) {
			GameScoringMethod.BY_FRAME -> Unit
			GameScoringMethod.MANUAL -> {
				ScoreTextField(
					score = state.score,
					onScoreChanged = { onAction(ScoreEditorUiAction.ScoreChanged(it)) },
					modifier = Modifier.focusRequester(focusRequester),
				)
			}
		}
	}
}

@Composable
private fun ScoreTextField(score: Int, onScoreChanged: (String) -> Unit, modifier: Modifier = Modifier) {
	OutlinedTextField(
		value = score.toString(),
		onValueChange = onScoreChanged,
		singleLine = true,
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		label = {
			Text(
				stringResource(R.string.score_editor_score),
				style = MaterialTheme.typography.bodyMedium,
			)
		},
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	)
}
