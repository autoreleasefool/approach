package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.annotation.PluralsRes
import java.util.UUID

data class ResourcePickerUiState<T: Resource>(
	val items: List<T>,
	val selectedItems: Set<UUID>,
)

sealed interface ResourcePickerUiAction {
	data object BackClicked: ResourcePickerUiAction
	data object DoneClicked: ResourcePickerUiAction

	data class ItemClicked(val itemId: UUID): ResourcePickerUiAction
}

data class ResourcePickerTopBarUiState(
	@PluralsRes val titleResourceId: Int,
	val limit: Int,
)