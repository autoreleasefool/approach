package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

const val alleysListItemLimit = 5
const val gearListItemLimit = 10

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
	alleysRepository: AlleysRepository,
	gearRepository: GearRepository,
): ViewModel() {
	private val _uiState = MutableStateFlow(AccessoriesUiState(alleysItemLimit = alleysListItemLimit, gearItemLimit = gearListItemLimit))
	val uiState: StateFlow<AccessoriesUiState> =
		_uiState.asStateFlow()

	// FIXME: Refactor to AccessoriesScreenUiState, remove optional
	val alleysListState: StateFlow<AlleysListUiState?> =
		alleysRepository.getRecentAlleysList(limit = alleysListItemLimit)
			.map { AlleysListUiState(it, alleyToDelete = null) }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = null,
			)

	// FIXME: Refactor to AccessoriesScreenUiState, remove optional
	val gearListState: StateFlow<GearListUiState?> =
		gearRepository.getRecentlyUsedGear(limit = gearListItemLimit)
			.map { GearListUiState(it, gearToDelete = null) }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = null,
			)

	fun expandAccessoryMenu() {
		_uiState.update { it.copy(isAccessoryMenuExpanded = true)  }
	}

	fun minimizeAccessoryMenu() {
		_uiState.update { it.copy(isAccessoryMenuExpanded = false)  }
	}
}