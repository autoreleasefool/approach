package ca.josephroque.bowlingcompanion.feature.gearform.ui

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
fun GearFormTopBar(
	state: GearFormTopBarUiState,
	onAction: (GearFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(GearFormUiAction.BackClicked) }) },
		actions = { Actions(state, onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(state: GearFormTopBarUiState) {
	Text(
		text = if (state.existingName == null) {
			stringResource(R.string.gear_form_new_title)
		} else {
			stringResource(R.string.gear_form_edit_title, state.existingName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(state: GearFormTopBarUiState, onAction: (GearFormUiAction) -> Unit) {
	TextButton(
		onClick = { onAction(GearFormUiAction.DoneClicked) },
		enabled = state.isSaveButtonEnabled,
	) {
		Text(
			text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}
