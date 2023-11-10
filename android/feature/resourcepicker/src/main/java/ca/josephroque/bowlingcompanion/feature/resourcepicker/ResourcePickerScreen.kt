package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePicker
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBar
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
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

	when (state) {
		ResourcePickerScreenUiState.Loading -> LoadingState()
		is ResourcePickerScreenUiState.Loaded ->
			BowlerPickerScreen(
				state = state.picker,
				topBarState = state.topBar,
				onAction = { onAction(ResourcePickerScreenUiAction.ResourcePickerAction(it)) },
				modifier = modifier,
			)
	}
}

@Composable
private fun BowlerPickerScreen(
	state: ResourcePickerUiState<BowlerResource>,
	topBarState: ResourcePickerTopBarUiState,
	onAction: (ResourcePickerUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			ResourcePickerTopBar(
				state = topBarState,
				onAction = onAction,
			)
		},
	) { padding ->
		ResourcePicker(
			state = state,
			onAction = onAction,
			itemContent = { BowlerRow(name = it.name) },
			modifier = modifier.padding(padding),
		)
	}
}