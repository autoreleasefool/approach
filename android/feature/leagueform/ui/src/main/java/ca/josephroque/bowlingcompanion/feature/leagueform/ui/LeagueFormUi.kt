package ca.josephroque.bowlingcompanion.feature.leagueform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence

enum class IncludeAdditionalPinFall {
	INCLUDE,
	NONE,
}

enum class GamesPerSeries {
	STATIC,
	DYNAMIC,
}

data class LeagueFormTopBarUiState(
	val existingName: String? = null,
)

data class LeagueFormUiState(
	val name: String = "",
	@StringRes val nameErrorId: Int? = null,
	val excludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
	val includeAdditionalPinFall: IncludeAdditionalPinFall = IncludeAdditionalPinFall.NONE,
	val additionalPinFall: Int = 0,
	val additionalGames: Int = 0,
	val recurrence: LeagueRecurrence? = LeagueRecurrence.REPEATING,
	val numberOfGames: Int? = 4,
	val gamesPerSeries: GamesPerSeries? = GamesPerSeries.DYNAMIC,
	val isShowingArchiveDialog: Boolean = false,
	val isArchiveButtonEnabled: Boolean = false,
	val isShowingDiscardChangesDialog: Boolean = false,
)

sealed interface LeagueFormUiAction {
	data object BackClicked : LeagueFormUiAction
	data object DoneClicked : LeagueFormUiAction

	data object ArchiveClicked : LeagueFormUiAction
	data object ConfirmArchiveClicked : LeagueFormUiAction
	data object DismissArchiveClicked : LeagueFormUiAction

	data object DiscardChangesClicked : LeagueFormUiAction
	data object CancelDiscardChangesClicked : LeagueFormUiAction

	data class NameChanged(val name: String) : LeagueFormUiAction
	data class RecurrenceChanged(val recurrence: LeagueRecurrence) : LeagueFormUiAction
	data class ExcludeFromStatisticsChanged(
		val excludeFromStatistics: ExcludeFromStatistics,
	) : LeagueFormUiAction
	data class IncludeAdditionalPinFallChanged(
		val includeAdditionalPinFall: IncludeAdditionalPinFall,
	) : LeagueFormUiAction
	data class NumberOfGamesChanged(val numberOfGames: Int) : LeagueFormUiAction
	data class GamesPerSeriesChanged(val gamesPerSeries: GamesPerSeries) : LeagueFormUiAction
	data class AdditionalPinFallChanged(val additionalPinFall: Int) : LeagueFormUiAction
	data class AdditionalGamesChanged(val additionalGames: Int) : LeagueFormUiAction
}
