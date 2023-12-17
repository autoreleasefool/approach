package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.LANE_IDS
import ca.josephroque.bowlingcompanion.feature.laneform.ui.AddLanesDialogUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.AddLanesDialogUiState
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiState
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneLabelDialogUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneLabelDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LaneFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val lanesRepository: LanesRepository,
): ApproachViewModel<LaneFormScreenEvent>() {
	private val existingLaneIds = savedStateHandle.get<String>(LANE_IDS)
		?.let { if (it == "nan") emptyList() else it.split(",").map { uuid -> UUID.fromString(uuid) }
	} ?: emptyList()

	private val _form: MutableStateFlow<LaneFormUiState> = MutableStateFlow(LaneFormUiState())

	val uiState: StateFlow<LaneFormScreenUiState> = _form.map {
		LaneFormScreenUiState.Loaded(
			laneForm = it
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = LaneFormScreenUiState.Loading,
	)

	fun handleAction(action: LaneFormScreenUiAction) {
		when (action) {
			LaneFormScreenUiAction.LoadLanes -> loadLanes()
			is LaneFormScreenUiAction.LaneForm -> handleLaneFormAction(action.action)
		}
	}

	private fun handleLaneFormAction(action: LaneFormUiAction) {
		when (action) {
			LaneFormUiAction.BackClicked -> sendEvent(LaneFormScreenEvent.DismissedWithResult(existingLaneIds))
			LaneFormUiAction.DoneClicked -> saveLanes()
			is LaneFormUiAction.AddLanesClicked -> showAddLanesDialog()
			is LaneFormUiAction.LaneClicked -> showLaneLabelDialog(action.lane.id)
			is LaneFormUiAction.LaneDeleted -> deleteLane(action.lane.id)
			is LaneFormUiAction.AddLanesDialog -> handleAddLanesDialogAction(action.action)
			is LaneFormUiAction.LaneLabelDialog -> handleLaneLabelDialogAction(action.action)
		}
	}

	private fun handleAddLanesDialogAction(action: AddLanesDialogUiAction) {
		when (action) {
			AddLanesDialogUiAction.Dismissed -> dismissAddLanesDialog()
			is AddLanesDialogUiAction.AddLanesClicked -> addMultipleLanes(action.numberOfLanes)
			is AddLanesDialogUiAction.NumberOfLanesChanged -> updateLanesToAdd(action.numberOfLanes)
		}
	}

	private fun handleLaneLabelDialogAction(action: LaneLabelDialogUiAction) {
		when (action) {
			is LaneLabelDialogUiAction.LabelChanged -> updateLaneLabelDialogLabel(action.label)
			is LaneLabelDialogUiAction.PositionChanged -> updateLaneLabelDialogPosition(action.position)
			is LaneLabelDialogUiAction.PositionDropDownToggled -> toggleLaneLabelDialogPositionDropDown(action.newValue)
			LaneLabelDialogUiAction.PositionClicked -> toggleLaneLabelDialogPositionDropDown(true)
			LaneLabelDialogUiAction.CancelClicked -> dismissLaneLabelDialog(false)
			LaneLabelDialogUiAction.SaveClicked -> dismissLaneLabelDialog(true)
		}
	}

	private fun loadLanes() {
		viewModelScope.launch {
			val existingLanes = lanesRepository.getLanes(existingLaneIds).first()

			_form.value = LaneFormUiState(
				lanes = existingLanes,
				addLanes = null,
				laneLabel = null,
			)
		}
	}

	private fun deleteLane(id: UUID) {
		val laneToDelete = _form.value.lanes.indexOfFirst { it.id == id }
		if (laneToDelete >= 0) {
			_form.value = _form.value.copy(
				lanes = _form.value.lanes.toMutableList()
					.apply { removeAt(laneToDelete) }
			)
		}
	}

	private fun saveLanes() {
		val lanes = _form.value.lanes
		viewModelScope.launch {
			lanesRepository.insertLanes(lanes)

			sendEvent(LaneFormScreenEvent.DismissedWithResult(lanes.map(LaneListItem::id)))
		}
	}

	private fun showLaneLabelDialog(id: UUID) {
		val laneToEdit = _form.value.lanes.firstOrNull { it.id == id } ?: return

		_form.value = _form.value.copy(
			laneLabel = LaneLabelDialogUiState(
				laneId = laneToEdit.id,
				label = laneToEdit.label,
				position = laneToEdit.position,
				isPositionDropDownExpanded = false,
			)
		)
	}

	private fun toggleLaneLabelDialogPositionDropDown(expanded: Boolean) {
		_form.value = _form.value.copy(
			laneLabel = _form.value.laneLabel?.copy(
				isPositionDropDownExpanded = expanded,
			)
		)
	}

	private fun updateLaneLabelDialogLabel(label: String) {
		_form.value = _form.value.copy(
			laneLabel = _form.value.laneLabel?.copy(
				label = label,
				isPositionDropDownExpanded = false,
			)
		)
	}

	private fun updateLaneLabelDialogPosition(position: LanePosition) {
		_form.value = _form.value.copy(
			laneLabel = _form.value.laneLabel?.copy(
				position = position,
				isPositionDropDownExpanded = false,
			)
		)
	}

	private fun dismissLaneLabelDialog(confirmChanges: Boolean) {
		val laneEdited = _form.value.laneLabel ?: return

		_form.value = _form.value.copy(
			lanes = if (confirmChanges) {
				_form.value.lanes.toMutableList().map {
					if (laneEdited.laneId == it.id) {
						LaneListItem(
							id = laneEdited.laneId,
							position = laneEdited.position,
							label = laneEdited.label,
						)
					} else {
						it
					}
				}
			} else {
				_form.value.lanes
			},
			laneLabel = null,
		)
	}

	private fun showAddLanesDialog() {
		_form.value = _form.value.copy(
			addLanes = AddLanesDialogUiState(1),
		)
	}

	private fun dismissAddLanesDialog() {
		_form.value = _form.value.copy(
			addLanes = null,
		)
	}

	private fun updateLanesToAdd(numberOfLanes: Int) {
		_form.value = _form.value.copy(
			addLanes = _form.value.addLanes?.copy(lanesToAdd = numberOfLanes)
		)
	}

	private fun addMultipleLanes(numberOfLanes: Int) {
		_form.value = _form.value.copy(
			lanes = _form.value.lanes.toMutableList().apply {
				val labels = getNextLabels(numberOfLanes, _form.value.lanes)
				labels.forEach {
					add(
						LaneListItem(
							id = UUID.randomUUID(),
							label = it,
							position = LanePosition.NO_WALL,
						)
					)
				}
			},
			addLanes = null,
			laneLabel = null,
		)
	}

	private fun getNextLabels(count: Int, list: List<LaneListItem>): List<String> {
		if (list.isEmpty()) {
			return (1..count).map { it.toString() }
		}

		return list.last().label.toIntOrNull()?.let { previousLabel ->
			(previousLabel + 1..previousLabel + count).map { it.toString() }
		} ?: kotlin.run {
			(1..count).map { "" }
		}
	}
}