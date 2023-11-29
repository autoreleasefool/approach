package ca.josephroque.bowlingcompanion.feature.leagueform.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueFormTopBar(
	state: LeagueFormTopBarUiState,
	onAction: (LeagueFormUiAction) -> Unit,
) {
	TopAppBar(
		title = { Title(state.existingName) },
		navigationIcon = { BackButton(onClick = { onAction(LeagueFormUiAction.BackClicked) }) },
		actions = { Actions(onAction) },
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
	Text(
		stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
		modifier = Modifier
			.clickable(onClick = { onAction(LeagueFormUiAction.DoneClicked) })
			.padding(16.dp),
	)
}