package ca.josephroque.bowlingcompanion.feature.laneform.ui

import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import java.util.UUID

data class LaneFormUiState(
	val isShowingSwipeToEditTip: Boolean = false,
	val existingLanes: List<LaneListItem> = emptyList(),
	val lanes: List<LaneListItem> = emptyList(),
	val addLanes: AddLanesDialogUiState? = null,
	val laneLabel: LaneLabelDialogUiState? = null,
	val isShowingDiscardChangesDialog: Boolean = false,
) {
	fun hasAnyChanges(): Boolean =
		existingLanes != lanes
}

sealed interface LaneFormUiAction {
	data object BackClicked: LaneFormUiAction
	data object DoneClicked: LaneFormUiAction
	data object SwipeToEditTipDismissed: LaneFormUiAction
	data object AddLanesClicked: LaneFormUiAction

	data object DiscardChangesClicked: LaneFormUiAction
	data object CancelDiscardChangesClicked: LaneFormUiAction

	data class AddLanesDialog(val action: AddLanesDialogUiAction): LaneFormUiAction
	data class LaneLabelDialog(val action: LaneLabelDialogUiAction): LaneFormUiAction
	data class LaneClicked(val lane: LaneListItem): LaneFormUiAction
	data class LaneDeleted(val lane: LaneListItem): LaneFormUiAction
	data class LaneEdited(val lane: LaneListItem): LaneFormUiAction
}

data class AddLanesDialogUiState(
	val lanesToAdd: Int,
)

sealed interface AddLanesDialogUiAction {
	data object Dismissed: AddLanesDialogUiAction
	data class AddLanesClicked(val numberOfLanes: Int): AddLanesDialogUiAction
	data class NumberOfLanesChanged(val numberOfLanes: Int): AddLanesDialogUiAction
}

data class LaneLabelDialogUiState(
	val laneId: UUID,
	val label: String,
	val position: LanePosition,
	val isPositionDropDownExpanded: Boolean,
)

sealed interface LaneLabelDialogUiAction {
	data object PositionClicked: LaneLabelDialogUiAction
	data object SaveClicked: LaneLabelDialogUiAction
	data object CancelClicked: LaneLabelDialogUiAction

	data class PositionDropDownToggled(val newValue: Boolean): LaneLabelDialogUiAction
	data class LabelChanged(val label: String): LaneLabelDialogUiAction
	data class PositionChanged(val position: LanePosition): LaneLabelDialogUiAction
}