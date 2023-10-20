package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.database.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.database.model.asLaneCreate
import ca.josephroque.bowlingcompanion.core.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.ALLEY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LaneFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val lanesRepository: LanesRepository,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {
	private val alleyId = UUID.fromString(savedStateHandle[ALLEY_ID])

	private val _lanes: MutableStateFlow<LaneFormUiState> = MutableStateFlow(LaneFormUiState.Loading)
	val lanes: StateFlow<LaneFormUiState> = _lanes.asStateFlow()

	fun loadLanes() {
		viewModelScope.launch {
			val existingLanes = lanesRepository.alleyLanes(alleyId)
				.first()
				.map { it.asLaneCreate(alleyId) }
			_lanes.value = LaneFormUiState.Success(
				lanes = existingLanes,
				addLanesDialogState = null,
				laneLabelDialogState = null,
			)
		}
	}

	fun deleteLane(id: UUID) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				lanes = state.lanes.toMutableList().apply { removeAt(indexOfFirst { it.id == id }) }
			)
		}
	}

	fun saveLanes() {
		viewModelScope.launch {
			withContext(ioDispatcher) {
				when (val state = _lanes.value) {
					LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
					is LaneFormUiState.Success -> {
						lanesRepository.overwriteAlleyLanes(alleyId, state.lanes)
						_lanes.value = LaneFormUiState.Dismissed
					}
				}
			}
		}
	}

	fun showLaneLabelDialog(laneId: UUID) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				laneLabelDialogState = state.lanes.firstOrNull { it.id == laneId }?.let {
					LaneLabelDialogState(
						laneId = it.id,
						label = it.label,
						position = it.position,
						isPositionDropDownExpanded = false,
					)
				}
			)
		}
	}

	fun toggleLaneLabelDialogPositionDropDown(expanded: Boolean) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				laneLabelDialogState = state.laneLabelDialogState?.copy(
					isPositionDropDownExpanded = expanded,
				)
			)
		}
	}

	fun updateLaneLabelDialogLabel(label: String) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				laneLabelDialogState = state.laneLabelDialogState?.copy(
					label = label,
					isPositionDropDownExpanded = false,
				)
			)
		}
	}

	fun updateLaneLabelDialogPosition(position: LanePosition) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				laneLabelDialogState = state.laneLabelDialogState?.copy(
					position = position,
					isPositionDropDownExpanded = false,
				)
			)
		}
	}

	fun dismissLaneLabelDialog(confirmChanges: Boolean) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				lanes = if (confirmChanges) {
					state.lanes.toMutableList().map {
						if (state.laneLabelDialogState?.laneId == it.id) {
							state.laneLabelDialogState.result(alleyId)
						} else {
							it
						}
					}
				} else {
					state.lanes
				},
				laneLabelDialogState = null,
			)
		}
	}

	fun showAddLanesDialog() {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				addLanesDialogState = AddLanesDialogState(1),
			)
		}
	}

	fun dismissAddLanesDialog() {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				addLanesDialogState = null,
			)
		}
	}

	fun updateLanesToAdd(lanesToAdd: Int) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				addLanesDialogState = state.addLanesDialogState?.copy(lanesToAdd = lanesToAdd)
			)
		}
	}

	fun addMultipleLanes(count: Int) {
		when (val state = _lanes.value) {
			LaneFormUiState.Loading, LaneFormUiState.Dismissed -> Unit
			is LaneFormUiState.Success -> _lanes.value = state.copy(
				lanes = state.lanes.toMutableList().apply {
					val labels = getNextLabels(count, state.lanes)
					labels.forEach {
						add(
							LaneCreate(
								id = UUID.randomUUID(),
								alleyId = alleyId,
								label = it,
								position = LanePosition.NO_WALL,
							)
						)
					}
				},
				addLanesDialogState = null,
				laneLabelDialogState = null,
			)
		}
	}

	private fun getNextLabels(count: Int, list: List<LaneCreate>): List<String> {
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

sealed interface LaneFormUiState {
	data object Loading: LaneFormUiState
	data object Dismissed: LaneFormUiState

	data class Success(
		val lanes: List<LaneCreate>,
		val addLanesDialogState: AddLanesDialogState?,
		val laneLabelDialogState: LaneLabelDialogState?
	): LaneFormUiState
}

data class AddLanesDialogState(
	val lanesToAdd: Int,
)

data class LaneLabelDialogState(
	val laneId: UUID,
	val label: String,
	val position: LanePosition,
	val isPositionDropDownExpanded: Boolean,
) {
	internal fun result(alleyId: UUID): LaneCreate = LaneCreate(
		alleyId = alleyId,
		id = laneId,
		label = label,
		position = position,
	)
}

