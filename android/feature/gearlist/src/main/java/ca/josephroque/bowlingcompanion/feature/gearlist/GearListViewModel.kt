package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GearListViewModel @Inject constructor(
	gearRepository: GearRepository,
): ViewModel() {
	private val _gearKind: MutableStateFlow<GearKind?> = MutableStateFlow(null)

	private val _gearListTopBarState: MutableStateFlow<GearListTopBarState> = MutableStateFlow(GearListTopBarState())
	val gearListTopBarState = _gearListTopBarState.asStateFlow()

	val gearListState: StateFlow<GearListUiState> =
		_gearKind
			.flatMapLatest { gearRepository.getGearList(it) }
			.map(GearListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = GearListUiState.Loading,
			)

	fun updateGearFilter(gearKind: GearKind?) {
		_gearListTopBarState.value = _gearListTopBarState.value.copy(
			isFilterMenuVisible = false,
			kindFilter = gearKind,
		)
		_gearKind.value = gearKind
	}

	fun showGearFilter() {
		_gearListTopBarState.value = _gearListTopBarState.value.copy(isFilterMenuVisible = true)
	}

	fun hideGearFilter() {
		_gearListTopBarState.value = _gearListTopBarState.value.copy(isFilterMenuVisible = false)
	}
}

