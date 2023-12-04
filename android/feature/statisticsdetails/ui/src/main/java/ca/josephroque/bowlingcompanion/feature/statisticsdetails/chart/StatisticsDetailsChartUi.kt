package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

data class StatisticsDetailsChartUiState(
	val aggregation: TrackableFilter.AggregationFilter,
	val filterSource: TrackableFilter.Source,
	val isLoadingNextChart: Boolean,
	val isFilterTooNarrow: Boolean,
	val chartContent: ChartContent?,
) {
	data class ChartContent(
		val chart: StatisticChartContent,
		val model: ChartEntryModel,
	)
}

sealed interface StatisticsDetailsChartUiAction {
	data class AggregationChanged(
		val newValue: TrackableFilter.AggregationFilter
	): StatisticsDetailsChartUiAction
}