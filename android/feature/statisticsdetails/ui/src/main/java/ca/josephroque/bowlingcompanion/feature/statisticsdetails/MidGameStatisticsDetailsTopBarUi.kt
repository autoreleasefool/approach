package ca.josephroque.bowlingcompanion.feature.statisticsdetails

data object MidGameStatisticsDetailsTopBarUiState

sealed interface MidGameStatisticsDetailsTopBarUiAction {
	data object BackClicked : MidGameStatisticsDetailsTopBarUiAction
}
