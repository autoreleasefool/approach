package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiState
import java.util.UUID

sealed interface StatisticsOverviewScreenUiState {
	data object Loading: StatisticsOverviewScreenUiState

	data class Loaded(
		val statisticsOverview: StatisticsOverviewUiState = StatisticsOverviewUiState,
	): StatisticsOverviewScreenUiState
}

sealed interface StatisticsOverviewScreenUiAction {
	data class StatisticsOverview(val action: StatisticsOverviewUiAction): StatisticsOverviewScreenUiAction
}

sealed interface StatisticsOverviewScreenEvent {
	data object ShowSourcePicker: StatisticsOverviewScreenEvent
}