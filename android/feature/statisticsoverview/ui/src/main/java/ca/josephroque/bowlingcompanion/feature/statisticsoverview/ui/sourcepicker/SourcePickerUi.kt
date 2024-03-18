package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter

data class SourcePickerUiState(
	val source: TrackableFilter.SourceSummaries? = null,
)

data class SourcePickerTopBarUiState(
	val isApplyEnabled: Boolean = false,
)

sealed interface SourcePickerUiAction {
	data object ApplyFilterClicked : SourcePickerUiAction

	data object Dismissed : SourcePickerUiAction
	data object BowlerClicked : SourcePickerUiAction
	data object LeagueClicked : SourcePickerUiAction
	data object SeriesClicked : SourcePickerUiAction
	data object GameClicked : SourcePickerUiAction
}
