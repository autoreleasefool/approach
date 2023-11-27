package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter

data class StatisticsDetailsChartUiState(
	val aggregation: TrackableFilter.AggregationFilter,
)

sealed interface StatisticsDetailsChartUiAction {
	data class AggregationChanged(
		val newValue: TrackableFilter.AggregationFilter
	): StatisticsDetailsChartUiAction
}