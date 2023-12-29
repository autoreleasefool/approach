package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.AlleyPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.BowlerPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.GearPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.LanePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.LeaguePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.ResourcePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.RESOURCE_FILTER
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.RESOURCE_TYPE
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.SELECTED_IDS
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.SELECTION_LIMIT
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.TITLE_OVERRIDE
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.R
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerFilter
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ResourcePickerViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
	gearRepository: GearRepository,
	alleysRepository: AlleysRepository,
	lanesRepository: LanesRepository,
	savedStateHandle: SavedStateHandle,
): ApproachViewModel<ResourcePickerScreenEvent>() {
	private val _uiState: MutableStateFlow<ResourcePickerScreenUiState> =
		MutableStateFlow(ResourcePickerScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val resourceType = savedStateHandle.get<String>(RESOURCE_TYPE)
		?.let { ResourcePickerType.valueOf(it) } ?: ResourcePickerType.BOWLER

	private val initiallySelectedIds = (savedStateHandle.get<String>(SELECTED_IDS) ?: "nan")
		.let { if (it == "nan") emptySet() else it.split(",").toSet() }
		.map(UUID::fromString)
		.toSet()

	private val limit = savedStateHandle.get<Int>(SELECTION_LIMIT) ?: 0

	private val filter: ResourcePickerFilter? = savedStateHandle.get<String>(RESOURCE_FILTER)
		?.let {
			if (it == "nan") null else when (resourceType) {
				ResourcePickerType.LEAGUE -> ResourcePickerFilter.Bowler(UUID.fromString(it))
				ResourcePickerType.GEAR -> ResourcePickerFilter.Gear(GearKind.valueOf(it))
				ResourcePickerType.LANE -> ResourcePickerFilter.Alley(UUID.fromString(it))
				ResourcePickerType.BOWLER, ResourcePickerType.ALLEY -> null
			}
		}

	private val titleOverride = savedStateHandle.get<String>(TITLE_OVERRIDE)
		?.let { if (it == "nan") null else it }

	private val dataProvider: ResourcePickerDataProvider = when (resourceType) {
		ResourcePickerType.BOWLER -> BowlerPickerDataProvider(bowlersRepository)
		ResourcePickerType.LEAGUE -> LeaguePickerDataProvider(leaguesRepository, (filter as? ResourcePickerFilter.Bowler)?.id)
		ResourcePickerType.GEAR -> GearPickerDataProvider(gearRepository, (filter as? ResourcePickerFilter.Gear)?.kind)
		ResourcePickerType.ALLEY -> AlleyPickerDataProvider(alleysRepository)
		ResourcePickerType.LANE -> LanePickerDataProvider(lanesRepository, (filter as? ResourcePickerFilter.Alley)?.id)
	}

	private fun getPickerUiState(): ResourcePickerUiState? {
		return when (val state = _uiState.value) {
			ResourcePickerScreenUiState.Loading -> null
			is ResourcePickerScreenUiState.Loaded -> state.picker
		}
	}

	private fun setPickerUiState(state: ResourcePickerUiState) {
		when (val uiState = _uiState.value) {
			ResourcePickerScreenUiState.Loading -> Unit
			is ResourcePickerScreenUiState.Loaded -> _uiState.value = uiState.copy(picker = state)
		}
	}

	fun handleAction(action: ResourcePickerScreenUiAction) {
		when (action) {
			ResourcePickerScreenUiAction.LoadResources -> loadResources()
			is ResourcePickerScreenUiAction.ResourcePickerAction -> handleResourcePickerAction(action.action)
		}
	}

	private fun handleResourcePickerAction(action: ResourcePickerUiAction) {
		when (action) {
			ResourcePickerUiAction.BackClicked -> sendEvent(ResourcePickerScreenEvent.Dismissed(initiallySelectedIds))
			ResourcePickerUiAction.DoneClicked -> sendEvent(ResourcePickerScreenEvent.Dismissed(getPickerUiState()?.selectedItems ?: initiallySelectedIds))
			is ResourcePickerUiAction.ItemClicked -> onResourceClicked(action.itemId)
		}
	}

	private fun loadResources() {
		viewModelScope.launch {
			val resources = dataProvider.loadResources()

			_uiState.value = ResourcePickerScreenUiState.Loaded(
				topBar = ResourcePickerTopBarUiState(
					titleResourceId = when (resourceType) {
						ResourcePickerType.BOWLER -> R.plurals.bowler_picker_title
						ResourcePickerType.LEAGUE -> R.plurals.league_picker_title
						ResourcePickerType.GEAR -> R.plurals.gear_picker_title
						ResourcePickerType.ALLEY -> R.plurals.alley_picker_title
						ResourcePickerType.LANE -> R.plurals.lane_picker_title
					},
					titleOverride = titleOverride,
					limit = limit,
				),
				picker = ResourcePickerUiState(
					items = resources,
					selectedItems = initiallySelectedIds,
					resourceType = resourceType,
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
			sendEvent(ResourcePickerScreenEvent.Dismissed(newSelectedIds))
		}
	}
}
