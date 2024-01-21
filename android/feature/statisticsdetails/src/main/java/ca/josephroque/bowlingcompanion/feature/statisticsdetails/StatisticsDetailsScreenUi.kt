package ca.josephroque.bowlingcompanion.feature.statisticsdetails

sealed interface StatisticsDetailsScreenUiState {
	data object Loading: StatisticsDetailsScreenUiState

	data class Loaded(
		val details: StatisticsDetailsUiState,
	): StatisticsDetailsScreenUiState
}

sealed interface StatisticsDetailsScreenUiAction {
	data class Details(val action: StatisticsDetailsUiAction): StatisticsDetailsScreenUiAction
	data class TopBar(val action: StatisticsDetailsTopBarUiAction): StatisticsDetailsScreenUiAction
}

sealed interface StatisticsDetailsScreenEvent {
	data object Dismissed: StatisticsDetailsScreenEvent
}