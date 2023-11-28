package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesDetailsProperties
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import kotlinx.datetime.LocalDate

data class SeriesDetailsUiState(
	val details: SeriesDetailsProperties,
	val scores: ChartEntryModel?,
	val gamesList: GamesListUiState,
)

sealed interface SeriesDetailsUiAction {
	data object BackClicked: SeriesDetailsUiAction
	data object AddGameClicked: SeriesDetailsUiAction

	data class GamesList(val action: GamesListUiAction): SeriesDetailsUiAction
}