package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter

data class StatisticsOverviewUiState(
	val sourcePicker: SourcePickerUiState = SourcePickerUiState(),
)

sealed interface StatisticsOverviewUiAction {
	data object SourcePickerDismissed: StatisticsOverviewUiAction
	data object SourcePickerBowlerClicked: StatisticsOverviewUiAction
	data object SourcePickerLeagueClicked: StatisticsOverviewUiAction
	data object SourcePickerSeriesClicked: StatisticsOverviewUiAction
	data object SourcePickerGameClicked: StatisticsOverviewUiAction

	data object ViewMoreClicked: StatisticsOverviewUiAction
	data object ApplyFilterClicked: StatisticsOverviewUiAction
}

data class SourcePickerUiState(
	val isShowing: Boolean = false,
	val source: TrackableFilter.SourceSummaries? = null,
)