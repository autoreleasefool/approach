package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.annotation.PluralsRes
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearKind
import java.util.UUID

enum class ResourcePickerType {
	BOWLER,
	LEAGUE,
	GEAR,
}

sealed interface ResourceItem {
	val id: UUID
	val name: String

	data class Bowler(
		override val id: UUID,
		override val name: String,
	): ResourceItem

	data class League(
		override val id: UUID,
		override val name: String,
	): ResourceItem

	data class Gear(
		override val id: UUID,
		override val name: String,
		val kind: GearKind,
		val ownerName: String?,
		val avatar: Avatar,
	): ResourceItem
}

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