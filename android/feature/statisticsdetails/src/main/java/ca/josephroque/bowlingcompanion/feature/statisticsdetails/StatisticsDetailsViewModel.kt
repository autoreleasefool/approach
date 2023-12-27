package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModel
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.SOURCE_ID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.SOURCE_TYPE
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
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
): ApproachViewModel<StatisticsDetailsScreenEvent>() {
	private val _sourceType = savedStateHandle.get<SourceType>(SOURCE_TYPE) ?: SourceType.BOWLER
	private val _sourceId = savedStateHandle.get<String>(SOURCE_ID)
		?.let { UUID.fromString(it) } ?: UUID.randomUUID()
	private val _initialFilterSource = when (_sourceType) {
		SourceType.BOWLER -> TrackableFilter.Source.Bowler(_sourceId)
		SourceType.LEAGUE -> TrackableFilter.Source.League(_sourceId)
		SourceType.SERIES -> TrackableFilter.Source.Series(_sourceId)
		SourceType.GAME -> TrackableFilter.Source.Game(_sourceId)
	}

	private val _chartEntryModelProducer = ChartEntryModelProducer()

	private val _filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = _initialFilterSource))

	private val _statisticsList = _filter.map { statisticsRepository.getStatisticsList(it) }

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
		userDataRepository.userData,
	) { statistics, selectedStatistic, userData ->
		StatisticsDetailsListUiState(
			statistics = statistics,
			highlightedEntry = selectedStatistic,
			isHidingZeroStatistics = !userData.isShowingZeroStatistics,
		)
	}

	val uiState: StateFlow<StatisticsDetailsScreenUiState> = combine(
		_statisticsListState,
		_statisticsChartState,
	) { statisticsList, statisticsChart ->
		StatisticsDetailsScreenUiState.Loaded(
			list = statisticsList,
			chart = statisticsChart,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			is StatisticsDetailsScreenUiAction.ListAction -> handleListAction(action.action)
			is StatisticsDetailsScreenUiAction.TopBarAction -> handleTopBarAction(action.action)
			is StatisticsDetailsScreenUiAction.ChartAction -> handleChartAction(action.action)
		}
	}

	private fun handleListAction(action: StatisticsDetailsListUiAction) {
		when (action) {
			is StatisticsDetailsListUiAction.StatisticClicked ->
				showStatisticChart(statistic = action.id)
			is StatisticsDetailsListUiAction.HidingZeroStatisticsToggled ->
				toggleHidingZeroStatistics(action.newValue)
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

	private fun showStatisticChart(statistic: StatisticID) {
		_selectedStatistic.value = statistic
	}

	private fun toggleHidingZeroStatistics(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isShowingZeroStatistics
			userDataRepository.setIsHidingZeroStatistics(newValue ?: !currentValue)
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

enum class SourceType {
	BOWLER,
	LEAGUE,
	SERIES,
	GAME,
}

fun TrackableFilter.Source.sourceType(): SourceType = when (this) {
	is TrackableFilter.Source.Bowler -> SourceType.BOWLER
	is TrackableFilter.Source.League -> SourceType.LEAGUE
	is TrackableFilter.Source.Series -> SourceType.SERIES
	is TrackableFilter.Source.Game -> SourceType.GAME
}