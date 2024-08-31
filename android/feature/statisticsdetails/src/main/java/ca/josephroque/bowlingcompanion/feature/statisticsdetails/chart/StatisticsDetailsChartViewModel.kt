package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.hasModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.StatisticsDetailsChartTopBarUiAction
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsDetailsChartViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
) : ApproachViewModel<StatisticsDetailsChartScreenEvent>() {
	private val sourceType = Route.StatisticsDetailsChart.getSourceType(savedStateHandle)!!
	private val sourceId = Route.StatisticsDetailsChart.getSourceId(savedStateHandle)!!
	private val statisticId = Route.StatisticsDetailsChart.getStatisticId(savedStateHandle)!!
	private val initialFilterSource = when (sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(BowlerID(sourceId))
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(sourceId)
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(sourceId)
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(sourceId)
	}
	private val filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = initialFilterSource))

	private val chartEntryModelProducer = ChartEntryModelProducer()

	private val chartContent: Flow<StatisticsDetailsChartUiState.ChartContent?> = filter
		.map { filter ->
			val chart = statisticsRepository.getStatisticsChart(
				statistic = statisticInstanceFromID(statisticId)!!,
				filter = filter,
			)

			StatisticsDetailsChartUiState.ChartContent(
				statisticId = statisticId,
				chart = chart,
				modelProducer = chartEntryModelProducer,
			)
		}

	private val statisticsChartState: Flow<StatisticsDetailsChartUiState> = combine(
		filter,
		chartContent,
	) { filter, chartContent ->
		StatisticsDetailsChartUiState(
			filter = filter,
			isFilterTooNarrow = false,
			chartContent = chartContent,
			supportsAggregation = statisticInstanceFromID(statisticId)!!.supportsAggregation,
		)
	}

	val uiState: StateFlow<StatisticsDetailsChartScreenUiState> = statisticsChartState
		.map { StatisticsDetailsChartScreenUiState.Loaded(it) }
		.stateIn(
			viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = StatisticsDetailsChartScreenUiState.Loading,
		)

	init {
		viewModelScope.launch {
			chartContent.collect {
				if (it != null && it.chart.hasModelEntries()) {
					chartEntryModelProducer.setEntriesSuspending(it.chart.getModelEntries()).await()
				}
			}
		}
	}

	fun handleAction(action: StatisticsDetailsChartScreenUiAction) {
		when (action) {
			is StatisticsDetailsChartScreenUiAction.Chart -> handleChartAction(action.action)
			is StatisticsDetailsChartScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleChartAction(action: StatisticsDetailsChartUiAction) {
		when (action) {
			is StatisticsDetailsChartUiAction.AggregationChanged ->
				toggleAggregation(action.newValue)
		}
	}

	private fun handleTopBarAction(action: StatisticsDetailsChartTopBarUiAction) {
		when (action) {
			StatisticsDetailsChartTopBarUiAction.BackClicked ->
				sendEvent(StatisticsDetailsChartScreenEvent.Dismissed)
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
