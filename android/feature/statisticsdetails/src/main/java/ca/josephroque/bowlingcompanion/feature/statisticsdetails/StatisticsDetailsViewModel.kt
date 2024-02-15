package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.statistics.StatisticViewed
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
	@ApplicationScope private val scope: CoroutineScope,
) : ApproachViewModel<StatisticsDetailsScreenEvent>(), DefaultLifecycleObserver {
	private val sourceType = Route.StatisticsDetails.getSourceType(savedStateHandle)!!
	private val sourceId = Route.StatisticsDetails.getSourceId(savedStateHandle) ?: UUID.randomUUID()
	private val initialFilterSource = when (sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(sourceId)
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(sourceId)
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(sourceId)
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(sourceId)
	}

	private val chartEntryModelProducer = ChartEntryModelProducer()

	private val filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = initialFilterSource))

	private val sourceSummaries = filter.map {
		statisticsRepository.getSourceDetails(it.source)
	}

	private val headerPeekHeight = MutableStateFlow(0f)

	@OptIn(ExperimentalMaterial3Api::class)
	private val bottomSheetValue = MutableStateFlow(SheetValue.PartiallyExpanded)

	private data class StatisticsSettings(
		val isHidingZeroStatistics: Boolean,
		val isHidingStatisticDescriptions: Boolean,
	)
	private val statisticsSettings: Flow<StatisticsSettings> =
		userDataRepository.userData.map {
			StatisticsSettings(
				isHidingZeroStatistics = !it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
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

	private val chartContent: Flow<StatisticsDetailsChartUiState.ChartContent?> = combine(
		filter,
		selectedStatistic,
	) { filter, selectedStatistic ->
		if (selectedStatistic == null) return@combine null
		val chart = statisticsRepository.getStatisticsChart(
			statistic = statisticInstanceFromID(selectedStatistic),
			filter = filter,
		)

		StatisticsDetailsChartUiState.ChartContent(
			chart = chart,
			modelProducer = chartEntryModelProducer,
		)
	}

	private val statisticsChartState: Flow<StatisticsDetailsChartUiState> = combine(
		filter,
		selectedStatistic,
		chartContent,
	) { filter, selectedStatistic, chartContent ->
		if (chartContent == null || selectedStatistic == null) {
			StatisticsDetailsChartUiState(
				filter = filter,
				isLoadingNextChart = true,
				isFilterTooNarrow = false,
				chartContent = null,
				supportsAggregation = false,
			)
		} else {
			StatisticsDetailsChartUiState(
				filter = filter,
				isLoadingNextChart = false,
				isFilterTooNarrow = false,
				chartContent = chartContent,
				supportsAggregation = statisticInstanceFromID(selectedStatistic).supportsAggregation,
			)
		}
	}

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
		)
	}

	@OptIn(ExperimentalMaterial3Api::class)
	val uiState: StateFlow<StatisticsDetailsScreenUiState> = combine(
		statisticsListState,
		statisticsChartState,
		headerPeekHeight,
		bottomSheetValue,
	) { statisticsList, statisticsChart, headerPeekHeight, bottomSheetValue ->
		StatisticsDetailsScreenUiState.Loaded(
			list = statisticsList,
			chart = statisticsChart,
			headerPeekHeight = headerPeekHeight,
			bottomSheetValue = bottomSheetValue,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			chartContent.collect {
				if (it != null) {
					chartEntryModelProducer.setEntries(it.chart.getModelEntries())
				}
			}
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	override fun onResume(owner: LifecycleOwner) {
		bottomSheetValue.value = SheetValue.PartiallyExpanded
	}

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			StatisticsDetailsScreenUiAction.OnDismissed -> hasSeenAllStatistics()
			is StatisticsDetailsScreenUiAction.Chart -> handleChartAction(action.action)
			is StatisticsDetailsScreenUiAction.List -> handleListAction(action.action)
			is StatisticsDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is StatisticsDetailsScreenUiAction.BottomSheet -> handleBottomSheetAction(action.action)
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
			is StatisticsDetailsListUiAction.HeaderHeightMeasured -> setHeaderPeekHeight(action.height)
		}
	}

	private fun handleTopBarAction(action: StatisticsDetailsTopBarUiAction) {
		when (action) {
			StatisticsDetailsTopBarUiAction.BackClicked -> sendEvent(StatisticsDetailsScreenEvent.Dismissed)
		}
	}

	private fun handleChartAction(action: StatisticsDetailsChartUiAction) {
		when (action) {
			is StatisticsDetailsChartUiAction.AggregationChanged ->
				toggleAggregation(action.newValue)
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	private fun handleBottomSheetAction(action: StatisticsDetailsBottomSheetUiAction) {
		when (action) {
			is StatisticsDetailsBottomSheetUiAction.SheetValueChanged ->
				bottomSheetValue.value = action.value
		}
	}

	private fun setHeaderPeekHeight(height: Float) {
		headerPeekHeight.value = height
	}

	private fun hasSeenAllStatistics() {
		scope.launch {
			userDataRepository.setAllStatisticIDsSeen()
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	private fun showStatisticChart(statistic: StatisticID) {
		bottomSheetValue.value = SheetValue.PartiallyExpanded
		selectedStatistic.value = statistic

		viewModelScope.launch {
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

	private fun toggleAggregation(newValue: Boolean) {
		filter.update {
			it.copy(
				aggregation = when (newValue) {
					true -> TrackableFilter.AggregationFilter.ACCUMULATE
					false -> TrackableFilter.AggregationFilter.PERIODIC
				},
			)
		}
	}
}

fun TrackableFilter.Source.sourceType(): StatisticsDetailsSourceType = when (this) {
	is TrackableFilter.Source.Bowler -> StatisticsDetailsSourceType.BOWLER
	is TrackableFilter.Source.League -> StatisticsDetailsSourceType.LEAGUE
	is TrackableFilter.Source.Series -> StatisticsDetailsSourceType.SERIES
	is TrackableFilter.Source.Game -> StatisticsDetailsSourceType.GAME
}
