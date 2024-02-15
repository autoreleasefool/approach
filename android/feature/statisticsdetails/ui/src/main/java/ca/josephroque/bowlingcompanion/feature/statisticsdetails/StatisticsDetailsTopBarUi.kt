package ca.josephroque.bowlingcompanion.feature.statisticsdetails

@Suppress("unused")
data object StatisticsDetailsTopBarUiState

sealed interface StatisticsDetailsTopBarUiAction {
	data object BackClicked : StatisticsDetailsTopBarUiAction
}
