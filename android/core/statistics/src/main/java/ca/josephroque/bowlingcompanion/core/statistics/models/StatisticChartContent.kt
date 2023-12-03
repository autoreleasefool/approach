package ca.josephroque.bowlingcompanion.core.statistics.models

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID

sealed interface StatisticChartContent {


	data class CountableChart(val chart: CountableChartData): StatisticChartContent

	data class DataMissing(val id: StatisticID): StatisticChartContent
	data class ChartUnavailable(val id: StatisticID): StatisticChartContent
}