package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiAction
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val STATISTICS_WIDGET_CONTEXT = "bowler_details"

@HiltViewModel
class BowlerDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	bowlersRepository: BowlersRepository,
	userDataRepository: UserDataRepository,
	statisticsWidgetsRepository: StatisticsWidgetsRepository,
	private val gearRepository: GearRepository,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
): ApproachViewModel<BowlerDetailsScreenEvent>() {
	private val bowlerId = UUID.fromString(savedStateHandle[BOWLER_ID])

	private val _leagueToArchive: MutableStateFlow<LeagueListItem?> = MutableStateFlow(null)

	private val _leaguesListState: Flow<LeaguesListUiState> = combine(
		leaguesRepository.getLeaguesList(bowlerId),
		_leagueToArchive,
	) { leaguesList, leagueToArchive ->
		LeaguesListUiState(
			list = leaguesList,
			leagueToArchive = leagueToArchive,
		)
	}

	private val _widgets = userDataRepository.userData
		.map { it.isHidingWidgetsInLeaguesList }
		.flatMapLatest {
			if (it) flowOf(null)
			else statisticsWidgetsRepository.getStatisticsWidgets(STATISTICS_WIDGET_CONTEXT)
		}

	val uiState: StateFlow<BowlerDetailsScreenUiState> = combine(
		_leaguesListState,
		_widgets,
		bowlersRepository.getBowlerDetails(bowlerId),
		gearRepository.getBowlerPreferredGear(bowlerId),
	) { leaguesList, widgets, bowlerDetails, gearList ->
		BowlerDetailsScreenUiState.Loaded(
			bowler = BowlerDetailsUiState(
				bowler = bowlerDetails,
				leaguesList = leaguesList,
				gearList = GearListUiState(gearList, gearToDelete = null),
				topBar = BowlerDetailsTopBarUiState(bowlerDetails.name),
				widgets = widgets?.let { StatisticsWidgetLayoutUiState(isEditing = false, widgets = it) },
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = BowlerDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			recentlyUsedRepository.didRecentlyUseBowler(bowlerId)
		}
	}

	fun handleAction(action: BowlerDetailsScreenUiAction) {
		when (action) {
			is BowlerDetailsScreenUiAction.PreferredGearSelected -> setBowlerPreferredGear(action.gear)
			is BowlerDetailsScreenUiAction.BowlerDetailsAction -> handleBowlerDetailsAction(action.action)
		}
	}

	private fun handleBowlerDetailsAction(action: BowlerDetailsUiAction) {
		when (action) {
			BowlerDetailsUiAction.BackClicked -> sendEvent(BowlerDetailsScreenEvent.Dismissed)
			BowlerDetailsUiAction.AddLeagueClicked -> sendEvent(BowlerDetailsScreenEvent.AddLeague(bowlerId))
			BowlerDetailsUiAction.EditStatisticsWidgetClicked -> sendEvent(BowlerDetailsScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT))
			BowlerDetailsUiAction.ManageGearClicked -> showPreferredGearPicker()
			is BowlerDetailsUiAction.GearClicked -> sendEvent(BowlerDetailsScreenEvent.ShowGearDetails(action.id))
			is BowlerDetailsUiAction.LeaguesListAction -> handleLeaguesListAction(action.action)
			is BowlerDetailsUiAction.StatisticsWidgetLayout -> handleStatisticsWidgetLayoutAction(action.action)
		}
	}

	private fun handleLeaguesListAction(action: LeaguesListUiAction) {
		when (action) {
			LeaguesListUiAction.AddLeagueClicked -> sendEvent(BowlerDetailsScreenEvent.AddLeague(bowlerId))
			is LeaguesListUiAction.LeagueClicked -> sendEvent(BowlerDetailsScreenEvent.ShowLeagueDetails(action.id))
			is LeaguesListUiAction.LeagueEdited -> sendEvent(BowlerDetailsScreenEvent.EditLeague(action.id))
			is LeaguesListUiAction.LeagueArchived -> setLeagueArchivePrompt(action.league)
			is LeaguesListUiAction.ConfirmArchiveClicked -> archiveLeague()
			is LeaguesListUiAction.DismissArchiveClicked -> setLeagueArchivePrompt(null)
		}
	}

	private fun handleStatisticsWidgetLayoutAction(action: StatisticsWidgetLayoutUiAction) {
		when (action) {
			is StatisticsWidgetLayoutUiAction.WidgetClicked -> sendEvent(BowlerDetailsScreenEvent.ShowStatistics(action.widget.id))
			is StatisticsWidgetLayoutUiAction.ChangeLayoutClicked -> sendEvent(BowlerDetailsScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT))
		}
	}

	private fun setBowlerPreferredGear(gear: Set<UUID>) {
		viewModelScope.launch {
			gearRepository.setBowlerPreferredGear(bowlerId, gear)
		}
	}

	private fun setLeagueArchivePrompt(league: LeagueListItem?) {
		_leagueToArchive.value = league
	}

	private fun archiveLeague() {
		val leagueToArchive = _leagueToArchive.value ?: return
		viewModelScope.launch {
			leaguesRepository.archiveLeague(leagueToArchive.id)
			setLeagueArchivePrompt(null)
		}
	}

	private fun showPreferredGearPicker() {
		val selectedGear =
			(uiState.value as? BowlerDetailsScreenUiState.Loaded)?.bowler?.gearList?.list?.map { it.id }?.toSet()
				?: return
		sendEvent(BowlerDetailsScreenEvent.ShowPreferredGearPicker(selectedGear))
	}
}