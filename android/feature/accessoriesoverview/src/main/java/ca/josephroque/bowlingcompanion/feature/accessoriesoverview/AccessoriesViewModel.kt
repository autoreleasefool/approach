package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiAction
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val ALLEYS_LIST_ITEM_LIMIT = 5
const val GEAR_LIST_ITEM_LIMIT = 10

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
	alleysRepository: AlleysRepository,
	gearRepository: GearRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<AccessoriesScreenUiEvent>() {
	private val isAccessoryMenuExpanded = MutableStateFlow(false)
	private val alleyToDelete: MutableStateFlow<AlleyListItem?> = MutableStateFlow(null)
	private val gearToDelete: MutableStateFlow<GearListItem?> = MutableStateFlow(null)

	private val alleysListState: Flow<AlleysListUiState> = combine(
		alleysRepository.getRecentAlleysList(limit = ALLEYS_LIST_ITEM_LIMIT),
		alleyToDelete,
	) { alleysList, alleyToDelete ->
		AlleysListUiState(alleysList, alleyToDelete = alleyToDelete)
	}

	private val gearListState: Flow<GearListUiState> = combine(
		gearRepository.getRecentlyUsedGear(limit = GEAR_LIST_ITEM_LIMIT),
		gearToDelete,
	) { gearList, gearToDelete ->
		GearListUiState(gearList, gearToDelete = gearToDelete)
	}

	val uiState: StateFlow<AccessoriesScreenUiState> = combine(
		isAccessoryMenuExpanded,
		alleysListState,
		gearListState,
	) { isAccessoryMenuExpanded, alleysList, gearList ->
		AccessoriesScreenUiState.Loaded(
			accessories = AccessoriesUiState(
				isAccessoryMenuExpanded = isAccessoryMenuExpanded,
				alleysList = alleysList,
				gearList = gearList,
				alleysItemLimit = ALLEYS_LIST_ITEM_LIMIT,
				gearItemLimit = GEAR_LIST_ITEM_LIMIT,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = AccessoriesScreenUiState.Loading,
	)

	fun handleAction(action: AccessoriesScreenUiAction) {
		when (action) {
			AccessoriesScreenUiAction.DidAppear -> showAccessoriesOnboardingIfRequired()
			is AccessoriesScreenUiAction.Accessories -> handleAccessoryAction(action.action)
		}
	}

	private fun handleAccessoryAction(action: AccessoriesUiAction) {
		when (action) {
			AccessoriesUiAction.AddAccessoryClicked -> setAccessoryMenu(isExpanded = true)
			AccessoriesUiAction.AccessoryMenuDismissed -> setAccessoryMenu(isExpanded = false)
			AccessoriesUiAction.ViewAllAlleysClicked -> sendEvent(AccessoriesScreenUiEvent.ViewAllAlleys)
			AccessoriesUiAction.ViewAllGearClicked -> sendEvent(AccessoriesScreenUiEvent.ViewAllGear)
			AccessoriesUiAction.AddAlleyClicked -> {
				setAccessoryMenu(isExpanded = false)
				sendEvent(AccessoriesScreenUiEvent.AddAlley)
			}
			AccessoriesUiAction.AddGearClicked -> {
				setAccessoryMenu(isExpanded = false)
				sendEvent(AccessoriesScreenUiEvent.AddGear)
			}
			is AccessoriesUiAction.AlleyClicked -> sendEvent(
				AccessoriesScreenUiEvent.ShowAlleyDetails(action.alley.alleyId),
			)
			is AccessoriesUiAction.GearClicked -> sendEvent(
				AccessoriesScreenUiEvent.ShowGearDetails(action.gear.gearId),
			)
		}
	}

	private fun setAccessoryMenu(isExpanded: Boolean) {
		isAccessoryMenuExpanded.value = isExpanded
	}

	private fun showAccessoriesOnboardingIfRequired() {
		viewModelScope.launch {
			val userData = userDataRepository.userData.first()
			if (!userData.hasOpenedAccessoriesTab) {
				sendEvent(AccessoriesScreenUiEvent.ShowAccessoriesOnboarding)
			}
		}
	}
}
