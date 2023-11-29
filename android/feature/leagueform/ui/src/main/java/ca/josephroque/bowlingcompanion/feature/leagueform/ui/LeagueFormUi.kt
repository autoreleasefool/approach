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
	val name: String,
	@StringRes val nameErrorId: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
	val includeAdditionalPinFall: IncludeAdditionalPinFall,
	val additionalPinFall: Int,
	val additionalGames: Int,
	val recurrence: LeagueRecurrence? = null,
	val numberOfGames: Int? = null,
	val gamesPerSeries: GamesPerSeries? = null,
	val isShowingArchiveDialog: Boolean,
	val isArchiveButtonEnabled: Boolean,
)

sealed interface LeagueFormUiAction {
	data object BackClicked: LeagueFormUiAction
	data object DoneClicked: LeagueFormUiAction

	data object ArchiveClicked: LeagueFormUiAction
	data object ConfirmArchiveClicked: LeagueFormUiAction
	data object DismissArchiveClicked: LeagueFormUiAction

	data class NameChanged(val name: String): LeagueFormUiAction
	data class RecurrenceChanged(val recurrence: LeagueRecurrence): LeagueFormUiAction
	data class ExcludeFromStatisticsChanged(val excludeFromStatistics: ExcludeFromStatistics): LeagueFormUiAction
	data class IncludeAdditionalPinFallChanged(val includeAdditionalPinFall: IncludeAdditionalPinFall): LeagueFormUiAction
	data class NumberOfGamesChanged(val numberOfGames: Int): LeagueFormUiAction
	data class GamesPerSeriesChanged(val gamesPerSeries: GamesPerSeries): LeagueFormUiAction
	data class AdditionalPinFallChanged(val additionalPinFall: Int): LeagueFormUiAction
	data class AdditionalGamesChanged(val additionalGames: Int): LeagueFormUiAction
}