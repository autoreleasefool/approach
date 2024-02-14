package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import ca.josephroque.bowlingcompanion.core.model.FrameEdit

data class RollEditorUiState(
	val recentBalls: List<FrameEdit.Gear> = emptyList(),
	val selectedBall: FrameEdit.Gear? = null,
	val didFoulRoll: Boolean = false,
)

sealed interface RollEditorUiAction {
	data object PickBallClicked : RollEditorUiAction

	data class BallClicked(val ball: FrameEdit.Gear) : RollEditorUiAction
	data class FoulToggled(val foul: Boolean) : RollEditorUiAction
}
