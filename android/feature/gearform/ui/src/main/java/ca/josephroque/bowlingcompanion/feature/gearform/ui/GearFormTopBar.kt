package ca.josephroque.bowlingcompanion.feature.gearform.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
		actions = { Actions(onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(
	state: GearFormTopBarUiState,
) {
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
private fun Actions(
	onAction: (GearFormUiAction) -> Unit,
) {
	Text(
		text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
		style = MaterialTheme.typography.bodyMedium,
		modifier = Modifier
			.clickable(onClick = { onAction(GearFormUiAction.DoneClicked) })
			.padding(16.dp),
	)
}