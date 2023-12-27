package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GearListViewModel @Inject constructor(
	private val gearRepository: GearRepository,
): ApproachViewModel<GearListScreenEvent>() {
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

	fun handleAction(action: GearListScreenUiAction) {
		when (action) {
			is GearListScreenUiAction.GearListAction -> handleGearListAction(action.action)
		}
	}

	private fun handleGearListAction(action: GearListUiAction) {
		when (action) {
			GearListUiAction.BackClicked -> sendEvent(GearListScreenEvent.Dismissed)
			GearListUiAction.AddGearClicked -> sendEvent(GearListScreenEvent.NavigateToAddGear)
			is GearListUiAction.GearClicked -> sendEvent(GearListScreenEvent.NavigateToEditGear(action.id))
			is GearListUiAction.GearEdited -> sendEvent(GearListScreenEvent.NavigateToEditGear(action.id))
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
		_gearListTopBarState.update {
			it.copy(isFilterMenuVisible = isVisible)
		}
	}

	private fun setGearFilter(gearKind: GearKind?) {
		_gearListTopBarState.update {
			it.copy(
				isFilterMenuVisible = false,
				kindFilter = gearKind,
			)
		}
		_gearKind.value = gearKind
	}
}


