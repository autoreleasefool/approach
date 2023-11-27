package ca.josephroque.bowlingcompanion.feature.opponentslist.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

data class OpponentsListUiState(
	val list: List<BowlerListItem>,
	val opponentToArchive: BowlerListItem?,
)

sealed interface OpponentsListUiAction {
	data object BackClicked: OpponentsListUiAction
	data object AddOpponentClicked: OpponentsListUiAction

	data class OpponentClicked(val id: UUID): OpponentsListUiAction
	data class OpponentEdited(val id: UUID): OpponentsListUiAction
	data class OpponentArchived(val opponent: BowlerListItem): OpponentsListUiAction

	data object ConfirmArchiveClicked: OpponentsListUiAction
	data object DismissArchiveClicked: OpponentsListUiAction
}