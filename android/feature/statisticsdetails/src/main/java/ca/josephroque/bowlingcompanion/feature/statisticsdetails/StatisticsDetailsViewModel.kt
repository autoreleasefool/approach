package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.statistics.StatisticViewed
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.skydoves.flexible.core.FlexibleSheetValue
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<StatisticsDetailsScreenEvent>() {
	private val _sourceType = Route.StatisticsDetails.getSourceType(savedStateHandle)!!
	private val _sourceId = Route.StatisticsDetails.getSourceId(savedStateHandle) ?: UUID.randomUUID()
	private val _initialFilterSource = when (_sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(_sourceId)
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(_sourceId)
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(_sourceId)
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(_sourceId)
	}

	private val _chartEntryModelProducer = ChartEntryModelProducer()

	private val _filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = _initialFilterSource))

	private data class StatisticsSettings(
		val isHidingZeroStatistics: Boolean,
		val isHidingStatisticDescriptions: Boolean,
	)
	private val _statisticsSettings: Flow<StatisticsSettings> =
		userDataRepository.userData.map {
			StatisticsSettings(
				isHidingZeroStatistics = !it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
			)
		}

	private val _statisticsList = combine(
		_filter,
		_statisticsSettings,
	) { filter, _ ->
		statisticsRepository.getStatisticsList(filter)
	}

	private val _selectedStatistic: MutableStateFlow<StatisticID?> = MutableStateFlow(
		allStatistics(source = _initialFilterSource).firstOrNull()?.id
	)

	private val _chartContent: Flow<StatisticsDetailsChartUiState.ChartContent?> = combine(
		_filter,
		_selectedStatistic,
	) { filter, selectedStatistic ->
		if (selectedStatistic == null) return@combine null
		val chart = statisticsRepository.getStatisticsChart(
			statistic = statisticInstanceFromID(selectedStatistic),
			filter = filter,
		)

		StatisticsDetailsChartUiState.ChartContent(
			chart = chart,
			modelProducer = _chartEntryModelProducer,
		)
	}

	private val _nextSheetSize = MutableStateFlow(FlexibleSheetValue.SlightlyExpanded)

	private val _statisticsChartState: Flow<StatisticsDetailsChartUiState> = combine(
		_filter,
		_selectedStatistic,
		_chartContent
	) { filter, selectedStatistic, chartContent ->
		if (chartContent == null || selectedStatistic == null)
			StatisticsDetailsChartUiState(
				aggregation = filter.aggregation,
				filterSource = filter.source,
				isLoadingNextChart = true,
				isFilterTooNarrow = false,
				chartContent = null,
				supportsAggregation = false,
			)
		else
			StatisticsDetailsChartUiState(
				aggregation = filter.aggregation,
				filterSource = filter.source,
				isLoadingNextChart = false,
				isFilterTooNarrow = false,
				chartContent = chartContent,
				supportsAggregation = statisticInstanceFromID(selectedStatistic).supportsAggregation,
			)
	}

	private val _statisticsListState: Flow<StatisticsDetailsListUiState> = combine(
		_statisticsList,
		_selectedStatistic,
		_statisticsSettings,
	) { statistics, selectedStatistic, settings ->
		StatisticsDetailsListUiState(
			statistics = statistics,
			highlightedEntry = selectedStatistic,
			isHidingZeroStatistics = settings.isHidingZeroStatistics,
			isHidingStatisticDescriptions = settings.isHidingStatisticDescriptions,
		)
	}

	val uiState: StateFlow<StatisticsDetailsScreenUiState> = combine(
		_statisticsListState,
		_statisticsChartState,
		_nextSheetSize,
	) { statisticsList, statisticsChart, nextSheetSize ->
		StatisticsDetailsScreenUiState.Loaded(
			details = StatisticsDetailsUiState(
				list = statisticsList,
				chart = statisticsChart,
				nextSheetSize = nextSheetSize
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			_chartContent.collect {
				if (it != null) {
					_chartEntryModelProducer.setEntries(it.chart.getModelEntries())
				}
			}
		}
	}

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			is StatisticsDetailsScreenUiAction.Details -> handleDetailsAction(action.action)
			is StatisticsDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleDetailsAction(action: StatisticsDetailsUiAction) {
		when (action) {
			is StatisticsDetailsUiAction.StatisticsDetailsList -> handleListAction(action.action)
			is StatisticsDetailsUiAction.StatisticsDetailsChart -> handleChartAction(action.action)
			is StatisticsDetailsUiAction.NextSheetSize -> _nextSheetSize.value = action.size
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

	suspend fun hasSeenAllStatistics() {
		userDataRepository.setAllStatisticIDsSeen()
	}

	private fun showStatisticChart(statistic: StatisticID) {
		_nextSheetSize.value = FlexibleSheetValue.SlightlyExpanded
		_selectedStatistic.value = statistic

		viewModelScope.launch {
			val userData = userDataRepository.userData.first()
			analyticsClient.trackEvent(StatisticViewed(
				statisticName = statistic.name,
				countsH2AsH = !userData.isCountingH2AsHDisabled,
				countsS2AsS = !userData.isCountingSplitWithBonusAsSplitDisabled,
			))
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
		_filter.update {
			it.copy(aggregation = when (newValue) {
				true -> TrackableFilter.AggregationFilter.ACCUMULATE
				false -> TrackableFilter.AggregationFilter.PERIODIC
			})
		}
	}
}

fun TrackableFilter.Source.sourceType(): StatisticsDetailsSourceType = when (this) {
	is TrackableFilter.Source.Bowler -> StatisticsDetailsSourceType.BOWLER
	is TrackableFilter.Source.League -> StatisticsDetailsSourceType.LEAGUE
	is TrackableFilter.Source.Series -> StatisticsDetailsSourceType.SERIES
	is TrackableFilter.Source.Game -> StatisticsDetailsSourceType.GAME
}