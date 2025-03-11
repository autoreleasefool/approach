package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.ui.graphics.ImageBitmap
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState
import kotlinx.coroutines.Deferred

sealed interface SharingUiState {
	data class SharingSeries(
		val seriesSharing: SeriesSharingConfigurationUiState,
		val series: SharingData.Series,
	) : SharingUiState

	data object SharingGame : SharingUiState
	data object SharingStatistic : SharingUiState
	data object SharingTeamSeries: SharingUiState

	val sharingData: SharingData
		get() = when (this) {
			is SharingSeries -> series
			is SharingGame -> SharingData.Game
			is SharingStatistic -> SharingData.Statistic
			is SharingTeamSeries -> SharingData.TeamSeries
		}
}

sealed interface SharingUiAction {
	data class ShareButtonClicked(val image: Deferred<ImageBitmap>) : SharingUiAction

	data class SeriesSharingAction(val action: SeriesSharingConfigurationUiAction) : SharingUiAction
	data object GameSharingAction : SharingUiAction
	data object StatisticSharingAction : SharingUiAction
}

sealed interface SharingSource {
	data class TeamSeries(val teamSeriesId: TeamSeriesID) : SharingSource
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
	data object TeamSeries: SharingData
}

enum class SharingAppearance {
	Light,
	Dark,
}
