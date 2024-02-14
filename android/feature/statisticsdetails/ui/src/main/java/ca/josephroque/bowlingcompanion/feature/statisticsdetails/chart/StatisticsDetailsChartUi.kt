package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

data class StatisticsDetailsChartUiState(
	val filter: TrackableFilter,
	val isLoadingNextChart: Boolean,
	val isFilterTooNarrow: Boolean,
	val supportsAggregation: Boolean,
	val chartContent: ChartContent?,
) {
	data class ChartContent(
		val chart: StatisticChartContent,
		val modelProducer: ChartEntryModelProducer,
	)
}

sealed interface StatisticsDetailsChartUiAction {
	data class AggregationChanged(val newValue: Boolean) : StatisticsDetailsChartUiAction
}
