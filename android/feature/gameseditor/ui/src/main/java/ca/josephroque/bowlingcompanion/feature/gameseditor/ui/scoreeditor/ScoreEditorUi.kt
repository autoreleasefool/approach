package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor

import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod

data class ScoreEditorUiState(
	val score: Int,
	val scoringMethod: GameScoringMethod,
)

sealed interface ScoreEditorUiAction {
	data object SaveClicked : ScoreEditorUiAction
	data object CancelClicked : ScoreEditorUiAction

	data class ScoreChanged(val score: String) : ScoreEditorUiAction
	data class ScoringMethodChanged(val scoringMethod: GameScoringMethod) : ScoreEditorUiAction
}
