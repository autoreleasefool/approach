package ca.josephroque.bowlingcompanion.feature.gameseditor

import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import java.util.UUID

sealed interface GamesEditorScreenUiState {
	data object Loading: GamesEditorScreenUiState

	data class Loaded(
		val headerPeekHeight: Float = 0f,
		val isGameLockSnackBarVisible: Boolean = false,
		val gameDetails: GameDetailsUiState,
		val gamesEditor: GamesEditorUiState,
	): GamesEditorScreenUiState
}

sealed interface GamesEditorScreenUiAction {
	data object LoadInitialGame: GamesEditorScreenUiAction
	data object GameLockSnackBarDismissed: GamesEditorScreenUiAction

	data class GearUpdated(val gearIds: Set<UUID>): GamesEditorScreenUiAction
	data class AlleyUpdated(val alleyId: UUID?): GamesEditorScreenUiAction
	data class LanesUpdated(val laneIds: Set<UUID>): GamesEditorScreenUiAction
	data class GamesEditor(val action: GamesEditorUiAction): GamesEditorScreenUiAction
	data class GameDetails(val action: GameDetailsUiAction): GamesEditorScreenUiAction
}

sealed interface GamesEditorScreenEvent {
	data object Dismissed: GamesEditorScreenEvent

	data class EditMatchPlay(val gameId: UUID): GamesEditorScreenEvent
	data class EditGear(val gearIds: Set<UUID>): GamesEditorScreenEvent
	data class EditAlley(val alleyId: UUID?): GamesEditorScreenEvent
	data class EditLanes(val alleyId: UUID, val laneIds: Set<UUID>): GamesEditorScreenEvent
}