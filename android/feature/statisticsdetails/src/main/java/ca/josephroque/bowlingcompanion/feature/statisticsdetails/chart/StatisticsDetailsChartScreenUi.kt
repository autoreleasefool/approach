package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import ca.josephroque.bowlingcompanion.feature.statisticsdetails.StatisticsDetailsChartTopBarUiAction

sealed interface StatisticsDetailsChartScreenUiState {
	data object Loading : StatisticsDetailsChartScreenUiState

	data class Loaded(
		val chart: StatisticsDetailsChartUiState,
	) : StatisticsDetailsChartScreenUiState
}

sealed interface StatisticsDetailsChartScreenUiAction {
	data class Chart(val action: StatisticsDetailsChartUiAction) : StatisticsDetailsChartScreenUiAction
	data class TopBar(
		val action: StatisticsDetailsChartTopBarUiAction,
	) : StatisticsDetailsChartScreenUiAction
}

sealed interface StatisticsDetailsChartScreenEvent {
	data object Dismissed : StatisticsDetailsChartScreenEvent
}
