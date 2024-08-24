package ca.josephroque.bowlingcompanion.feature.teamform.ui

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
fun TeamFormTopBar(
	state: TeamFormTopBarUiState,
	onAction: (TeamFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(TeamFormUiAction.BackClicked) }) },
		actions = { Actions(onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(state: TeamFormTopBarUiState) {
	Text(
		text = if (state.existingName == null) {
			stringResource(R.string.team_form_title_new)
		} else {
			stringResource(R.string.team_form_title_edit, state.existingName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(onAction: (TeamFormUiAction) -> Unit) {
	TextButton(
		onClick = { onAction(TeamFormUiAction.DoneClicked) },
	) {
		Text(
			stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}
