package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val alleysListItemLimit = 5
const val gearListItemLimit = 10

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
	alleysRepository: AlleysRepository,
	gearRepository: GearRepository,
	private val userDataRepository: UserDataRepository,
): ViewModel() {
	private val _isAccessoryMenuExpanded = MutableStateFlow(false)

	val uiState: StateFlow<AccessoriesUiState> = combine(
		_isAccessoryMenuExpanded,
		userDataRepository.userData.map { it.hasOpenedAccessoriesTab },
	) { isAccessoryMenuExpanded, hasOpenedAccessoriesTab ->
		AccessoriesUiState(
			isAccessoryMenuExpanded = isAccessoryMenuExpanded,
			isAccessoryOnboardingVisible = !hasOpenedAccessoriesTab,
			alleysItemLimit = alleysListItemLimit,
			gearItemLimit = gearListItemLimit,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = AccessoriesUiState(
			isAccessoryMenuExpanded = false,
			isAccessoryOnboardingVisible = false,
			alleysItemLimit = alleysListItemLimit,
			gearItemLimit = gearListItemLimit,
		)
	)

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
		_isAccessoryMenuExpanded.value = true
	}

	fun minimizeAccessoryMenu() {
		_isAccessoryMenuExpanded.value = false
	}

	fun didDismissAccessoriesSummary() {
		viewModelScope.launch {
			userDataRepository.didOpenAccessoriesTab()
		}
	}
}