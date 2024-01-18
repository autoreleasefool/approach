package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerListItem

data class BowlersListUiState(
	val list: List<BowlerListItem>,
	val bowlerToArchive: BowlerListItem?,
)

sealed interface BowlersListUiAction {
	data object AddBowlerClicked: BowlersListUiAction

	data class BowlerClicked(val bowler: BowlerListItem): BowlersListUiAction
	data class BowlerEdited(val bowler: BowlerListItem): BowlersListUiAction
	data class BowlerArchived(val bowler: BowlerListItem): BowlersListUiAction

	data object ConfirmArchiveClicked: BowlersListUiAction
	data object DismissArchiveClicked: BowlersListUiAction
}