package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem

data class GearListUiState(val list: List<GearListItem>, val gearToDelete: GearListItem?)

sealed interface GearListUiAction {
	data object BackClicked : GearListUiAction
	data object AddGearClicked : GearListUiAction

	data class GearClicked(val id: GearID) : GearListUiAction
	data class GearEdited(val id: GearID) : GearListUiAction
	data class GearDeleted(val gear: GearListItem) : GearListUiAction

	data object ConfirmDeleteClicked : GearListUiAction
	data object DismissDeleteClicked : GearListUiAction

	data object FilterMenuClicked : GearListUiAction
	data object FilterMenuDismissed : GearListUiAction
	data class FilterClicked(val filter: GearKind?) : GearListUiAction
}

data class GearListTopBarUiState(
	val kindFilter: GearKind? = null,
	val isFilterMenuVisible: Boolean = false,
)
