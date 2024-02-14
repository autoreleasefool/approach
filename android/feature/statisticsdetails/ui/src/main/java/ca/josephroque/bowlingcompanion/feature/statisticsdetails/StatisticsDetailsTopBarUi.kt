package ca.josephroque.bowlingcompanion.feature.statisticsdetails

data object StatisticsDetailsTopBarUiState

sealed interface StatisticsDetailsTopBarUiAction {
	data object BackClicked : StatisticsDetailsTopBarUiAction
}
