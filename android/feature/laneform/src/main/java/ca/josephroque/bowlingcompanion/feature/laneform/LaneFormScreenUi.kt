package ca.josephroque.bowlingcompanion.feature.laneform

import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiState
import java.util.UUID

sealed interface LaneFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading: LaneFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Loaded(
		val laneForm: LaneFormUiState,
	): LaneFormScreenUiState {
		override fun isSavable(): Boolean = true

		override fun hasAnyChanges(): Boolean =
			laneForm.hasAnyChanges()
	}
}

sealed interface LaneFormScreenUiAction {
	data object LoadLanes: LaneFormScreenUiAction
	data class LaneForm(val action: LaneFormUiAction): LaneFormScreenUiAction
}

sealed interface LaneFormScreenEvent {
	data class DismissedWithResult(val lanes: List<UUID>): LaneFormScreenEvent
}