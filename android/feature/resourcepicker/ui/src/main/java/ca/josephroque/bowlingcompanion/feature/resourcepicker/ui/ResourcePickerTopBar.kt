package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcePickerTopBar(
	state: ResourcePickerTopBarUiState,
	onAction: (ResourcePickerUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = state.titleOverride ?: pluralStringResource(state.titleResourceId, count = state.limit),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = { BackButton(onClick = { onAction(ResourcePickerUiAction.BackClicked) }) },
		actions = {
			TextButton(onClick = { onAction(ResourcePickerUiAction.DoneClicked) }) {
				Text(
					text = stringResource(R.string.action_save),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	)
}