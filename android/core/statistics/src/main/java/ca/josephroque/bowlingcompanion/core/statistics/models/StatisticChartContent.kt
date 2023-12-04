package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

sealed interface StatisticChartContent {
	data class CountableChart(val data: CountableChartData): StatisticChartContent
	data class AveragingChart(val data: AveragingChartData): StatisticChartContent
	data class PercentageChart(val data: PercentageChartData): StatisticChartContent

	data class DataMissing(val id: StatisticID): StatisticChartContent
	data class ChartUnavailable(val id: StatisticID): StatisticChartContent
}