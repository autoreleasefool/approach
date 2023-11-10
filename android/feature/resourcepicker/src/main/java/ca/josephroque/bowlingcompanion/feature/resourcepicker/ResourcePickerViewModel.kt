package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.SELECTED_IDS
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.SELECTION_LIMIT
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.R
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.Resource
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerPickerViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository
): ViewModel() {
	private val _uiState: MutableStateFlow<ResourcePickerScreenUiState<BowlerResource>> =
		MutableStateFlow(ResourcePickerScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val _events: MutableStateFlow<ResourcePickerScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	private val bowlerIds = (savedStateHandle.get<String>(SELECTED_IDS) ?: "nan")
		.let { if (it == "nan") emptySet() else it.split(",").toSet() }
		.map(UUID::fromString)
		.toSet()

	private val limit = savedStateHandle.get<Int>(SELECTION_LIMIT) ?: 0

	private fun getPickerUiState(): ResourcePickerUiState<BowlerResource>? {
		return when (val state = _uiState.value) {
			ResourcePickerScreenUiState.Loading -> null
			is ResourcePickerScreenUiState.Loaded -> state.picker
		}
	}

	private fun setPickerUiState(state: ResourcePickerUiState<BowlerResource>) {
		when (val uiState = _uiState.value) {
			ResourcePickerScreenUiState.Loading -> Unit
			is ResourcePickerScreenUiState.Loaded -> _uiState.value = uiState.copy(picker = state)
		}
	}

	fun handleAction(action: ResourcePickerScreenUiAction<BowlerResource>) {
		when (action) {
			ResourcePickerScreenUiAction.LoadResources -> loadResources()
			is ResourcePickerScreenUiAction.ResourcePickerAction -> handleResourcePickerAction(action.action)
		}
	}

	private fun handleResourcePickerAction(action: ResourcePickerUiAction) {
		when (action) {
			ResourcePickerUiAction.BackClicked -> _events.value = ResourcePickerScreenEvent.Dismissed(bowlerIds)
			ResourcePickerUiAction.DoneClicked -> _events.value = ResourcePickerScreenEvent.Dismissed(getPickerUiState()?.selectedItems ?: bowlerIds)
			is ResourcePickerUiAction.ItemClicked -> onResourceClicked(action.itemId)
		}
	}

	private fun loadResources() {
		viewModelScope.launch {
			val bowlers = bowlersRepository.getBowlersList()
			val resources = bowlers.map { list ->
				list.map {
					BowlerResource(
						id = it.id,
						name = it.name,
					)
				}
			}.first()

			_uiState.value = ResourcePickerScreenUiState.Loaded(
				topBar = ResourcePickerTopBarUiState(
					titleResourceId = R.plurals.bowler_picker_title,
					limit = limit,
				),
				picker = ResourcePickerUiState(
					items = resources,
					selectedItems = bowlerIds,
				),
			)
		}
	}

	private fun onResourceClicked(id: UUID) {
		val state = getPickerUiState() ?: return
		val newSelectedIds = if (state.selectedItems.contains(id)) {
			state.selectedItems - id
		} else if (limit == 1) {
			setOf(id)
		} else if (state.selectedItems.size < limit || limit <= 0) {
			state.selectedItems + id
		} else {
			state.selectedItems
		}

		setPickerUiState(state.copy(selectedItems = newSelectedIds))

		if (limit == 1 && newSelectedIds.size == 1) {
			_events.value = ResourcePickerScreenEvent.Dismissed(newSelectedIds)
		}
	}
}

data class BowlerResource(
	override val id: UUID,
	val name: String,
): Resource