package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState

sealed interface StatisticsDetailsScreenUiState {
	data object Loading : StatisticsDetailsScreenUiState

	data class Loaded(
		val list: StatisticsDetailsListUiState,
	) : StatisticsDetailsScreenUiState
}

sealed interface StatisticsDetailsScreenUiAction {
	data object OnDismissed : StatisticsDetailsScreenUiAction
	data class List(val action: StatisticsDetailsListUiAction) : StatisticsDetailsScreenUiAction
	data class TopBar(val action: StatisticsDetailsTopBarUiAction) : StatisticsDetailsScreenUiAction
}

sealed interface StatisticsDetailsScreenEvent {
	data object Dismissed : StatisticsDetailsScreenEvent
	data class ShowStatisticChart(
		val filter: TrackableFilter,
		val id: StatisticID,
	) : StatisticsDetailsScreenEvent
}
