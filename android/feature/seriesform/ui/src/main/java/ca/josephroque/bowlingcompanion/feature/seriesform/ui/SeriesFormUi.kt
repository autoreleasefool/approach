package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import kotlinx.datetime.LocalDate

data class SeriesFormUiState(
	val numberOfGames: Int?,
	val date: LocalDate,
	val isDatePickerVisible: Boolean,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
	val leagueExcludeFromStatistics: ExcludeFromStatistics,
	val alley: AlleyDetails?,
	val isShowingArchiveDialog: Boolean,
	val isArchiveButtonEnabled: Boolean,
)

sealed interface SeriesFormUiAction {
	data object BackClicked : SeriesFormUiAction
	data object DoneClicked : SeriesFormUiAction

	data object ArchiveClicked : SeriesFormUiAction
	data object ConfirmArchiveClicked : SeriesFormUiAction
	data object DismissArchiveClicked : SeriesFormUiAction

	data object AlleyClicked : SeriesFormUiAction

	data object DateClicked: SeriesFormUiAction
	data object DatePickerDismissed : SeriesFormUiAction

	data class NumberOfGamesChanged(val numberOfGames: Int) : SeriesFormUiAction
	data class DateChanged(val date: LocalDate) : SeriesFormUiAction
	data class PreBowlChanged(val preBowl: SeriesPreBowl) : SeriesFormUiAction
	data class ExcludeFromStatisticsChanged(val excludeFromStatistics: ExcludeFromStatistics) : SeriesFormUiAction
}

data class SeriesFormTopBarUiState(
	val existingDate: LocalDate? = null,
)