package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState

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