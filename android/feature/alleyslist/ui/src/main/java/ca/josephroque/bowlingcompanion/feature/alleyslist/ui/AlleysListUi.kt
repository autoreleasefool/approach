package ca.josephroque.bowlingcompanion.feature.alleyslist.ui

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem

data class AlleysListUiState(val list: List<AlleyListItem>, val alleyToDelete: AlleyListItem?)

sealed interface AlleysListUiAction {
	data object BackClicked : AlleysListUiAction
	data object AddAlleyClicked : AlleysListUiAction

	data class AlleyClicked(val id: AlleyID) : AlleysListUiAction
	data class AlleyEdited(val id: AlleyID) : AlleysListUiAction
	data class AlleyDeleted(val alley: AlleyListItem) : AlleysListUiAction

	data object ConfirmDeleteClicked : AlleysListUiAction
	data object DismissDeleteClicked : AlleysListUiAction
}
