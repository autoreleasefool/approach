package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePicker
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBar
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import java.util.UUID

@Composable
internal fun BowlerPickerRoute(
	onDismissWithResult: (Set<UUID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerPickerViewModel = hiltViewModel(),
) {
	val resourcePickerScreenState = viewModel.uiState.collectAsState().value

	when (val event = viewModel.events.collectAsState().value) {
		is ResourcePickerScreenEvent.Dismissed -> onDismissWithResult(event.result)
		else -> Unit
	}

	BowlerPickerScreen(
		state = resourcePickerScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun BowlerPickerScreen(
	state: ResourcePickerScreenUiState<BowlerResource>,
	onAction: (ResourcePickerScreenUiAction<BowlerResource>) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(ResourcePickerScreenUiAction.LoadResources)
	}

	Scaffold(
		topBar = {
			ResourcePickerTopBar(
				state = when (state) {
					ResourcePickerScreenUiState.Loading -> ResourcePickerTopBarUiState()
					is ResourcePickerScreenUiState.Loaded -> state.topBar
			  },
				onAction = { onAction(ResourcePickerScreenUiAction.ResourcePickerAction(it)) },
			)
		},
	) { padding ->
		when (state) {
			ResourcePickerScreenUiState.Loading -> Unit
			is ResourcePickerScreenUiState.Loaded ->
				ResourcePicker(
					state = state.picker,
					onAction = { onAction(ResourcePickerScreenUiAction.ResourcePickerAction(it)) },
					itemContent = { BowlerRow(name = it.name) },
					modifier = modifier.padding(padding),
				)
		}
	}
}