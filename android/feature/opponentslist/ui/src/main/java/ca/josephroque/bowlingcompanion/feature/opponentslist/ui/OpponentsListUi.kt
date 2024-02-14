package ca.josephroque.bowlingcompanion.feature.opponentslist.ui

import ca.josephroque.bowlingcompanion.core.model.OpponentListItem

data class OpponentsListUiState(
	val list: List<OpponentListItem>,
	val opponentToArchive: OpponentListItem?,
)

sealed interface OpponentsListUiAction {
	data object BackClicked : OpponentsListUiAction
	data object AddOpponentClicked : OpponentsListUiAction

	data class OpponentClicked(val opponent: OpponentListItem) : OpponentsListUiAction
	data class OpponentEdited(val opponent: OpponentListItem) : OpponentsListUiAction
	data class OpponentArchived(val opponent: OpponentListItem) : OpponentsListUiAction

	data object ConfirmArchiveClicked : OpponentsListUiAction
	data object DismissArchiveClicked : OpponentsListUiAction
}
