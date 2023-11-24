package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup

data class StatisticsDetailsListUiState(
	val statistics: List<StatisticListEntryGroup> = emptyList(),
	val highlightedEntry: Int? = null,
	val isHidingZeroStatistics: Boolean = true,
)

sealed interface StatisticsDetailsListUiAction {
	data object BackClicked: StatisticsDetailsListUiAction

	data class StatisticClicked(val title: Int): StatisticsDetailsListUiAction
	data class HidingZeroStatisticsToggled(val newValue: Boolean?): StatisticsDetailsListUiAction
}
