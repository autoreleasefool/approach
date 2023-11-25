package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.annotation.PluralsRes
import java.util.UUID

enum class ResourcePickerType {
	Bowler,
}

data class ResourceItem(
	val id: UUID,
	val name: String,
)

data class ResourcePickerUiState(
	val resourceType: ResourcePickerType,
	val items: List<ResourceItem>,
	val selectedItems: Set<UUID>,
)

sealed interface ResourcePickerUiAction {
	data object BackClicked: ResourcePickerUiAction
	data object DoneClicked: ResourcePickerUiAction

	data class ItemClicked(val itemId: UUID): ResourcePickerUiAction
}

data class ResourcePickerTopBarUiState(
	@PluralsRes val titleResourceId: Int = R.plurals.base_picker_title,
	val limit: Int = 0,
)