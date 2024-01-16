package ca.josephroque.bowlingcompanion.feature.leagueform.ui

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
fun LeagueFormTopBar(
	state: LeagueFormTopBarUiState,
	onAction: (LeagueFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state.existingName) },
		navigationIcon = { BackButton(onClick = { onAction(LeagueFormUiAction.BackClicked) }) },
		actions = { Actions(onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(leagueName: String?) {
	Text(
		text = if (leagueName == null) {
			stringResource(R.string.league_form_title_new)
		} else {
			stringResource(R.string.league_form_title_edit, leagueName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(
	onAction: (LeagueFormUiAction) -> Unit,
) {
	TextButton(onClick = { onAction(LeagueFormUiAction.DoneClicked) }) {
		Text(
			text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}