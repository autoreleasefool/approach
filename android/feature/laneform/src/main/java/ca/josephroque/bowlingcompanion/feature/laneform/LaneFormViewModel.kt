package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.laneform.ui.AddLanesDialogUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.AddLanesDialogUiState
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiState
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneLabelDialogUiAction
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneLabelDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LaneFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val lanesRepository: LanesRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<LaneFormScreenEvent>() {

	private val existingLaneIds = Route.EditLanes.getLanes(savedStateHandle)

	private val isSwipeToEditTipDismissed = userDataRepository.userData.map {
		it.isLaneFormSwipeToEditTipDismissed
	}

	private val form: MutableStateFlow<LaneFormUiState> = MutableStateFlow(LaneFormUiState())

	val uiState: StateFlow<LaneFormScreenUiState> = combine(
		isSwipeToEditTipDismissed,
		form,
	) { isSwipeToEditTipDismissed, form ->
		LaneFormScreenUiState.Loaded(
			laneForm = form.copy(
				isShowingSwipeToEditTip = !isSwipeToEditTipDismissed,
			),
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
			LaneFormUiAction.BackClicked -> handleBackClicked()
			LaneFormUiAction.DoneClicked -> saveLanes()
			LaneFormUiAction.SwipeToEditTipDismissed -> dismissSwipeToEditTip()
			LaneFormUiAction.DiscardChangesClicked -> dismissForm()
			LaneFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is LaneFormUiAction.AddLanesClicked -> showAddLanesDialog()
			is LaneFormUiAction.LaneClicked -> showLaneLabelDialog(action.lane.id)
			is LaneFormUiAction.LaneDeleted -> deleteLane(action.lane.id)
			is LaneFormUiAction.LaneEdited -> showLaneLabelDialog(action.lane.id)
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
			is LaneLabelDialogUiAction.PositionDropDownToggled -> toggleLaneLabelDialogPositionDropDown(
				action.newValue,
			)
			LaneLabelDialogUiAction.PositionClicked -> toggleLaneLabelDialogPositionDropDown(true)
			LaneLabelDialogUiAction.CancelClicked -> dismissLaneLabelDialog(false)
			LaneLabelDialogUiAction.SaveClicked -> dismissLaneLabelDialog(true)
		}
	}

	private fun dismissForm() {
		sendEvent(LaneFormScreenEvent.DismissedWithResult(existingLaneIds))
	}

	private fun loadLanes() {
		viewModelScope.launch {
			val existingLanes = lanesRepository.getLanes(existingLaneIds).first()

			form.value = LaneFormUiState(
				existingLanes = existingLanes,
				lanes = existingLanes,
				addLanes = null,
				laneLabel = null,
				isShowingDiscardChangesDialog = false,
			)
		}
	}

	private fun handleBackClicked() {
		if (form.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			dismissForm()
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		form.update {
			it.copy(
				isShowingDiscardChangesDialog = isVisible,
			)
		}
	}

	private fun deleteLane(id: UUID) {
		form.update { form ->
			val laneToDelete = form.lanes.indexOfFirst { it.id == id }
			if (laneToDelete >= 0) {
				form.copy(
					lanes = form.lanes.toMutableList()
						.apply { removeAt(laneToDelete) },
				)
			} else {
				form
			}
		}
	}

	private fun saveLanes() {
		val lanes = form.value.lanes
		viewModelScope.launch {
			lanesRepository.insertLanes(lanes)

			sendEvent(LaneFormScreenEvent.DismissedWithResult(lanes.map(LaneListItem::id)))
		}
	}

	private fun dismissSwipeToEditTip() {
		viewModelScope.launch {
			userDataRepository.didDismissLaneFormSwipeToEditTip()
		}
	}

	private fun showLaneLabelDialog(id: UUID) {
		form.update { form ->
			val laneToEdit = form.lanes.firstOrNull { it.id == id } ?: return@update form

			form.copy(
				laneLabel = LaneLabelDialogUiState(
					laneId = laneToEdit.id,
					label = laneToEdit.label,
					position = laneToEdit.position,
					isPositionDropDownExpanded = false,
				),
			)
		}
	}

	private fun toggleLaneLabelDialogPositionDropDown(expanded: Boolean) {
		form.update {
			it.copy(
				laneLabel = it.laneLabel?.copy(
					isPositionDropDownExpanded = expanded,
				),
			)
		}
	}

	private fun updateLaneLabelDialogLabel(label: String) {
		form.update {
			it.copy(
				laneLabel = it.laneLabel?.copy(
					label = label,
					isPositionDropDownExpanded = false,
				),
			)
		}
	}

	private fun updateLaneLabelDialogPosition(position: LanePosition) {
		form.update {
			it.copy(
				laneLabel = it.laneLabel?.copy(
					position = position,
					isPositionDropDownExpanded = false,
				),
			)
		}
	}

	private fun dismissLaneLabelDialog(confirmChanges: Boolean) {
		val laneEdited = form.value.laneLabel ?: return

		form.update { form ->
			form.copy(
				lanes = if (confirmChanges) {
					form.lanes.toMutableList().map {
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
					form.lanes
				},
				laneLabel = null,
			)
		}
	}

	private fun showAddLanesDialog() {
		form.update {
			it.copy(
				addLanes = AddLanesDialogUiState(1),
			)
		}
	}

	private fun dismissAddLanesDialog() {
		form.update {
			it.copy(
				addLanes = null,
			)
		}
	}

	private fun updateLanesToAdd(numberOfLanes: Int) {
		form.update {
			it.copy(
				addLanes = it.addLanes?.copy(lanesToAdd = numberOfLanes.coerceAtLeast(1)),
			)
		}
	}

	private fun addMultipleLanes(numberOfLanes: Int) {
		form.update {
			it.copy(
				addLanes = null,
				laneLabel = null,
				lanes = it.lanes.toMutableList().apply {
					val labels = getNextLabels(numberOfLanes, form.value.lanes)
					labels.forEach { label ->
						add(
							LaneListItem(
								id = UUID.randomUUID(),
								label = label,
								position = LanePosition.NO_WALL,
							),
						)
					}
				},
			)
		}
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
