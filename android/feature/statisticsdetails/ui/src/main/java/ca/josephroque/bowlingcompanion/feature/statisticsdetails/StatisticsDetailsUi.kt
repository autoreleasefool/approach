package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState

data class StatisticsDetailsUiState(
	val list: StatisticsDetailsListUiState,
	val chart: StatisticsDetailsChartUiState,
)

sealed interface StatisticsDetailsUiAction {
	data class StatisticsDetailsList(val action: StatisticsDetailsListUiAction): StatisticsDetailsUiAction
	data class StatisticsDetailsChart(val action: StatisticsDetailsChartUiAction): StatisticsDetailsUiAction
}