package ca.josephroque.bowlingcompanion.feature.resourcepicker

import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
import java.util.UUID

sealed interface ResourcePickerScreenUiState {
	data object Loading: ResourcePickerScreenUiState

	data class Loaded(
		val picker: ResourcePickerUiState,
		val topBar: ResourcePickerTopBarUiState,
	): ResourcePickerScreenUiState
}

sealed interface ResourcePickerScreenUiAction {
	data object LoadResources: ResourcePickerScreenUiAction

	data class ResourcePickerAction(
		val action: ResourcePickerUiAction,
	): ResourcePickerScreenUiAction
}

sealed interface ResourcePickerScreenEvent {
	data class Dismissed(val result: Set<UUID>): ResourcePickerScreenEvent
}