package ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate

data class TeamSeriesDetailsUiState(
	val teamSeries: TeamSeries,
	val listItems: List<ListItem>,
	val isShowingPlaceholder: Boolean,
	val gameToArchive: GameToArchive?,
	val gameToRestore: GameToArchive?,
) {
	data class TeamSeries(
		val teamName: String,
		val date: LocalDate,
		val total: Int,
		val numberOfGames: Int,
		val teamScores: ChartEntryModelProducer,
		val seriesLow: Int?,
		val seriesHigh: Int?,
	)

	sealed interface ListItem {
		data class GameHeader(val gameIndex: Int, val teamTotal: Int) : ListItem

		data class GameRow(
			val gameId: GameID,
			val bowlerName: String,
			val score: Int,
			val isArchived: Boolean,
		) : ListItem
	}

	data class GameToArchive(val gameId: GameID, val bowlerName: String, val gameIndex: Int)
}

sealed interface TeamSeriesDetailsUiAction {
	data class GameClicked(val gameId: GameID) : TeamSeriesDetailsUiAction
	data class GameArchived(val gameId: GameID) : TeamSeriesDetailsUiAction
	data class GameRestored(val gameId: GameID) : TeamSeriesDetailsUiAction

	data object ConfirmArchiveClicked : TeamSeriesDetailsUiAction
	data object DismissArchiveClicked : TeamSeriesDetailsUiAction
	data object ConfirmRestoreClicked : TeamSeriesDetailsUiAction
}

data class TeamSeriesDetailsTopBarUiState(val date: LocalDate? = null)

sealed interface TeamSeriesDetailsTopBarUiAction {
	data object BackClicked : TeamSeriesDetailsTopBarUiAction
	data object AddGameClicked : TeamSeriesDetailsTopBarUiAction
}
