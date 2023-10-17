package ca.josephroque.bowlingcompanion.feature.accessories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.feature.alleyslist.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

const val alleysListItemLimit = 5
const val gearListItemLimit = 10

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
	alleysRepository: AlleysRepository,
	gearRepository: GearRepository,
): ViewModel() {
	val _uiState = MutableStateFlow(AccessoriesUiState())
	val uiState: StateFlow<AccessoriesUiState> =
		_uiState.asStateFlow()

	val alleysListState: StateFlow<AlleysListUiState> =
		alleysRepository.getRecentAlleysList(limit = alleysListItemLimit)
			.map(AlleysListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = AlleysListUiState.Loading,
			)

	val gearListState: StateFlow<GearListUiState> =
		gearRepository.getRecentlyUsedGear(limit = gearListItemLimit)
			.map(GearListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = GearListUiState.Loading,
			)

	fun expandAccessoryMenu() {
		_uiState.value = _uiState.value.copy(isAccessoryMenuExpanded = true)
	}

	fun minimizeAccessoryMenu() {
		_uiState.value = _uiState.value.copy(isAccessoryMenuExpanded = false)
	}
}

data class AccessoriesUiState(
	val isAccessoryMenuExpanded: Boolean = false,
)