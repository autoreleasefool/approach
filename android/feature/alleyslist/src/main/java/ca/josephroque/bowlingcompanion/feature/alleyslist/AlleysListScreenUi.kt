package ca.josephroque.bowlingcompanion.feature.alleyslist

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiAction
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState

sealed interface AlleysListScreenUiState {
	data object Loading : AlleysListScreenUiState

	data class Loaded(val alleysList: AlleysListUiState) : AlleysListScreenUiState
}

sealed interface AlleysListScreenUiAction {
	data class AlleysList(val action: AlleysListUiAction) : AlleysListScreenUiAction
}

sealed interface AlleysListScreenEvent {
	data object Dismissed : AlleysListScreenEvent
	data object NavigateToAddAlley : AlleysListScreenEvent
	data class NavigateToEditAlley(val id: AlleyID) : AlleysListScreenEvent
}
