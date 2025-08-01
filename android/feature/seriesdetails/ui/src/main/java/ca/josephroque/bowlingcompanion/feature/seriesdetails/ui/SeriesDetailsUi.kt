package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiAction
import ca.josephroque.bowlingcompanion.feature.gameslist.ui.GamesListUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate

data class SeriesDetailsUiState(
	val details: SeriesDetails.Properties,
	val gamesList: GamesListUiState,
	val scores: ChartEntryModelProducer,
	val seriesLow: Int?,
	val seriesHigh: Int?,
	val isShowingPlaceholder: Boolean,
	val sharingSeries: SharingSource?,
)

sealed interface SeriesDetailsUiAction {
	data class GamesList(val action: GamesListUiAction) : SeriesDetailsUiAction
}

data class SeriesDetailsTopBarUiState(
	val seriesDate: LocalDate? = null,
	val isSharingButtonVisible: Boolean = false,
	val isReorderGamesButtonVisible: Boolean = false,
	val isReorderingGames: Boolean = false,
)

sealed interface SeriesDetailsTopBarUiAction {
	data object BackClicked : SeriesDetailsTopBarUiAction
	data object ShareClicked : SeriesDetailsTopBarUiAction
	data object AddGameClicked : SeriesDetailsTopBarUiAction
	data object CancelReorderClicked : SeriesDetailsTopBarUiAction
	data object ConfirmReorderClicked : SeriesDetailsTopBarUiAction
	data object ReorderGamesClicked : SeriesDetailsTopBarUiAction
}
