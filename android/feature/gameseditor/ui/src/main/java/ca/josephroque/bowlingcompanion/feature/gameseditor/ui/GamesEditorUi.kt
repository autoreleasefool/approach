package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import java.util.UUID

data class GamesEditorUiState(
	val gameId: UUID,
	val frames: List<FrameEdit> = emptyList(),
	val frameEditor: FrameEditorUiState = FrameEditorUiState(),
	val rollEditor: RollEditorUiState = RollEditorUiState(),
	val scoreSheet: ScoreSheetUiState = ScoreSheetUiState(),
	val manualScore: Int? = null,
)

sealed interface GamesEditorUiAction {
	data object BackClicked : GamesEditorUiAction
	data object SettingsClicked : GamesEditorUiAction
	data object ManualScoreClicked : GamesEditorUiAction

	data class FrameEditor(val action: FrameEditorUiAction) : GamesEditorUiAction
	data class RollEditor(val action: RollEditorUiAction) : GamesEditorUiAction
	data class ScoreSheet(val action: ScoreSheetUiAction) : GamesEditorUiAction
}
