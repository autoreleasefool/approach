package ca.josephroque.bowlingcompanion.feature.teamdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsFloatingActionButtonUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamSeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamSeriesListItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val teamsRepository: TeamsRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val seriesRepository: SeriesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<TeamDetailsScreenEvent>() {
	private val teamId = Route.TeamDetails.getTeam(savedStateHandle)!!

	private val seriesItemSize = userDataRepository.userData.map { it.seriesItemSize }
	private val seriesToArchive = MutableStateFlow<TeamSeriesListChartItem?>(null)
	private val isSeriesSortOrderMenuExpanded = MutableStateFlow(false)
	private val seriesSortOrder = MutableStateFlow(SeriesSortOrder.NEWEST_TO_OLDEST)

	private val seriesChartModelProducers = mutableMapOf<TeamSeriesID, ChartEntryModelProducer>()

	private val teamDetails = teamsRepository.getTeamDetails(teamId)

	private val seriesList = seriesSortOrder.flatMapLatest { sortOrder ->
		teamSeriesRepository.getTeamSeriesList(teamId, sortOrder)
	}

	private val topBarUiState = teamDetails.map {
		TeamDetailsTopBarUiState(teamName = it.name)
	}

	private val teamDetailsUiState = combine(
		teamDetails,
		seriesList,
	) { teamDetails, seriesList ->
		TeamDetailsUiState(
			members = teamDetails.members,
			series = seriesList.map {
				TeamSeriesListItem.Summary(
					TeamSeriesSummary(
						id = it.id,
						date = it.date,
						total = it.total,
					),
				)
			},
		)
	}

	val uiState: StateFlow<TeamDetailsScreenUiState> = combine(
		topBarUiState,
		teamDetailsUiState,
	) { topBar, teamDetails ->
		TeamDetailsScreenUiState.Loaded(
			topBar = topBar,
			teamDetails = teamDetails,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = TeamDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			recentlyUsedRepository.didRecentlyUseTeam(teamId)
		}
	}

	fun handleAction(action: TeamDetailsScreenUiAction) {
		when (action) {
			is TeamDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is TeamDetailsScreenUiAction.FloatingActionButton -> handleFloatingActionButtonAction(
				action.action,
			)
			is TeamDetailsScreenUiAction.TeamDetails -> handleTeamDetailsAction(action.action)
		}
	}

	private fun handleTopBarAction(action: TeamDetailsTopBarUiAction) {
		when (action) {
			TeamDetailsTopBarUiAction.BackClicked -> sendEvent(TeamDetailsScreenEvent.Dismissed)
		}
	}

	private fun handleFloatingActionButtonAction(action: TeamDetailsFloatingActionButtonUiAction) {
		when (action) {
			TeamDetailsFloatingActionButtonUiAction.AddSeriesClicked -> sendEvent(
				TeamDetailsScreenEvent.AddSeries(teamId),
			)
		}
	}

	private fun handleTeamDetailsAction(action: TeamDetailsUiAction) {
		when (action) {
			is TeamDetailsUiAction.AddSeriesClicked -> sendEvent(TeamDetailsScreenEvent.AddSeries(teamId))
		}
	}
}
