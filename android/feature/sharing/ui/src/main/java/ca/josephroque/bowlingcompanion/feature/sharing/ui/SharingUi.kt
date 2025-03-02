package ca.josephroque.bowlingcompanion.feature.sharing.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState

sealed interface SharingUiState {
	data class SharingSeries(
		val seriesSharing: SeriesSharingConfigurationUiState,
		val series: SharingData.Series
	) : SharingUiState

	data object SharingGame : SharingUiState
	data object SharingStatistic : SharingUiState

	val sharingData: SharingData
		get() = when (this) {
			is SharingSeries -> series
			is SharingGame -> SharingData.Game
			is SharingStatistic -> SharingData.Statistic
		}
}

sealed interface SharingUiAction {
	data object ShareButtonClicked : SharingUiAction

	data class SeriesSharingAction(val action: SeriesSharingConfigurationUiAction) : SharingUiAction
	data object GameSharingAction : SharingUiAction
	data object StatisticSharingAction : SharingUiAction
}

sealed interface SharingSource {
	data class Series(val seriesId: SeriesID) : SharingSource
	data class Game(val gameID: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource
}

sealed interface SharingData {
	data class Series(
		val series: ShareableSeries,
		val configuration: SeriesSharingConfigurationUiState,
	) : SharingData
	data object Game : SharingData
	data object Statistic : SharingData
}

enum class SharingAppearance {
	Light,
	Dark,
}