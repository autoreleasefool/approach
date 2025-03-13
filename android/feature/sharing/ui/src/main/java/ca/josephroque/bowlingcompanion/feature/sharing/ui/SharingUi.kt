package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.ui.graphics.ImageBitmap
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.GamesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.GamesSharingConfigurationUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState
import kotlinx.coroutines.Deferred

sealed interface SharingUiState {
	data class SharingSeries(
		val seriesSharing: SeriesSharingConfigurationUiState,
		val series: SharingData.Series,
	) : SharingUiState

	data class SharingGames(
		val gamesSharing: GamesSharingConfigurationUiState,
		val games: SharingData.Games,
	) : SharingUiState

	data object SharingStatistic : SharingUiState
	data object SharingTeamSeries : SharingUiState

	val sharingData: SharingData
		get() = when (this) {
			is SharingSeries -> series
			is SharingGames -> games
			is SharingStatistic -> SharingData.Statistic
			is SharingTeamSeries -> SharingData.TeamSeries
		}
}

sealed interface SharingUiAction {
	data class ShareButtonClicked(val image: Deferred<ImageBitmap>) : SharingUiAction

	data class SeriesSharingAction(val action: SeriesSharingConfigurationUiAction) : SharingUiAction
	data class GameSharingAction(val action: GamesSharingConfigurationUiAction) : SharingUiAction
	data object StatisticSharingAction : SharingUiAction
}

sealed interface SharingSource {
	data class TeamSeries(val teamSeriesId: TeamSeriesID) : SharingSource
	data class Series(val seriesId: SeriesID) : SharingSource
	data class Game(val gameId: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource
}

sealed interface SharingData {
	data class Series(
		val series: ShareableSeries,
		val configuration: SeriesSharingConfigurationUiState,
	) : SharingData

	data class Games(
		val games: List<ShareableGame>,
		val configuration: GamesSharingConfigurationUiState,
	) : SharingData

	data object Statistic : SharingData
	data object TeamSeries : SharingData
}

enum class SharingAppearance {
	Light,
	Dark,
}
