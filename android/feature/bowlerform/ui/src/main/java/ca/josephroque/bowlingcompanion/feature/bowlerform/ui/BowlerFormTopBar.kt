package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerFormTopBar(
	state: BowlerFormTopBarUiState,
	onAction: (BowlerFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(BowlerFormUiAction.BackClicked) }) },
		actions = { Actions(state, onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(state: BowlerFormTopBarUiState) {
	Text(
		text = if (state.existingName == null) {
			when (state.kind) {
				BowlerKind.PLAYABLE -> stringResource(R.string.bowler_form_new_bowler_title)
				BowlerKind.OPPONENT -> stringResource(R.string.bowler_form_new_opponent_title)
			}
		} else {
			stringResource(R.string.bowler_form_edit_title, state.existingName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(state: BowlerFormTopBarUiState, onAction: (BowlerFormUiAction) -> Unit) {
	TextButton(
		onClick = { onAction(BowlerFormUiAction.DoneClicked) },
		enabled = state.isSaveButtonEnabled,
	) {
		Text(
			text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}
