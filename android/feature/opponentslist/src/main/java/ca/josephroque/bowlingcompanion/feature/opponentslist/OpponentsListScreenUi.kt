package ca.josephroque.bowlingcompanion.feature.opponentslist

import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsListUiAction
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsListUiState
import java.util.UUID

sealed interface OpponentsListScreenUiState {
	data object Loading : OpponentsListScreenUiState

	data class Loaded(
		val list: OpponentsListUiState,
	) : OpponentsListScreenUiState
}

sealed interface OpponentsListScreenUiAction {
	data class OpponentsListAction(val action: OpponentsListUiAction) : OpponentsListScreenUiAction
}

sealed interface OpponentsListScreenEvent {
	data object Dismissed : OpponentsListScreenEvent
	data object AddOpponent : OpponentsListScreenEvent

	data class EditOpponent(val id: UUID) : OpponentsListScreenEvent
	data class ShowOpponentDetails(val id: UUID) : OpponentsListScreenEvent
}
