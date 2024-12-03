package ca.josephroque.bowlingcompanion.feature.sharing.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID

data object SharingTopBarUiState

sealed interface SharingTopBarUiAction {
	data object BackClicked : SharingTopBarUiAction
}

sealed interface SharingSource {
	data class Series(val seriesId: SeriesID) : SharingSource
	data class Game(val gameID: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource
}
