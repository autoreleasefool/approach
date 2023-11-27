package ca.josephroque.bowlingcompanion.feature.statisticsdetails

sealed interface StatisticsDetailsTopBarUiAction {
	data object BackClicked: StatisticsDetailsTopBarUiAction
}