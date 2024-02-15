package ca.josephroque.bowlingcompanion.feature.statisticsdetails

@Suppress("unused")
data object MidGameStatisticsDetailsTopBarUiState

sealed interface MidGameStatisticsDetailsTopBarUiAction {
	data object BackClicked : MidGameStatisticsDetailsTopBarUiAction
}
