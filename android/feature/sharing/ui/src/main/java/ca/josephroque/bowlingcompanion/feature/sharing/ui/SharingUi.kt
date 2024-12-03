package ca.josephroque.bowlingcompanion.feature.sharing.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID

sealed interface SharingSource {
	data class Series(val seriesId: SeriesID) : SharingSource
	data class Game(val gameID: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource
}
