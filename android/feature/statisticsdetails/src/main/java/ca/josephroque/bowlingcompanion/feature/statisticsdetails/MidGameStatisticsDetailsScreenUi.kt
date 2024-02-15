package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState

sealed interface MidGameStatisticsDetailsScreenUiState {
	data object Loading : MidGameStatisticsDetailsScreenUiState

	data class Loaded(
		val list: StatisticsDetailsListUiState,
	) : MidGameStatisticsDetailsScreenUiState
}

sealed interface MidGameStatisticsDetailsScreenUiAction {
	data object OnDismissed : MidGameStatisticsDetailsScreenUiAction
	data class List(val action: StatisticsDetailsListUiAction) : MidGameStatisticsDetailsScreenUiAction
	data class TopBar(
		val action: MidGameStatisticsDetailsTopBarUiAction,
	) : MidGameStatisticsDetailsScreenUiAction
}

sealed interface MidGameStatisticsDetailsScreenEvent {
	data object Dismissed : MidGameStatisticsDetailsScreenEvent
}
