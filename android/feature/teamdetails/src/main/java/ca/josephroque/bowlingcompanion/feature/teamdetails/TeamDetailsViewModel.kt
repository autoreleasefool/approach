package ca.josephroque.bowlingcompanion.feature.teamdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val teamsRepository: TeamsRepository,
	private val seriesRepository: SeriesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
) : ApproachViewModel<TeamDetailsScreenEvent>() {
	private val teamId = Route.TeamDetails.getTeam(savedStateHandle)!!

	private val seriesChartModelProducers: MutableMap<UUID, ChartEntryModelProducer> = mutableMapOf()

	private val teamDetails = teamsRepository.getTeamDetails(teamId)

	private val topBarUiState = teamDetails.map {
		TeamDetailsTopBarUiState(teamName = it.name)
	}

	private val teamDetailsUiState = teamDetails.map {
		TeamDetailsUiState(
			members = it.members,
			series = emptyList(),
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
			is TeamDetailsScreenUiAction.SeriesAdded -> showSeriesDetails(action.teamSeriesId)
			is TeamDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is TeamDetailsScreenUiAction.TeamDetails -> TODO()
		}
	}

	private fun handleTopBarAction(action: TeamDetailsTopBarUiAction) {
		when (action) {
			TeamDetailsTopBarUiAction.BackClicked -> sendEvent(TeamDetailsScreenEvent.Dismissed)
			TeamDetailsTopBarUiAction.AddSeriesClicked -> sendEvent(TeamDetailsScreenEvent.AddSeries(teamId))
		}
	}

	private fun showSeriesDetails(teamSeriesId: UUID) {
		TODO()
	}
}
