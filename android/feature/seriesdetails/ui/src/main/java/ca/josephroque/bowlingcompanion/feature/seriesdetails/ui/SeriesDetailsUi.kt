package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesDetailsProperties
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate

data class SeriesDetailsUiState(
	val details: SeriesDetailsProperties,
	val gamesList: GamesListUiState,
	val scores: ChartEntryModelProducer,
	val seriesLow: Int?,
	val seriesHigh: Int?,
	val isShowingPlaceholder: Boolean,
)

sealed interface SeriesDetailsUiAction {
	data class GamesList(val action: GamesListUiAction) : SeriesDetailsUiAction
}

data class SeriesDetailsTopBarUiState(
	val seriesDate: LocalDate? = null,
	val isSharingButtonVisible: Boolean = false,
)

sealed interface SeriesDetailsTopBarUiAction {
	data object BackClicked : SeriesDetailsTopBarUiAction
	data object ShareClicked : SeriesDetailsTopBarUiAction
	data object AddGameClicked : SeriesDetailsTopBarUiAction
}
