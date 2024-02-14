package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.annotation.PluralsRes
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import java.util.UUID
import kotlinx.datetime.LocalDate

sealed interface ResourcePickerFilter {
	data class Str(val value: String) : ResourcePickerFilter
	data class League(val id: UUID) : ResourcePickerFilter
	data class Series(val id: UUID) : ResourcePickerFilter
	data class Gear(val kind: GearKind) : ResourcePickerFilter
	data class Alley(val id: UUID) : ResourcePickerFilter
}

sealed interface ResourceItem {
	val id: UUID
	val name: String

	data class Bowler(
		override val id: UUID,
		override val name: String,
	) : ResourceItem

	data class League(
		override val id: UUID,
		override val name: String,
	) : ResourceItem

	data class Series(
		override val id: UUID,
		val date: LocalDate,
		val total: Int,
	) : ResourceItem {
		override val name: String = date.simpleFormat()
	}

	data class Game(
		override val id: UUID,
		val index: Int,
		val score: Int,
	) : ResourceItem {
		override val name: String = "Game ${index + 1}"
	}

	data class Gear(
		override val id: UUID,
		override val name: String,
		val kind: GearKind,
		val ownerName: String?,
		val avatar: Avatar,
	) : ResourceItem

	data class Alley(
		override val id: UUID,
		override val name: String,
	) : ResourceItem

	data class Lane(
		override val id: UUID,
		override val name: String,
		val position: LanePosition,
	) : ResourceItem
}

data class ResourcePickerUiState(
	val resourceType: ResourcePickerType,
	val items: List<ResourceItem>,
	val selectedItems: Set<UUID>,
)

sealed interface ResourcePickerUiAction {
	data object BackClicked : ResourcePickerUiAction
	data object DoneClicked : ResourcePickerUiAction

	data class ItemClicked(val itemId: UUID) : ResourcePickerUiAction
}

data class ResourcePickerTopBarUiState(
	@PluralsRes val titleResourceId: Int = R.plurals.base_picker_title,
	val titleOverride: String? = null,
	val limit: Int = 0,
)
