package ca.josephroque.bowlingcompanion.feature.gearlist

import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiAction
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import java.util.UUID

sealed interface GearListScreenUiState {
	data object Loading : GearListScreenUiState

	data class Loaded(
		val gearList: GearListUiState,
		val topBar: GearListTopBarUiState,
	) : GearListScreenUiState
}

sealed interface GearListScreenUiAction {
	data class GearListAction(
		val action: GearListUiAction,
	) : GearListScreenUiAction
}

sealed interface GearListScreenEvent {
	data object Dismissed : GearListScreenEvent
	data object NavigateToAddGear : GearListScreenEvent
	data class NavigateToEditGear(val id: UUID) : GearListScreenEvent
}
