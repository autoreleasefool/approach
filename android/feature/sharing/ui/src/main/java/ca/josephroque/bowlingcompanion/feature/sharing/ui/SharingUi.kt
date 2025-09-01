package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
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
		val supportedFormats: List<SharingSourceFormat>,
	) : SharingUiState

	data class SharingGames(
		val gamesSharing: GamesSharingConfigurationUiState,
		val games: SharingData.Games,
		val supportedFormats: List<SharingSourceFormat>,
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

	val supportedSharingFormats: List<SharingSourceFormat>
		get() = when (this) {
			is SharingSeries -> supportedFormats
			is SharingGames -> supportedFormats
			else -> emptyList()
		}
}

sealed interface SharingUiAction {
	data class ShareButtonClicked(val image: Deferred<ImageBitmap>) : SharingUiAction
	data class SourceFormatChanged(val format: SharingSourceFormat) : SharingUiAction

	data class SeriesSharingAction(val action: SeriesSharingConfigurationUiAction) : SharingUiAction
	data class GameSharingAction(val action: GamesSharingConfigurationUiAction) : SharingUiAction
	data object StatisticSharingAction : SharingUiAction
}

sealed interface SharingSource {
	data class TeamSeries(val teamSeriesId: TeamSeriesID) : SharingSource
	data class Series(val seriesId: SeriesID, val format: SharingSourceFormat) : SharingSource
	data class Game(val gameId: GameID) : SharingSource
	data class Statistic(val statisticId: String) : SharingSource

	val supportedFormats: List<SharingSourceFormat>
		get() = when (this) {
			is TeamSeries -> emptyList()
			is Series -> listOf(SharingSourceFormat.SERIES, SharingSourceFormat.GAMES)
			is Game -> listOf(SharingSourceFormat.GAMES)
			is Statistic -> emptyList()
		}
}

enum class SharingSourceFormat {
	SERIES,
	GAMES,
	;

	@Composable
	fun title(): String = when (this) {
		SERIES -> stringResource(R.string.sharing_source_format_series)
		GAMES -> stringResource(R.string.sharing_source_format_games)
	}
}

sealed interface SharingData {
	data class Series(val series: ShareableSeries, val configuration: SeriesSharingConfigurationUiState) : SharingData

	data class Games(val games: List<ShareableGame>, val configuration: GamesSharingConfigurationUiState) : SharingData

	data object Statistic : SharingData
	data object TeamSeries : SharingData

	val format: SharingSourceFormat?
		get() = when (this) {
			is Series -> SharingSourceFormat.SERIES
			is Games -> SharingSourceFormat.GAMES
			else -> null
		}
}

enum class SharingAppearance {
	Light,
	Dark,
	;

	@Composable
	fun title(): String = when (this) {
		Light -> stringResource(R.string.sharing_appearance_light)
		Dark -> stringResource(R.string.sharing_appearance_dark)
	}
}
