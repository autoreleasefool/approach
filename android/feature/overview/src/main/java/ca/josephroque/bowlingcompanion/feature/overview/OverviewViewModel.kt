package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.bowler.BowlerViewed
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState
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
import javax.inject.Inject

private const val STATISTICS_WIDGET_CONTEXT = "overview"

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	statisticsWidgetsRepository: StatisticsWidgetsRepository,
	userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<OverviewScreenEvent>() {
	private val _bowlerToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)
	private val _quickPlayEnabled = MutableStateFlow(false)

	private val _bowlersListState: Flow<BowlersListUiState> =
		combine(
			bowlersRepository.getBowlersList(),
			_bowlerToArchive,
		) { bowlersList, bowlerToArchive ->
			BowlersListUiState(
				list = bowlersList,
				bowlerToArchive = bowlerToArchive,
			)
		}

	private val _widgets = userDataRepository.userData
		.map { it.isHidingWidgetsInBowlersList }
		.flatMapLatest {
			if (it) flowOf(null)
			else statisticsWidgetsRepository.getStatisticsWidgets(STATISTICS_WIDGET_CONTEXT)
		}

	val uiState: StateFlow<OverviewScreenUiState> = combine(
		_bowlersListState,
		_widgets,
		_quickPlayEnabled,
	) { bowlersList, widgets, quickPlayEnabled ->
		OverviewScreenUiState.Loaded(
			overview = OverviewUiState(
				bowlersList = bowlersList,
				widgets = widgets?.let { StatisticsWidgetLayoutUiState(widgets = it) },
				isQuickPlayEnabled = quickPlayEnabled,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = OverviewScreenUiState.Loading
	)

	fun handleAction(action: OverviewScreenUiAction) {
		when (action) {
			OverviewScreenUiAction.DidAppear -> loadQuickPlay()
			is OverviewScreenUiAction.OverviewAction -> handleOverviewAction(action.action)
		}
	}

	private fun handleOverviewAction(action: OverviewUiAction) {
		when (action) {
			OverviewUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			OverviewUiAction.EditStatisticsWidgetClicked -> sendEvent(OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT))
			OverviewUiAction.QuickPlayClicked -> sendEvent(OverviewScreenEvent.ShowQuickPlay)
			is OverviewUiAction.BowlersListAction -> handleBowlersListAction(action.action)
			is OverviewUiAction.StatisticsWidgetLayout -> handleStatisticsWidgetLayoutAction(action.action)
		}
	}

	private fun handleBowlersListAction(action: BowlersListUiAction) {
		when (action) {
			is BowlersListUiAction.BowlerClicked -> showBowlerDetails(action.bowler)
			is BowlersListUiAction.AddBowlerClicked -> sendEvent(OverviewScreenEvent.AddBowler)
			is BowlersListUiAction.BowlerEdited -> sendEvent(OverviewScreenEvent.EditBowler(action.bowler.id))
			is BowlersListUiAction.BowlerArchived -> setBowlerArchivePrompt(action.bowler)
			is BowlersListUiAction.ConfirmArchiveClicked -> archiveBowler()
			is BowlersListUiAction.DismissArchiveClicked -> setBowlerArchivePrompt(null)
		}
	}

	private fun handleStatisticsWidgetLayoutAction(action: StatisticsWidgetLayoutUiAction) {
		when (action) {
			is StatisticsWidgetLayoutUiAction.WidgetClicked -> sendEvent(OverviewScreenEvent.ShowStatistics(action.widget.id))
			is StatisticsWidgetLayoutUiAction.ChangeLayoutClicked -> sendEvent(OverviewScreenEvent.EditStatisticsWidget(STATISTICS_WIDGET_CONTEXT))
		}
	}

	private fun loadQuickPlay() {
		viewModelScope.launch {
			bowlersRepository.getDefaultQuickPlay()?.let {
				_quickPlayEnabled.value = true
			}
		}
	}

	private fun showBowlerDetails(bowler: BowlerListItem) {
		sendEvent(OverviewScreenEvent.ShowBowlerDetails(bowler.id))
		analyticsClient.trackEvent(BowlerViewed(BowlerKind.PLAYABLE))
	}

	private fun setBowlerArchivePrompt(bowler: BowlerListItem?) {
		_bowlerToArchive.value = bowler
	}

	private fun archiveBowler() {
		val bowlerToArchive = _bowlerToArchive.value ?: return
		viewModelScope.launch {
			bowlersRepository.archiveBowler(bowlerToArchive.id)
			setBowlerArchivePrompt(null)
		}
	}
}