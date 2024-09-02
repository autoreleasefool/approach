package ca.josephroque.bowlingcompanion.feature.gameseditor

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState

object GamesEditorArguments {
	data class EditMatchPlay(val gameId: GameID)

	data class EditGear(val gearIds: Set<GearID>, val onGearUpdated: NavResultCallback<Set<GearID>>)

	data class EditRolledBall(val ballId: GearID?, val onBallUpdated: NavResultCallback<Set<GearID>>)

	data class EditAlley(val alleyId: AlleyID?, val onAlleyUpdated: NavResultCallback<Set<AlleyID>>)

	data class EditLanes(
		val alleyId: AlleyID,
		val laneIds: Set<LaneID>,
		val onLanesUpdated: NavResultCallback<Set<LaneID>>,
	)

	data class ShowGamesSettings(
		val series: List<SeriesID>,
		val currentGameId: GameID,
		val onSeriesUpdated: NavResultCallback<Pair<List<SeriesID>, GameID>>,
	)

	data class EditScore(
		val score: Int,
		val scoringMethod: GameScoringMethod,
		val onScoreUpdated: NavResultCallback<Pair<GameScoringMethod, Int>>,
	)

	data class ShowStatistics(val filter: TrackableFilter)

	data class ShowBowlerScores(val series: List<SeriesID>, val gameIndex: Int)
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

	data class GearUpdated(val gearIds: Set<GearID>) : GamesEditorScreenUiAction
	data class AlleyUpdated(val alleyId: AlleyID?) : GamesEditorScreenUiAction
	data class LanesUpdated(val laneIds: Set<LaneID>) : GamesEditorScreenUiAction
	data class GamesEditor(val action: GamesEditorUiAction) : GamesEditorScreenUiAction
	data class GameDetails(val action: GameDetailsUiAction) : GamesEditorScreenUiAction
	data class SeriesUpdated(val series: List<SeriesID>) : GamesEditorScreenUiAction
	data class CurrentGameUpdated(val gameId: GameID) : GamesEditorScreenUiAction
	data class SelectedBallUpdated(val ballId: GearID?) : GamesEditorScreenUiAction
	data class ScoreUpdated(val score: Int, val scoringMethod: GameScoringMethod) :
		GamesEditorScreenUiAction
}

sealed interface GamesEditorScreenEvent {
	data object Dismissed : GamesEditorScreenEvent

	data class EditMatchPlay(val gameId: GameID) : GamesEditorScreenEvent
	data class EditGear(val gearIds: Set<GearID>) : GamesEditorScreenEvent
	data class EditAlley(val alleyId: AlleyID?) : GamesEditorScreenEvent
	data class EditLanes(val alleyId: AlleyID, val laneIds: Set<LaneID>) : GamesEditorScreenEvent
	data class EditRolledBall(val ballId: GearID?) : GamesEditorScreenEvent
	data class ShowGamesSettings(val series: List<SeriesID>, val currentGameId: GameID) :
		GamesEditorScreenEvent
	data class EditScore(val score: Int, val scoringMethod: GameScoringMethod) : GamesEditorScreenEvent
	data class ShowStatistics(val filter: TrackableFilter) : GamesEditorScreenEvent
	data class ShowBowlerScores(val series: List<SeriesID>, val gameIndex: Int) :
		GamesEditorScreenEvent
}
