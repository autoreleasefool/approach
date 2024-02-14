package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiState

sealed interface StatisticsOverviewScreenUiState {
	data object Loading : StatisticsOverviewScreenUiState

	data class Loaded(
		val statisticsOverview: StatisticsOverviewUiState = StatisticsOverviewUiState,
	) : StatisticsOverviewScreenUiState
}

sealed interface StatisticsOverviewScreenUiAction {
	data class StatisticsOverview(
		val action: StatisticsOverviewUiAction,
	) : StatisticsOverviewScreenUiAction
}

sealed interface StatisticsOverviewScreenEvent {
	data object ShowSourcePicker : StatisticsOverviewScreenEvent
}
