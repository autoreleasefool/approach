package ca.josephroque.bowlingcompanion.feature.gameseditor.scoreeditor

import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiState

sealed interface ScoreEditorScreenUiState {
	data object Loading : ScoreEditorScreenUiState

	data class Loaded(
		val scoreEditor: ScoreEditorUiState,
	) : ScoreEditorScreenUiState
}

sealed interface ScoreEditorScreenUiAction {
	data class ScoreEditor(val action: ScoreEditorUiAction) : ScoreEditorScreenUiAction
}

sealed interface ScoreEditorScreenEvent {
	data class Dismissed(val scoringMethod: GameScoringMethod, val score: Int) : ScoreEditorScreenEvent
}
