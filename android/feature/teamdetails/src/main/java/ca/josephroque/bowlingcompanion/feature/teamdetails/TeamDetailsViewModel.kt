package ca.josephroque.bowlingcompanion.feature.teamdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.utils.range
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesGameDetails
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.ArchiveSeriesUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.EditSeriesUiState
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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	teamsRepository: TeamsRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<TeamDetailsScreenEvent>() {
	private val teamId = Route.TeamDetails.getTeam(savedStateHandle)!!

	private val seriesToArchive = MutableStateFlow<TeamSeriesListItem?>(null)

	private val seriesToEdit = MutableStateFlow<TeamSeriesListItem?>(null)
	private val seriesToEditDate = MutableStateFlow(Clock.System.now().toLocalDate())

	private val seriesItemSize = userDataRepository.userData.map { it.seriesItemSize }
	private val isSeriesSortOrderMenuExpanded = MutableStateFlow(false)
	private val seriesSortOrder = MutableStateFlow(TeamSeriesSortOrder.NEWEST_TO_OLDEST)
	private val isArchiveMemberSeriesVisible = MutableStateFlow(false)

	private val teamDetails = teamsRepository.getTeamDetails(teamId)
	private val teamSeries = seriesSortOrder.flatMapLatest { sortOrder ->
		teamSeriesRepository.getTeamSeriesList(teamId, sortOrder)
	}
	private val teamSeriesDetails =
		MutableStateFlow<Map<TeamSeriesID, TeamSeriesListChartItem>>(emptyMap())

	private val teamSeriesDetailsList = combine(
		teamSeries,
		teamSeriesDetails,
	) { seriesList, seriesDetails ->
		seriesList.map {
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
		}
	}

	private val editSeriesUiState = combine(
		seriesToEdit,
		seriesToEditDate,
	) { seriesToEdit, date ->
		EditSeriesUiState(
			seriesToEdit = seriesToEdit,
			date = date,
		)
	}

	private val archiveSeriesUiState = combine(
		seriesToArchive,
		isArchiveMemberSeriesVisible,
	) { seriesToArchive, isArchiveMemberSeriesVisible ->
		ArchiveSeriesUiState(
			seriesToArchive = seriesToArchive,
			isArchiveMemberSeriesVisible = seriesToArchive != null && isArchiveMemberSeriesVisible,
		)
	}

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
		teamSeriesDetailsList,
		seriesItemSize,
		archiveSeriesUiState,
		editSeriesUiState,
	) { teamDetails, teamSeriesDetailsList, seriesItemSize, archiveSeriesUiState, editSeriesUiState ->
		TeamDetailsUiState(
			seriesToArchive = archiveSeriesUiState,
			seriesItemSize = seriesItemSize,
			members = teamDetails.members,
			series = teamSeriesDetailsList,
			seriesToEdit = editSeriesUiState,
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
			is TeamDetailsUiAction.SeriesClicked -> sendEvent(
				TeamDetailsScreenEvent.ViewSeries(action.series.id),
			)
			is TeamDetailsUiAction.EditSeriesClicked -> setSeriesToEdit(action.series)
			is TeamDetailsUiAction.ArchiveSeriesClicked -> seriesToArchive.value = action.series
			is TeamDetailsUiAction.SeriesDateChanged -> updateSeriesDate(action.date)
			TeamDetailsUiAction.DismissEditSeriesClicked -> setSeriesToEdit(null)
			TeamDetailsUiAction.ConfirmArchiveClicked -> isArchiveMemberSeriesVisible.value = true
			TeamDetailsUiAction.DismissArchiveClicked -> seriesToArchive.value = null
			TeamDetailsUiAction.ArchiveMemberSeriesClicked -> archiveSeries(archiveMemberSeries = true)
			TeamDetailsUiAction.KeepMemberSeriesClicked -> archiveSeries(archiveMemberSeries = false)
			TeamDetailsUiAction.DismissArchiveMemberSeriesClicked -> {
				seriesToArchive.value = null
				isArchiveMemberSeriesVisible.value = false
			}
		}
	}

	private fun setSeriesToEdit(series: TeamSeriesListItem?) {
		seriesToEdit.value = series
		seriesToEditDate.value = series?.date ?: Clock.System.now().toLocalDate()
	}

	private fun updateSeriesDate(date: LocalDate) {
		val seriesToEdit = seriesToEdit.value ?: return
		viewModelScope.launch {
			teamSeriesRepository.updateTeamSeriesDate(seriesToEdit.id, date)
		}

		setSeriesToEdit(null)
	}

	private fun archiveSeries(archiveMemberSeries: Boolean) {
		val seriesToArchive = seriesToArchive.value ?: return
		viewModelScope.launch {
			teamSeriesRepository.archiveTeamSeries(seriesToArchive.id, archiveMemberSeries)
			this@TeamDetailsViewModel.seriesToArchive.value = null
			isArchiveMemberSeriesVisible.value = false
		}
	}

	private fun loadDetails(teamSeriesId: TeamSeriesID) {
		viewModelScope.launch {
			val series = teamSeries.first().firstOrNull { it.id == teamSeriesId } ?: return@launch
			val details = teamSeriesRepository.getTeamSeriesDetails(teamSeriesId).first()
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
									scoreRange = member.games.map(TeamSeriesGameDetails::score).range(),
									chart = ChartEntryModelProducer(
										details.scores.indices.map { gameIndex ->
											val game = member.games.firstOrNull { game -> game.index == gameIndex }
											if (game == null) {
												entryOf(gameIndex.toFloat(), 0f)
											} else {
												entryOf(gameIndex.toFloat(), game.score.toFloat())
											}
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
