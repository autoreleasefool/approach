package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.AlleyPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.BowlerPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.GamePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.GearPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.LanePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.LeaguePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.ResourcePickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.SeriesPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.data.TeamPickerDataProvider
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.R
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerFilter
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ResourcePickerViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
	seriesRepository: SeriesRepository,
	gamesRepository: GamesRepository,
	gearRepository: GearRepository,
	alleysRepository: AlleysRepository,
	lanesRepository: LanesRepository,
	teamsRepository: TeamsRepository,
	savedStateHandle: SavedStateHandle,
) : ApproachViewModel<ResourcePickerScreenEvent>() {
	private val _uiState: MutableStateFlow<ResourcePickerScreenUiState> =
		MutableStateFlow(ResourcePickerScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val resultKey = ResourcePickerResultKey(
		Route.ResourcePicker.getResultKey(savedStateHandle)!!,
	)
	private val resourceType = Route.ResourcePicker.getResourceType(savedStateHandle)!!
	private val initiallySelectedIds = Route.ResourcePicker.getSelectedIds(savedStateHandle)
	private val hiddenIds = Route.ResourcePicker.getHiddenIds(savedStateHandle)
	private val limit = Route.ResourcePicker.getLimit(savedStateHandle) ?: 0
	private val titleOverride = Route.ResourcePicker.getTitleOverride(savedStateHandle)

	private val filter: ResourcePickerFilter? = Route.ResourcePicker.getResourceFilter(
		savedStateHandle,
	)
		?.let {
			when (resourceType) {
				ResourcePickerType.LEAGUE -> ResourcePickerFilter.Str(it)
				ResourcePickerType.GEAR -> ResourcePickerFilter.Gear(GearKind.valueOf(it))
				ResourcePickerType.LANE -> ResourcePickerFilter.Alley(AlleyID.fromString(it))
				ResourcePickerType.SERIES -> ResourcePickerFilter.Str(it)
				ResourcePickerType.GAME -> ResourcePickerFilter.Series(SeriesID.fromString(it))
				ResourcePickerType.BOWLER -> ResourcePickerFilter.BowlerKind(BowlerKind.valueOf(it))
				ResourcePickerType.ALLEY, ResourcePickerType.TEAM -> null
			}
		}

	private val dataProvider: ResourcePickerDataProvider = when (resourceType) {
		ResourcePickerType.BOWLER -> BowlerPickerDataProvider(
			bowlersRepository,
			(filter as? ResourcePickerFilter.BowlerKind)?.kind,
		)
		ResourcePickerType.LEAGUE -> LeaguePickerDataProvider(
			leaguesRepository,
			(filter as? ResourcePickerFilter.Str)?.value ?: "",
		)
		ResourcePickerType.SERIES -> SeriesPickerDataProvider(
			seriesRepository,
			(filter as? ResourcePickerFilter.Str)?.value ?: "",
		)
		ResourcePickerType.GAME -> GamePickerDataProvider(
			gamesRepository,
			(filter as? ResourcePickerFilter.Series)?.id,
		)
		ResourcePickerType.GEAR -> GearPickerDataProvider(
			gearRepository,
			(filter as? ResourcePickerFilter.Gear)?.kind,
		)
		ResourcePickerType.ALLEY -> AlleyPickerDataProvider(alleysRepository)
		ResourcePickerType.LANE -> LanePickerDataProvider(
			lanesRepository,
			(filter as? ResourcePickerFilter.Alley)?.id,
		)
		ResourcePickerType.TEAM -> TeamPickerDataProvider(teamsRepository)
	}

	private fun getPickerUiState(): ResourcePickerUiState? = when (val state = _uiState.value) {
		ResourcePickerScreenUiState.Loading -> null
		is ResourcePickerScreenUiState.Loaded -> state.picker
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
			ResourcePickerUiAction.BackClicked -> sendEvent(
				ResourcePickerScreenEvent.Dismissed(resultKey, initiallySelectedIds),
			)
			ResourcePickerUiAction.DoneClicked -> sendEvent(
				ResourcePickerScreenEvent.Dismissed(
					resultKey,
					getPickerUiState()?.selectedItems ?: initiallySelectedIds,
				),
			)
			is ResourcePickerUiAction.ItemClicked -> onResourceClicked(action.itemId)
		}
	}

	private fun loadResources() {
		viewModelScope.launch {
			val resources = dataProvider
				.loadResources()
				.filter { !hiddenIds.contains(it.id) }

			_uiState.value = ResourcePickerScreenUiState.Loaded(
				topBar = ResourcePickerTopBarUiState(
					titleResourceId = when (resourceType) {
						ResourcePickerType.BOWLER -> R.plurals.bowler_picker_title
						ResourcePickerType.LEAGUE -> R.plurals.league_picker_title
						ResourcePickerType.SERIES -> R.plurals.series_picker_title
						ResourcePickerType.GAME -> R.plurals.game_picker_title
						ResourcePickerType.GEAR -> R.plurals.gear_picker_title
						ResourcePickerType.ALLEY -> R.plurals.alley_picker_title
						ResourcePickerType.LANE -> R.plurals.lane_picker_title
						ResourcePickerType.TEAM -> R.plurals.team_picker_title
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
			sendEvent(ResourcePickerScreenEvent.Dismissed(resultKey, newSelectedIds))
		}
	}
}
