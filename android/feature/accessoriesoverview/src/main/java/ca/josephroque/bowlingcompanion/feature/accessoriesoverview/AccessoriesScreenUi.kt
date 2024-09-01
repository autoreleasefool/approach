package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiAction
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState

sealed interface AccessoriesScreenUiState {
	data object Loading : AccessoriesScreenUiState

	data class Loaded(val accessories: AccessoriesUiState) : AccessoriesScreenUiState
}

sealed interface AccessoriesScreenUiAction {
	data object DidAppear : AccessoriesScreenUiAction
	data class Accessories(val action: AccessoriesUiAction) : AccessoriesScreenUiAction
}

sealed interface AccessoriesScreenUiEvent {
	data object AddAlley : AccessoriesScreenUiEvent
	data object AddGear : AccessoriesScreenUiEvent
	data object ViewAllAlleys : AccessoriesScreenUiEvent
	data object ViewAllGear : AccessoriesScreenUiEvent
	data object ShowAccessoriesOnboarding : AccessoriesScreenUiEvent

	data class ShowAlleyDetails(val alleyId: AlleyID) : AccessoriesScreenUiEvent
	data class ShowGearDetails(val gearId: GearID) : AccessoriesScreenUiEvent
}
