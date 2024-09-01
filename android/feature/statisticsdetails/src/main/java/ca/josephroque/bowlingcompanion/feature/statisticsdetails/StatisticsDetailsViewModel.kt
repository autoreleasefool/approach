package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.statistics.StatisticViewed
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
	@ApplicationScope private val scope: CoroutineScope,
) : ApproachViewModel<StatisticsDetailsScreenEvent>(),
	DefaultLifecycleObserver {
	private val sourceType = Route.StatisticsDetails.getSourceType(savedStateHandle)!!
	private val sourceId = Route.StatisticsDetails.getSourceId(savedStateHandle) ?: UUID.randomUUID()
	private val initialFilterSource = when (sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(BowlerID(sourceId))
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(LeagueID(sourceId))
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(SeriesID(sourceId))
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(sourceId)
	}

	private val filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = initialFilterSource))

	private val sourceSummaries = filter.map {
		statisticsRepository.getSourceDetails(it.source)
	}

	private data class StatisticsSettings(
		val isHidingZeroStatistics: Boolean,
		val isHidingStatisticDescriptions: Boolean,
		val isTapToViewChartTipDismissed: Boolean,
	)
	private val statisticsSettings: Flow<StatisticsSettings> =
		userDataRepository.userData.map {
			StatisticsSettings(
				isHidingZeroStatistics = !it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
				isTapToViewChartTipDismissed = it.isStatisticsTapToViewChartTipDismissed,
			)
		}

	private val statisticsList = combine(
		filter,
		statisticsSettings,
	) { filter, _ ->
		statisticsRepository.getStatisticsList(filter)
	}

	private val selectedStatistic: MutableStateFlow<StatisticID?> = MutableStateFlow(
		allStatistics(source = initialFilterSource).firstOrNull()?.id,
	)

	private val statisticsListState: Flow<StatisticsDetailsListUiState> = combine(
		filter,
		sourceSummaries,
		statisticsList,
		selectedStatistic,
		statisticsSettings,
	) { filter, sourceSummaries, statistics, selectedStatistic, settings ->
		StatisticsDetailsListUiState(
			filter = filter,
			filterSources = sourceSummaries,
			statistics = statistics,
			highlightedEntry = selectedStatistic,
			isHidingZeroStatistics = settings.isHidingZeroStatistics,
			isHidingStatisticDescriptions = settings.isHidingStatisticDescriptions,
			isShowingTapToViewChartTip = !settings.isTapToViewChartTipDismissed,
			isChartSupportEnabled = true,
		)
	}

	val uiState: StateFlow<StatisticsDetailsScreenUiState> = statisticsListState.map {
		StatisticsDetailsScreenUiState.Loaded(list = it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			StatisticsDetailsScreenUiAction.OnDismissed -> hasSeenAllStatistics()
			is StatisticsDetailsScreenUiAction.List -> handleListAction(action.action)
			is StatisticsDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleListAction(action: StatisticsDetailsListUiAction) {
		when (action) {
			is StatisticsDetailsListUiAction.StatisticClicked ->
				showStatisticChart(statistic = action.id)
			is StatisticsDetailsListUiAction.HidingZeroStatisticsToggled ->
				toggleHidingZeroStatistics(action.newValue)
			is StatisticsDetailsListUiAction.HidingStatisticDescriptionsToggled ->
				toggleHidingStatisticDescriptions(action.newValue)
			StatisticsDetailsListUiAction.TapToViewChartTipDismissed -> dismissTapToViewChartTip()
		}
	}

	private fun handleTopBarAction(action: StatisticsDetailsTopBarUiAction) {
		when (action) {
			StatisticsDetailsTopBarUiAction.BackClicked -> sendEvent(StatisticsDetailsScreenEvent.Dismissed)
		}
	}

	private fun hasSeenAllStatistics() {
		// TODO: Do we need to use @ApplicationScope here
		scope.launch {
			userDataRepository.setAllStatisticIDsSeen()
		}
	}

	private fun dismissTapToViewChartTip() {
		viewModelScope.launch {
			userDataRepository.didDismissStatisticsTapToViewChartTip()
		}
	}

	private fun showStatisticChart(statistic: StatisticID) {
		sendEvent(
			StatisticsDetailsScreenEvent.ShowStatisticChart(
				filter = filter.value,
				id = statistic,
			),
		)

		scope.launch {
			val userData = userDataRepository.userData.first()
			analyticsClient.trackEvent(
				StatisticViewed(
					statisticName = statistic.name,
					countsH2AsH = !userData.isCountingH2AsHDisabled,
					countsS2AsS = !userData.isCountingSplitWithBonusAsSplitDisabled,
				),
			)
		}
	}

	private fun toggleHidingZeroStatistics(newValue: Boolean) {
		viewModelScope.launch {
			userDataRepository.setIsHidingZeroStatistics(newValue)
		}
	}

	private fun toggleHidingStatisticDescriptions(newValue: Boolean) {
		viewModelScope.launch {
			userDataRepository.setIsHidingStatisticDescriptions(newValue)
		}
	}
}

fun TrackableFilter.Source.sourceType(): StatisticsDetailsSourceType = when (this) {
	is TrackableFilter.Source.Bowler -> StatisticsDetailsSourceType.BOWLER
	is TrackableFilter.Source.League -> StatisticsDetailsSourceType.LEAGUE
	is TrackableFilter.Source.Series -> StatisticsDetailsSourceType.SERIES
	is TrackableFilter.Source.Game -> StatisticsDetailsSourceType.GAME
}
