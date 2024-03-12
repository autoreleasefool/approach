package ca.josephroque.bowlingcompanion.feature.statisticsdetails

@Suppress("unused")
data object StatisticsDetailsChartTopBarUiState

sealed interface StatisticsDetailsChartTopBarUiAction {
	data object BackClicked : StatisticsDetailsChartTopBarUiAction
}
