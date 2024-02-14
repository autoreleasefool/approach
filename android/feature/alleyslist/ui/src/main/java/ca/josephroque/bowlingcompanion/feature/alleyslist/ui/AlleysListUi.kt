package ca.josephroque.bowlingcompanion.feature.alleyslist.ui

import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import java.util.UUID

data class AlleysListUiState(
	val list: List<AlleyListItem>,
	val alleyToDelete: AlleyListItem?,
)

sealed interface AlleysListUiAction {
	data object BackClicked : AlleysListUiAction
	data object AddAlleyClicked : AlleysListUiAction

	data class AlleyClicked(val id: UUID) : AlleysListUiAction
	data class AlleyEdited(val id: UUID) : AlleysListUiAction
	data class AlleyDeleted(val alley: AlleyListItem) : AlleysListUiAction

	data object ConfirmDeleteClicked : AlleysListUiAction
	data object DismissDeleteClicked : AlleysListUiAction
}
