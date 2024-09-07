package ca.josephroque.bowlingcompanion.feature.teamdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.utils.range
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsFloatingActionButtonUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamMemberSeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamSeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamSeriesListItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	teamsRepository: TeamsRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<TeamDetailsScreenEvent>() {
	private val teamId = Route.TeamDetails.getTeam(savedStateHandle)!!

	private val seriesItemSize = userDataRepository.userData.map { it.seriesItemSize }
	private val seriesToArchive = MutableStateFlow<TeamSeriesListChartItem?>(null)
	private val isSeriesSortOrderMenuExpanded = MutableStateFlow(false)
	private val seriesSortOrder = MutableStateFlow(TeamSeriesSortOrder.NEWEST_TO_OLDEST)

	private val teamDetails = teamsRepository.getTeamDetails(teamId)
	private val teamSeries = seriesSortOrder.flatMapLatest { sortOrder ->
		teamSeriesRepository.getTeamSeriesList(teamId, sortOrder)
	}
	private val teamSeriesDetails =
		MutableStateFlow<Map<TeamSeriesID, TeamSeriesListChartItem>>(emptyMap())

	private val topBarUiState = combine(
		teamDetails,
		teamSeries,
		isSeriesSortOrderMenuExpanded,
		seriesSortOrder,
		seriesItemSize,
	) { teamDetails, teamSeries, isSeriesSortOrderMenuExpanded, seriesSortOrder, seriesItemSize ->
		TeamDetailsTopBarUiState(
			teamName = teamDetails.name,
			isSortOrderMenuVisible = teamSeries.isNotEmpty(),
			isSeriesItemSizeVisible = teamSeries.isNotEmpty(),
			isSortOrderMenuExpanded = isSeriesSortOrderMenuExpanded,
			sortOrder = seriesSortOrder,
			seriesItemSize = seriesItemSize,
		)
	}

	private val teamDetailsUiState = combine(
		teamDetails,
		teamSeries,
		teamSeriesDetails,
		seriesItemSize,
	) { teamDetails, seriesList, seriesDetails, seriesItemSize ->
		TeamDetailsUiState(
			seriesItemSize = seriesItemSize,
			members = teamDetails.members,
			series = seriesList.map {
				val details = seriesDetails[it.id]
				if (details != null && details.total > 0) {
					TeamSeriesListItem.Chart(details)
				} else {
					TeamSeriesListItem.Summary(
						TeamSeriesSummary(
							id = it.id,
							date = it.date,
							total = it.total,
						),
					)
				}
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
			TeamDetailsTopBarUiAction.AddSeriesClicked -> sendEvent(TeamDetailsScreenEvent.AddSeries(teamId))
			TeamDetailsTopBarUiAction.SortClicked -> isSeriesSortOrderMenuExpanded.value = true
			TeamDetailsTopBarUiAction.SortDismissed -> isSeriesSortOrderMenuExpanded.value = false
			is TeamDetailsTopBarUiAction.SortOrderClicked -> {
				seriesSortOrder.value = action.sortOrder
				isSeriesSortOrderMenuExpanded.value = false
			}
			is TeamDetailsTopBarUiAction.SeriesItemSizeToggled -> viewModelScope.launch {
				userDataRepository.setSeriesItemSize(action.itemSize)
			}
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
			is TeamDetailsUiAction.SeriesAppeared -> loadDetails(action.id)
			is TeamDetailsUiAction.AddSeriesClicked -> sendEvent(TeamDetailsScreenEvent.AddSeries(teamId))
		}
	}

	private fun loadDetails(teamSeriesId: TeamSeriesID) {
		viewModelScope.launch {
			val series = teamSeries.first().firstOrNull { it.id == teamSeriesId } ?: return@launch
			val details = teamSeriesRepository.getTeamSeriesDetails(teamSeriesId) ?: return@launch
			teamSeriesDetails.update {
				it.toMutableMap().apply {
					put(
						teamSeriesId,
						TeamSeriesListChartItem(
							id = series.id,
							date = series.date,
							total = series.total,
							numberOfGames = details.scores.size,
							scoreRange = details.scores.range(),
							chart = ChartEntryModelProducer(
								details.scores.mapIndexed { index, score ->
									entryOf(index.toFloat(), score.toFloat())
								},
							),
							members = details.members.map { member ->
								TeamMemberSeriesListChartItem(
									id = member.id,
									name = member.name,
									scoreRange = member.scores.range(),
									chart = ChartEntryModelProducer(
										member.scores.mapIndexed { index, score ->
											entryOf(index.toFloat(), score.toFloat())
										},
									),
								)
							},
						),
					)
				}
			}
		}
	}
}
