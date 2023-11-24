package ca.josephroque.bowlingcompanion.feature.statisticsdetails

sealed interface StatisticsDetailsScreenUiState {
	data object Loading: StatisticsDetailsScreenUiState

	data class Loaded(
		val list: StatisticsDetailsListUiState,
	): StatisticsDetailsScreenUiState
}

sealed interface StatisticsDetailsScreenUiAction {
	data class StatisticsDetailsListAction(val action: StatisticsDetailsListUiAction): StatisticsDetailsScreenUiAction
}

sealed interface StatisticsDetailsScreenEvent {
	data object Dismissed: StatisticsDetailsScreenEvent
}