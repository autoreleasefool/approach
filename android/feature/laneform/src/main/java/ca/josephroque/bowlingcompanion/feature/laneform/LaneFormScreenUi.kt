package ca.josephroque.bowlingcompanion.feature.laneform

import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiState

sealed interface LaneFormScreenUiState {
	data object Loading: LaneFormScreenUiState

	data class Loaded(
		val laneForm: LaneFormUiState,
	): LaneFormScreenUiState
}

sealed interface LaneFormScreenUiAction {
	data object LoadLanes: LaneFormScreenUiAction
	data class LaneForm(val action: LaneFormUiAction): LaneFormScreenUiAction
}

sealed interface LaneFormScreenEvent {
	data object Dismissed: LaneFormScreenEvent
}