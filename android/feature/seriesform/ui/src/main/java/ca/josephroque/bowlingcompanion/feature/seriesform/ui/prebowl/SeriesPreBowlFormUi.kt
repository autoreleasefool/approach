package ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl

import ca.josephroque.bowlingcompanion.core.model.SeriesSummary
import kotlinx.datetime.LocalDate

data class SeriesPreBowlFormUiState(
	val series: SeriesSummary? = null,
	val appliedDate: LocalDate,
	val isAppliedDatePickerVisible: Boolean = false,
)

data class SeriesPreBowlFormTopBarUiState(
	val isDoneEnabled: Boolean = false,
)

sealed interface SeriesPreBowlFormTopBarUiAction {
	data object BackClicked : SeriesPreBowlFormTopBarUiAction
	data object DoneClicked : SeriesPreBowlFormTopBarUiAction
}

sealed interface SeriesPreBowlFormUiAction {
	data object SeriesClicked : SeriesPreBowlFormUiAction

	data object AppliedDateClicked : SeriesPreBowlFormUiAction
	data object AppliedDatePickerDismissed : SeriesPreBowlFormUiAction
	data class AppliedDateChanged(val date: LocalDate) : SeriesPreBowlFormUiAction
}
