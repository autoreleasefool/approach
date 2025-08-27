package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.scoresheet.FramePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.GameIndexPosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScorePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabelContent

data class GamesSharingConfigurationUiState(
	val isSeriesDetailChecked: Boolean = true,
	val isSeriesDateChecked: Boolean = true,
	val isBowlerNameChecked: Boolean = false,
	val isLeagueNameChecked: Boolean = false,
	val isGameIncluded: List<IncludedGame> = emptyList(),
	val style: ScoreSheetConfiguration.Style = ScoreSheetConfiguration.Style.PLAIN,
	val appearance: SharingAppearance = SharingAppearance.Light,
) {
	data class IncludedGame(
		val gameId: GameID,
		val index: Int,
		val isGameIncluded: Boolean,
	)

	val scoreSheetConfiguration: ScoreSheetConfiguration
		get() = ScoreSheetConfiguration(
			style = style,
			framePosition = setOf(FramePosition.TOP),
			scorePosition = setOf(ScorePosition.END),
			gameIndexPosition = setOf(GameIndexPosition.START),
			scrollEnabled = false,
			relativeContainerSizing = false,
		)

	fun includeGame(game: ShareableGame) = isGameIncluded.firstOrNull { it.gameId == game.id }?.isGameIncluded ?: false

	fun title(game: ShareableGame): String? = when {
		isSeriesDateChecked -> game.seriesDate.simpleFormat()
		isBowlerNameChecked -> game.bowlerName
		isLeagueNameChecked -> game.leagueName
		else -> null
	}

	@Composable
	fun labels(games: List<ShareableGame>): List<ChartLabelContent> {
		val labels = mutableListOf<ChartLabelContent>()
		when {
			isSeriesDateChecked -> {
				if (isBowlerNameChecked) {
					labels.add(
						ChartLabelContent(
							icon = rememberVectorPainter(Icons.Default.Person),
							title = games.first().bowlerName,
						),
					)
				}

				if (isLeagueNameChecked) {
					labels.add(
						ChartLabelContent(
							icon = rememberVectorPainter(Icons.Default.Refresh),
							title = games.first().leagueName,
						),
					)
				}
			}
			isBowlerNameChecked -> {
				if (isLeagueNameChecked) {
					labels.add(
						ChartLabelContent(
							icon = rememberVectorPainter(Icons.Default.Refresh),
							title = games.first().leagueName,
						),
					)
				}
			}
			else -> Unit
		}

		if (isSeriesDetailChecked) {
			val total = games.sumOf { it.score.score ?: 0 }
			labels.add(
				ChartLabelContent(
					icon = rememberVectorPainter(Icons.Default.Check),
					title = stringResource(R.string.sharing_series_total_label, total),
				),
			)

			val highScore = games.maxOf { it.score.score ?: 0 }
			labels.add(
				ChartLabelContent(
					icon = rememberVectorPainter(Icons.Default.KeyboardArrowUp),
					title = stringResource(R.string.sharing_series_high_score_label, highScore),
				),
			)

			val lowScore = games.minOf { it.score.score ?: 0 }
			labels.add(
				ChartLabelContent(
					icon = rememberVectorPainter(Icons.Default.KeyboardArrowDown),
					title = stringResource(R.string.sharing_series_low_score_label, lowScore),
				),
			)
		}

		return labels
	}

	fun performAction(action: GamesSharingConfigurationUiAction): GamesSharingConfigurationUiState {
		return when (action) {
			is GamesSharingConfigurationUiAction.IsSeriesDetailCheckedToggled -> copy(
				isSeriesDetailChecked = action.isSeriesDetailChecked,
			)
			is GamesSharingConfigurationUiAction.IsSeriesDateCheckedToggled -> copy(
				isSeriesDateChecked = action.isSeriesDateChecked,
			)
			is GamesSharingConfigurationUiAction.IsBowlerNameCheckedToggled -> copy(
				isBowlerNameChecked = action.isBowlerNameChecked,
			)
			is GamesSharingConfigurationUiAction.IsLeagueNameCheckedToggled -> copy(
				isLeagueNameChecked = action.isLeagueNameChecked,
			)
			is GamesSharingConfigurationUiAction.IsGameIncludedToggled -> {
				val gameIndex = isGameIncluded.indexOfFirst { it.gameId == action.gameId }
				val updatedGame = isGameIncluded[gameIndex].copy(isGameIncluded = action.isGameIncluded)
				copy(isGameIncluded = isGameIncluded.toMutableList().apply { set(gameIndex, updatedGame) })
			}
			is GamesSharingConfigurationUiAction.AppearanceChanged -> copy(appearance = action.appearance)
		}
	}
}

sealed interface GamesSharingConfigurationUiAction {
	data class IsSeriesDetailCheckedToggled(val isSeriesDetailChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsSeriesDateCheckedToggled(val isSeriesDateChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsBowlerNameCheckedToggled(val isBowlerNameChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsLeagueNameCheckedToggled(val isLeagueNameChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsGameIncludedToggled(val gameId: GameID, val isGameIncluded: Boolean) : GamesSharingConfigurationUiAction
	data class AppearanceChanged(val appearance: SharingAppearance) : GamesSharingConfigurationUiAction
}
