package ca.josephroque.bowlingcompanion.feature.gameseditor

import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import java.util.UUID

object GamesEditorArguments {
	data class EditMatchPlay(val gameId: UUID)

	data class EditGear(val gearIds: Set<UUID>, val onGearUpdated: NavResultCallback<Set<UUID>>)

	data class EditRolledBall(val ballId: UUID?, val onBallUpdated: NavResultCallback<Set<UUID>>)

	data class EditAlley(val alleyId: UUID?, val onAlleyUpdated: NavResultCallback<Set<UUID>>)

	data class EditLanes(
		val alleyId: UUID,
		val laneIds: Set<UUID>,
		val onLanesUpdated: NavResultCallback<Set<UUID>>,
	)

	data class ShowGamesSettings(
		val series: List<UUID>,
		val currentGameId: UUID,
		val onSeriesUpdated: NavResultCallback<Pair<List<UUID>, UUID>>,
	)

	data class EditScore(
		val score: Int,
		val scoringMethod: GameScoringMethod,
		val onScoreUpdated: NavResultCallback<Pair<GameScoringMethod, Int>>,
	)

	data class ShowStatistics(val filter: TrackableFilter)

	data class ShowBowlerScores(val series: List<UUID>, val gameIndex: Int)
}

sealed interface GamesEditorScreenUiState {
	data object Loading : GamesEditorScreenUiState

	data class Loaded(
		val isGameLockSnackBarVisible: Boolean = false,
		val highestScorePossibleAlert: HighestScorePossibleAlertUiState? = null,
		val gameDetails: GameDetailsUiState,
		val gamesEditor: GamesEditorUiState,
		val bottomSheet: GamesEditorScreenBottomSheetUiState,
	) : GamesEditorScreenUiState
}

data class HighestScorePossibleAlertUiState(val score: Int)

data class GamesEditorScreenBottomSheetUiState(
	val headerPeekHeight: Float = 0f,
	val isGameDetailsSheetVisible: Boolean = true,
)

sealed interface GamesEditorScreenUiAction {
	data object DidAppear : GamesEditorScreenUiAction
	data object DidDisappear : GamesEditorScreenUiAction
	data object GameLockSnackBarDismissed : GamesEditorScreenUiAction
	data object HighestPossibleScoreSnackBarDismissed : GamesEditorScreenUiAction

	data class GearUpdated(val gearIds: Set<UUID>) : GamesEditorScreenUiAction
	data class AlleyUpdated(val alleyId: UUID?) : GamesEditorScreenUiAction
	data class LanesUpdated(val laneIds: Set<UUID>) : GamesEditorScreenUiAction
	data class GamesEditor(val action: GamesEditorUiAction) : GamesEditorScreenUiAction
	data class GameDetails(val action: GameDetailsUiAction) : GamesEditorScreenUiAction
	data class SeriesUpdated(val series: List<UUID>) : GamesEditorScreenUiAction
	data class CurrentGameUpdated(val gameId: UUID) : GamesEditorScreenUiAction
	data class SelectedBallUpdated(val ballId: UUID?) : GamesEditorScreenUiAction
	data class ScoreUpdated(val score: Int, val scoringMethod: GameScoringMethod) :
		GamesEditorScreenUiAction
}

sealed interface GamesEditorScreenEvent {
	data object Dismissed : GamesEditorScreenEvent

	data class EditMatchPlay(val gameId: UUID) : GamesEditorScreenEvent
	data class EditGear(val gearIds: Set<UUID>) : GamesEditorScreenEvent
	data class EditAlley(val alleyId: UUID?) : GamesEditorScreenEvent
	data class EditLanes(val alleyId: UUID, val laneIds: Set<UUID>) : GamesEditorScreenEvent
	data class EditRolledBall(val ballId: UUID?) : GamesEditorScreenEvent
	data class ShowGamesSettings(val series: List<UUID>, val currentGameId: UUID) :
		GamesEditorScreenEvent
	data class EditScore(val score: Int, val scoringMethod: GameScoringMethod) : GamesEditorScreenEvent
	data class ShowStatistics(val filter: TrackableFilter) : GamesEditorScreenEvent
	data class ShowBowlerScores(val series: List<UUID>, val gameIndex: Int) : GamesEditorScreenEvent
}
