package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcePickerTopBar(
	state: ResourcePickerTopBarUiState,
	onAction: (ResourcePickerUiAction) -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				text = pluralStringResource(state.titleResourceId, count = state.limit),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = { BackButton(onClick = { onAction(ResourcePickerUiAction.BackClicked) }) },
		actions = {
			Text(
				text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.clickable(onClick = { onAction(ResourcePickerUiAction.DoneClicked) },)
					.padding(16.dp),
			)
		}
	)
}