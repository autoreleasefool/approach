package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

data class BowlersListUiState(
	val list: List<BowlerListItem>,
	val bowlerToArchive: BowlerListItem?,
)

sealed interface BowlersListUiAction {
	data object AddBowlerClicked: BowlersListUiAction

	data class BowlerClicked(val id: UUID): BowlersListUiAction
	data class BowlerEdited(val id: UUID): BowlersListUiAction
	data class BowlerArchived(val bowler: BowlerListItem): BowlersListUiAction

	data object ConfirmArchiveClicked: BowlersListUiAction
	data object DismissArchiveClicked: BowlersListUiAction
}