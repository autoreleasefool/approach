package ca.josephroque.bowlingcompanion.feature.statisticsdetails.list

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntryGroup

data class StatisticsDetailsListUiState(
	val filter: TrackableFilter,
	val filterSources: TrackableFilter.SourceSummaries? = null,
	val statistics: List<StatisticListEntryGroup> = emptyList(),
	val highlightedEntry: StatisticID? = null,
	val isHidingZeroStatistics: Boolean = true,
	val isHidingStatisticDescriptions: Boolean = false,
)

sealed interface StatisticsDetailsListUiAction {
	data class StatisticClicked(val id: StatisticID) : StatisticsDetailsListUiAction
	data class HidingZeroStatisticsToggled(val newValue: Boolean) : StatisticsDetailsListUiAction
	data class HidingStatisticDescriptionsToggled(
		val newValue: Boolean,
	) : StatisticsDetailsListUiAction
	data class HeaderHeightMeasured(val height: Float) : StatisticsDetailsListUiAction
}
