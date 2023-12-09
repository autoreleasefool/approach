package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor

import ca.josephroque.bowlingcompanion.core.model.GearListItem
import java.util.UUID

data class RollEditorUiState(
	val recentBalls: List<GearListItem> = emptyList(),
	val selectedBall: UUID? = null,
	val didFoulRoll: Boolean = false,
)

sealed interface RollEditorUiAction {
	data object PickBallClicked: RollEditorUiAction

	data class BallClicked(val ball: GearListItem): RollEditorUiAction
	data class FoulToggled(val foul: Boolean): RollEditorUiAction
}