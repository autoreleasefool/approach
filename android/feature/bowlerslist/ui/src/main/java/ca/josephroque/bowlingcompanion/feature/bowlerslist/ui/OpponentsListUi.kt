package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import ca.josephroque.bowlingcompanion.core.model.OpponentListItem

data class OpponentsListUiState(
	val list: List<OpponentListItem>,
	val opponentToArchive: OpponentListItem?,
)

sealed interface OpponentsListUiAction {
	data object AddOpponentClicked: OpponentsListUiAction

	data class OpponentClicked(val bowler: OpponentListItem): OpponentsListUiAction
	data class OpponentEdited(val bowler: OpponentListItem): OpponentsListUiAction
	data class OpponentArchived(val bowler: OpponentListItem): OpponentsListUiAction

	data object ConfirmArchiveClicked: OpponentsListUiAction
	data object DismissArchiveClicked: OpponentsListUiAction
}