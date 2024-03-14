package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.bowler.BowlerViewed
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
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

private const val STATISTICS_WIDGET_CONTEXT = "overview"

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val gamesRepository: GamesRepository,
	statisticsWidgetsRepository: StatisticsWidgetsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<OverviewScreenEvent>() {
	private val bowlerToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)
	private val isGameInProgressSnackBarVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)

	private val bowlersListState: Flow<BowlersListUiState> =
		combine(
			bowlersRepository.getBowlersList(),
			bowlerToArchive,
		) { bowlersList, bowlerToArchive ->
			BowlersListUiState(
				list = bowlersList,
				bowlerToArchive = bowlerToArchive,
			)
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

	private val widgetCharts: MutableStateFlow<Map<UUID, StatisticChartContent>> = MutableStateFlow(
		emptyMap(),
	)

	val uiState: StateFlow<OverviewScreenUiState> = combine(
		bowlersListState,
		widgets,
		widgetCharts,
		isGameInProgressSnackBarVisible,
	) { bowlersList, widgets, widgetCharts, isGameInProgressSnackBarVisible ->
		OverviewScreenUiState.Loaded(
			overview = OverviewUiState(
				bowlersList = bowlersList,
				widgets = widgets?.let {
					StatisticsWidgetLayoutUiState(
						widgets = it,
						widgetCharts = widgetCharts,
					)
				},
			),
			isGameInProgressSnackBarVisible = isGameInProgressSnackBarVisible,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = OverviewScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			widgets.collect { widgets ->
				widgets?.forEach { widget ->
					launch {
						val chart = statisticsWidgetsRepository.getStatisticsWidgetChart(widget)
						widgetCharts.update {
							it + (widget.id to chart)
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
			OverviewUiAction.EditStatisticsWidgetClicked -> sendEvent(
				OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT),
			)
			OverviewUiAction.QuickPlayClicked -> sendEvent(OverviewScreenEvent.ShowQuickPlay)
			is OverviewUiAction.BowlersListAction -> handleBowlersListAction(action.action)
			is OverviewUiAction.StatisticsWidgetLayout -> handleStatisticsWidgetLayoutAction(action.action)
		}
	}

	private fun handleBowlersListAction(action: BowlersListUiAction) {
		when (action) {
			is BowlersListUiAction.BowlerClicked -> showBowlerDetails(action.bowler)
			is BowlersListUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			is BowlersListUiAction.BowlerEdited -> sendEvent(
				OverviewScreenEvent.EditBowler(action.bowler.id),
			)
			is BowlersListUiAction.BowlerArchived -> setBowlerArchivePrompt(action.bowler)
			is BowlersListUiAction.ConfirmArchiveClicked -> archiveBowler()
			is BowlersListUiAction.DismissArchiveClicked -> setBowlerArchivePrompt(null)
		}
	}

	private fun handleStatisticsWidgetLayoutAction(action: StatisticsWidgetLayoutUiAction) {
		when (action) {
			is StatisticsWidgetLayoutUiAction.WidgetClicked -> sendEvent(
				OverviewScreenEvent.ShowWidgetStatistics(action.widget.filter),
			)
			is StatisticsWidgetLayoutUiAction.ChangeLayoutClicked -> sendEvent(
				OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT),
			)
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
