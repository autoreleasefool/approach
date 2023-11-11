package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiAction
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GearListViewModel @Inject constructor(
	private val gearRepository: GearRepository,
): ViewModel() {
	private val _gearToDelete: MutableStateFlow<GearListItem?> = MutableStateFlow(null)

	private val _gearKind: MutableStateFlow<GearKind?> = MutableStateFlow(null)
	private val _gearList = _gearKind.flatMapLatest { gearRepository.getGearList(it) }

	private val _gearListState: Flow<GearListUiState> =
		combine(
			_gearToDelete,
			_gearList,
		) { gearToDelete, gearList ->
			GearListUiState(
				list = gearList,
				gearToDelete = gearToDelete,)
		}

	private val _gearListTopBarState: MutableStateFlow<GearListTopBarUiState> =
		MutableStateFlow(GearListTopBarUiState())

	val uiState: StateFlow<GearListScreenUiState> =
		combine(
			_gearListState,
			_gearListTopBarState,
		) { gearList, gearListTopBar ->
			GearListScreenUiState.Loaded(
				gearList = gearList,
				topBar = gearListTopBar,
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = GearListScreenUiState.Loading,
		)

	private val _events: MutableStateFlow<GearListScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: GearListScreenUiAction) {
		when (action) {
			GearListScreenUiAction.HandledNavigation -> _events.value = null
			is GearListScreenUiAction.GearListAction -> handleGearListAction(action.action)
		}
	}

	private fun handleGearListAction(action: GearListUiAction) {
		when (action) {
			GearListUiAction.BackClicked -> _events.value = GearListScreenEvent.Dismissed
			GearListUiAction.AddGearClicked -> _events.value = GearListScreenEvent.NavigateToAddGear
			is GearListUiAction.GearClicked -> _events.value = GearListScreenEvent.NavigateToEditGear(action.id)
			is GearListUiAction.GearEdited -> _events.value = GearListScreenEvent.NavigateToEditGear(action.id)
			is GearListUiAction.GearDeleted -> setDeleteGearPrompt(gear = action.gear)
			GearListUiAction.ConfirmDeleteClicked -> deleteGear()
			GearListUiAction.DismissDeleteClicked -> setDeleteGearPrompt(gear = null)
			GearListUiAction.FilterMenuClicked -> setGearFilterMenu(isVisible = true)
			GearListUiAction.FilterMenuDismissed -> setGearFilterMenu(isVisible = false)
			is GearListUiAction.FilterClicked -> setGearFilter(action.filter)
		}
	}

	private fun setDeleteGearPrompt(gear: GearListItem?) {
		_gearToDelete.value = gear
	}

	private fun deleteGear() {
		val gearToDelete = _gearToDelete.value ?: return
		viewModelScope.launch {
			gearRepository.deleteGear(gearToDelete.id)
			setDeleteGearPrompt(gear = null)
		}
	}

	private fun setGearFilterMenu(isVisible: Boolean) {
		_gearListTopBarState.value = _gearListTopBarState.value.copy(isFilterMenuVisible = isVisible)
	}

	private fun setGearFilter(gearKind: GearKind?) {
		_gearListTopBarState.value = _gearListTopBarState.value.copy(
			isFilterMenuVisible = false,
			kindFilter = gearKind,
		)
		_gearKind.value = gearKind
	}
}


