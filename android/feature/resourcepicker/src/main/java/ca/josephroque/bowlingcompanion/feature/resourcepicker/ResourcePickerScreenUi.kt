package ca.josephroque.bowlingcompanion.feature.resourcepicker

import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.Resource
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
import java.util.UUID

sealed interface ResourcePickerScreenUiState<out T> {
	data object Loading: ResourcePickerScreenUiState<Nothing>

	data class Loaded<T: Resource>(
		val picker: ResourcePickerUiState<T>,
		val topBar: ResourcePickerTopBarUiState,
	): ResourcePickerScreenUiState<T>
}

sealed interface ResourcePickerScreenUiAction<out T> {
	data object LoadResources: ResourcePickerScreenUiAction<Nothing>

	data class ResourcePickerAction<T: Resource>(
		val action: ResourcePickerUiAction,
	): ResourcePickerScreenUiAction<T>
}

sealed interface ResourcePickerScreenEvent {
	data class Dismissed(val result: Set<UUID>): ResourcePickerScreenEvent
}