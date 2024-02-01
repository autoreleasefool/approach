package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui

data object StatisticsOverviewUiState

sealed interface StatisticsOverviewUiAction {
	data object ViewMoreClicked: StatisticsOverviewUiAction
}