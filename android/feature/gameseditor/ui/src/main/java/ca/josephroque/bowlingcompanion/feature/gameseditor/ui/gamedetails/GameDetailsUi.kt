package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import java.util.UUID

sealed interface NextGameEditableElement {
	data class Roll(val rollIndex: Int) : NextGameEditableElement
	data class Frame(val frameIndex: Int) : NextGameEditableElement
	data class Game(val gameIndex: Int, val game: UUID) : NextGameEditableElement
	data class BowlerGame(val gameIndex: Int, val bowler: UUID) : NextGameEditableElement
	data class Bowler(val name: String, val bowler: UUID) : NextGameEditableElement
}

data class GameDetailsUiState(
	val gameId: UUID,
	val currentGameIndex: Int = 0,
	val bowlers: List<BowlerSummary> = emptyList(),
	val currentBowlerIndex: Int = 0,
	val seriesGameIds: List<UUID> = emptyList(),
	val header: HeaderUiState = HeaderUiState(),
	val gear: GearCardUiState = GearCardUiState(),
	val alley: AlleyCardUiState = AlleyCardUiState(),
	val matchPlay: MatchPlayCardUiState = MatchPlayCardUiState(),
	val scoringMethod: ScoringMethodCardUiState = ScoringMethodCardUiState(),
	val gameProperties: GamePropertiesCardUiState = GamePropertiesCardUiState(),
) {
	data class GamePropertiesCardUiState(
		val locked: GameLockState = GameLockState.LOCKED,
		val gameExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		val seriesExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		val leagueExcludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
		val seriesPreBowl: SeriesPreBowl = SeriesPreBowl.REGULAR,
	)

	data class HeaderUiState(
		val bowlerName: String = "",
		val leagueName: String = "",
		val nextElement: NextGameEditableElement? = null,
		val hasMultipleBowlers: Boolean = false,
	)

	data class AlleyCardUiState(
		val selectedAlley: AlleyDetails? = null,
		val selectedLanes: List<LaneListItem> = emptyList(),
	)

	data class GearCardUiState(
		val selectedGear: List<GearListItem> = emptyList(),
	)

	data class MatchPlayCardUiState(
		val opponentName: String? = null,
		val opponentScore: Int? = null,
		val result: MatchPlayResult? = null,
	)

	data class ScoringMethodCardUiState(
		val score: Int = 0,
		val scoringMethod: GameScoringMethod = GameScoringMethod.BY_FRAME,
	)
}

sealed interface GameDetailsUiAction {
	data object ManageGearClicked : GameDetailsUiAction
	data object ManageAlleyClicked : GameDetailsUiAction
	data object ManageLanesClicked : GameDetailsUiAction
	data object ManageMatchPlayClicked : GameDetailsUiAction
	data object ManageScoreClicked : GameDetailsUiAction
	data object ViewGameStatsClicked : GameDetailsUiAction
	data object ViewSeriesStatsClicked : GameDetailsUiAction
	data object ViewAllBowlersClicked : GameDetailsUiAction

	data class LockToggled(val locked: Boolean) : GameDetailsUiAction
	data class ExcludeFromStatisticsToggled(val excludeFromStatistics: Boolean) : GameDetailsUiAction
	data class NextGameElementClicked(
		val nextGameElement: NextGameEditableElement,
	) : GameDetailsUiAction
	data class HeaderHeightMeasured(val height: Float) : GameDetailsUiAction
}
