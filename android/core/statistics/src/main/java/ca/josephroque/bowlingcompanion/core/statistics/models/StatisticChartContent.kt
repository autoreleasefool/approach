package ca.josephroque.bowlingcompanion.core.statistics.models

sealed interface StatisticChartContent {
	data class CountableChart(val chart: CountableChartData): StatisticChartContent

	data class DataMissing(val title: Int): StatisticChartContent
	data class ChartUnavailable(val title: Int): StatisticChartContent
}