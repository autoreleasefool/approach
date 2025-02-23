package ca.josephroque.bowlingcompanion.feature.sharing.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries

data object SharingUiState

sealed interface SharingSource {
	data class Series(val seriesId: SeriesID) : SharingSource
	data class Game(val gameID: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource
}

sealed interface SharingData {
	data class Series(val series: ShareableSeries) : SharingData
	data object Game : SharingData
	data object Statistic : SharingData
}

enum class SharingAppearance {
	Light,
	Dark,
}