package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiAction
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState
import java.util.UUID

sealed interface AccessoriesScreenUiState {
	data object Loading : AccessoriesScreenUiState

	data class Loaded(
		val accessories: AccessoriesUiState,
	) : AccessoriesScreenUiState
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

	data class ShowAlleyDetails(val alleyId: UUID) : AccessoriesScreenUiEvent
	data class ShowGearDetails(val gearId: UUID) : AccessoriesScreenUiEvent
}
