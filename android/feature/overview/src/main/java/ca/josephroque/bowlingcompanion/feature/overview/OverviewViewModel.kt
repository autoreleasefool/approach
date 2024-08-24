package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.bowler.BowlerViewed
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.hasModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetID
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTab
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBarUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import ca.josephroque.bowlingcompanion.feature.teamslist.ui.TeamsListUiAction
import ca.josephroque.bowlingcompanion.feature.teamslist.ui.TeamsListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

private const val STATISTICS_WIDGET_CONTEXT = "overview"

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val teamsRepository: TeamsRepository,
	private val gamesRepository: GamesRepository,
	statisticsWidgetsRepository: StatisticsWidgetsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
	private val featureFlagsClient: FeatureFlagsClient,
) : ApproachViewModel<OverviewScreenEvent>() {
	private val selectedTab = MutableStateFlow(OverviewTab.BOWLERS)
	private val isGameInProgressSnackBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

	private val teamToDelete: MutableStateFlow<TeamListItem?> = MutableStateFlow(null)
	private val isTeamSortOrderMenuExpanded = MutableStateFlow(false)
	private val teamSortOrder = MutableStateFlow(TeamSortOrder.MOST_RECENTLY_USED)

	private val teams = teamSortOrder.flatMapLatest { sortOrder ->
		teamsRepository.getTeamList(sortOrder)
	}

	private val teamsListUiState: Flow<TeamsListUiState> =
		combine(teams, teamToDelete) { teamsList, teamToDelete ->
			TeamsListUiState(
				list = teamsList,
				teamToDelete = teamToDelete,
			)
		}

	private val teamsTopBar: Flow<OverviewTopBarUiState.TeamTab> =
		combine(
			teams,
			isTeamSortOrderMenuExpanded,
			teamSortOrder,
		) { teams, isTeamSortOrderMenuExpanded, teamSortOrder ->
			OverviewTopBarUiState.TeamTab(
				isSortOrderMenuVisible = teams.isNotEmpty(),
				isSortOrderMenuExpanded = isTeamSortOrderMenuExpanded,
				sortOrder = teamSortOrder,
			)
		}

	private val isShowingSwipeHint = userDataRepository.userData
		.map { !it.isSwipeRowsTipDismissed }

	private val bowlerToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)
	private val isBowlerSortOrderMenuExpanded = MutableStateFlow(false)
	private val bowlerSortOrder = MutableStateFlow(BowlerSortOrder.MOST_RECENTLY_USED)

	private val bowlers = bowlerSortOrder.flatMapLatest { sortOrder ->
		bowlersRepository.getBowlersList(kind = BowlerKind.PLAYABLE, sortOrder)
	}

	private val bowlersListUiState: Flow<BowlersListUiState> =
		combine(
			bowlers,
			bowlerToArchive,
		) { bowlersList, bowlerToArchive ->
			BowlersListUiState(
				list = bowlersList,
				bowlerToArchive = bowlerToArchive,
			)
		}

	private val bowlersTopBar: Flow<OverviewTopBarUiState.BowlerTab> = combine(
		bowlers,
		isBowlerSortOrderMenuExpanded,
		bowlerSortOrder,
	) { bowlers, isBowlerSortOrderMenuExpanded, bowlerSortOrder ->
		OverviewTopBarUiState.BowlerTab(
			isSortOrderMenuVisible = bowlers.isNotEmpty(),
			isSortOrderMenuExpanded = isBowlerSortOrderMenuExpanded,
			sortOrder = bowlerSortOrder,
		)
	}

	private val topBarUiState: Flow<OverviewTopBarUiState> = combine(
		selectedTab,
		bowlersTopBar,
		teamsTopBar,
	) { selectedTab, bowlersTopBar, teamsTopBar ->
		when (selectedTab) {
			OverviewTab.BOWLERS -> bowlersTopBar
			OverviewTab.TEAMS -> teamsTopBar
		}
	}

	private val widgets = userDataRepository.userData
		.map { it.isHidingWidgetsInBowlersList }
		.flatMapLatest {
			if (it) {
				flowOf(null)
			} else {
				statisticsWidgetsRepository.getStatisticsWidgets(STATISTICS_WIDGET_CONTEXT)
			}
		}

	private val widgetCharts:
		MutableStateFlow<Map<StatisticsWidgetID, StatisticsWidgetLayoutUiState.ChartContent>> =
		MutableStateFlow(emptyMap())

	private val widgetLayoutUiState = combine(
		widgets,
		widgetCharts,
	) { widgets, widgetCharts ->
		widgets?.let {
			StatisticsWidgetLayoutUiState(
				widgets = it,
				widgetCharts = widgetCharts,
			)
		}
	}

	private val overviewUiState = combine(
		selectedTab,
		bowlersListUiState,
		teamsListUiState,
		widgetLayoutUiState,
		isShowingSwipeHint,
	) { selectedTab, bowlersListState, teamsListState, widgets, isShowingSwipeHint ->
		OverviewUiState(
			isTeamsEnabled = featureFlagsClient.isEnabled(FeatureFlag.TEAMS),
			tab = selectedTab,
			widgets = widgets,
			teamsList = teamsListState,
			bowlersList = bowlersListState,
			isShowingSwipeHint = isShowingSwipeHint,
		)
	}

	val uiState: StateFlow<OverviewScreenUiState> = combine(
		overviewUiState,
		topBarUiState,
		isGameInProgressSnackBarVisible,
	) { overview, topBar, isGameInProgressSnackBarVisible ->
		OverviewScreenUiState.Loaded(
			overview = overview,
			topBar = topBar,
			isGameInProgressSnackBarVisible = isGameInProgressSnackBarVisible,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = OverviewScreenUiState.Loading,
	)

	init {
		// FIXME: Share with BowlerDetailsViewModel, StatisticsWidgetLayoutEditorViewModel?
		viewModelScope.launch {
			widgets.collect { widgets ->
				widgets?.forEach { widget ->
					launch {
						val chart = statisticsWidgetsRepository.getStatisticsWidgetChart(widget)

						widgetCharts.update {
							val widgetChart =
								it[widget.id]?.copy(chart = chart) ?: StatisticsWidgetLayoutUiState.ChartContent(
									chart,
									ChartEntryModelProducer(),
								)

							if (widgetChart.chart.hasModelEntries()) {
								widgetChart.modelProducer.setEntriesSuspending(widgetChart.chart.getModelEntries()).await()
							}

							it + (widget.id to widgetChart)
						}
					}
				}
			}
		}
	}

	fun handleAction(action: OverviewScreenUiAction) {
		when (action) {
			OverviewScreenUiAction.DidAppear -> checkForGameInProgress()
			OverviewScreenUiAction.GameInProgressSnackBarDismissed -> dismissGameInProgressSnackBar()
			OverviewScreenUiAction.ResumeGameInProgressClicked -> resumeGameInProgress()
			is OverviewScreenUiAction.OverviewAction -> handleOverviewAction(action.action)
		}
	}

	private fun handleOverviewAction(action: OverviewUiAction) {
		when (action) {
			OverviewUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			OverviewUiAction.AddTeamClicked -> sendEvent(OverviewScreenEvent.AddTeam)
			OverviewUiAction.EditStatisticsWidgetClicked -> sendEvent(
				OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT),
			)
			OverviewUiAction.QuickPlayClicked -> sendEvent(OverviewScreenEvent.ShowQuickPlay)
			OverviewUiAction.SwipeHintDismissed -> dismissSwipeHint()
			OverviewUiAction.TeamsSortClicked -> isTeamSortOrderMenuExpanded.value = true
			OverviewUiAction.TeamsSortDismissed -> isTeamSortOrderMenuExpanded.value = false
			is OverviewUiAction.TeamsSortOrderClicked -> {
				teamSortOrder.value = action.sortOrder
				isTeamSortOrderMenuExpanded.value = false
			}
			OverviewUiAction.BowlersSortClicked -> isBowlerSortOrderMenuExpanded.value = true
			OverviewUiAction.BowlersSortDismissed -> isBowlerSortOrderMenuExpanded.value = false
			is OverviewUiAction.BowlersSortOrderClicked -> {
				bowlerSortOrder.value = action.sortOrder
				isBowlerSortOrderMenuExpanded.value = false
			}
			is OverviewUiAction.TabClicked -> selectedTab.value = action.tab
			is OverviewUiAction.TeamsListAction -> handleTeamsListAction(action.action)
			is OverviewUiAction.BowlersListAction -> handleBowlersListAction(action.action)
			is OverviewUiAction.StatisticsWidgetLayout -> handleStatisticsWidgetLayoutAction(action.action)
		}
	}

	private fun handleBowlersListAction(action: BowlersListUiAction) {
		when (action) {
			BowlersListUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			is BowlersListUiAction.BowlerClicked -> showBowlerDetails(action.bowler)
			is BowlersListUiAction.BowlerEdited -> sendEvent(
				OverviewScreenEvent.EditBowler(action.bowler.id),
			)
			is BowlersListUiAction.BowlerArchived -> setBowlerArchivePrompt(action.bowler)
			BowlersListUiAction.ConfirmArchiveClicked -> archiveBowler()
			BowlersListUiAction.DismissArchiveClicked -> setBowlerArchivePrompt(null)
		}
	}

	private fun handleTeamsListAction(action: TeamsListUiAction) {
		when (action) {
			TeamsListUiAction.AddTeamClicked -> sendEvent(OverviewScreenEvent.AddTeam)
			is TeamsListUiAction.TeamClicked -> TODO()
			is TeamsListUiAction.TeamEdited -> sendEvent(OverviewScreenEvent.EditTeam(action.team.id))
			is TeamsListUiAction.TeamDeleted -> setTeamDeletePrompt(action.team)
			TeamsListUiAction.ConfirmDeleteClicked -> deleteTeam()
			TeamsListUiAction.DismissDeleteClicked -> setTeamDeletePrompt(null)
		}
	}

	private fun handleStatisticsWidgetLayoutAction(action: StatisticsWidgetLayoutUiAction) {
		when (action) {
			is StatisticsWidgetLayoutUiAction.WidgetClicked ->
				when (widgetCharts.value[action.widget.id]?.chart) {
					is StatisticChartContent.CountableChart,
					is StatisticChartContent.AveragingChart,
					is StatisticChartContent.PercentageChart,
					null,
					-> sendEvent(
						OverviewScreenEvent.ShowWidgetStatistics(
							action.widget.filter(Clock.System.now().toLocalDate()),
						),
					)
					is StatisticChartContent.ChartUnavailable ->
						sendEvent(OverviewScreenEvent.ShowWidgetUnavailableError)
					is StatisticChartContent.DataMissing ->
						sendEvent(OverviewScreenEvent.ShowWidgetNotEnoughDataError)
				}
			is StatisticsWidgetLayoutUiAction.ChangeLayoutClicked -> sendEvent(
				OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT),
			)
		}
	}

	private fun dismissSwipeHint() {
		viewModelScope.launch {
			userDataRepository.didDismissSwipeRowsTip()
		}
	}

	private fun checkForGameInProgress() {
		viewModelScope.launch {
			val isGameInProgress = gamesRepository.isGameInProgress()
			isGameInProgressSnackBarVisible.value = isGameInProgress
		}
	}

	private fun showBowlerDetails(bowler: BowlerListItem) {
		sendEvent(OverviewScreenEvent.ShowBowlerDetails(bowler.id))
		analyticsClient.trackEvent(BowlerViewed(BowlerKind.PLAYABLE))
	}

	private fun setTeamDeletePrompt(team: TeamListItem?) {
		teamToDelete.value = team
	}

	private fun deleteTeam() {
		val teamToDelete = teamToDelete.value ?: return
		viewModelScope.launch {
			teamsRepository.deleteTeam(teamToDelete.id)
			setTeamDeletePrompt(null)
		}
	}

	private fun setBowlerArchivePrompt(bowler: BowlerListItem?) {
		bowlerToArchive.value = bowler
	}

	private fun archiveBowler() {
		val bowlerToArchive = bowlerToArchive.value ?: return
		viewModelScope.launch {
			bowlersRepository.archiveBowler(bowlerToArchive.id)
			setBowlerArchivePrompt(null)
		}
	}

	private fun dismissGameInProgressSnackBar() {
		isGameInProgressSnackBarVisible.value = false
		viewModelScope.launch {
			userDataRepository.dismissLatestGameInEditor()
		}
	}

	private fun resumeGameInProgress() {
		viewModelScope.launch {
			val game = gamesRepository.getGameInProgress() ?: return@launch
			sendEvent(OverviewScreenEvent.ResumeGame(game.seriesIds, game.currentGameId))
		}
	}
}
