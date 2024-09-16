package ca.josephroque.bowlingcompanion.feature.alleyform.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlleyFormTopBar(
	state: AlleyFormTopBarUiState,
	onAction: (AlleyFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(AlleyFormUiAction.BackClicked) }) },
		actions = { Actions(state, onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(state: AlleyFormTopBarUiState) {
	Text(
		text = if (state.existingName == null) {
			stringResource(R.string.alley_form_title_new)
		} else {
			stringResource(R.string.alley_form_title_edit, state.existingName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(state: AlleyFormTopBarUiState, onAction: (AlleyFormUiAction) -> Unit) {
	TextButton(
		onClick = { onAction(AlleyFormUiAction.DoneClicked) },
		enabled = state.isSaveButtonEnabled,
	) {
		Text(
			stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}
