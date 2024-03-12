package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

data class StatisticsDetailsChartUiState(
	val filter: TrackableFilter,
	val filterSources: TrackableFilter.SourceSummaries? = null,
	val isFilterTooNarrow: Boolean,
	val supportsAggregation: Boolean,
	val chartContent: ChartContent?,
	val isShowingTitle: Boolean = false,
) {
	data class ChartContent(
		val statisticId: StatisticID,
		val chart: StatisticChartContent,
		val modelProducer: ChartEntryModelProducer,
	)
}

sealed interface StatisticsDetailsChartUiAction {
	data class AggregationChanged(val newValue: Boolean) : StatisticsDetailsChartUiAction
}
