package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiState

data class GamesEditorUiState(
	val frameEditor: FrameEditorUiState,
	val rollEditor: RollEditorUiState,
	val scoreSheet: ScoreSheetUiState,
	val scoreEditor: ScoreEditorUiState?,
)

sealed interface GamesEditorUiAction {
	data object BackClicked: GamesEditorUiAction
	data object SettingsClicked: GamesEditorUiAction

	data class FrameEditor(val action: FrameEditorUiAction): GamesEditorUiAction
	data class RollEditor(val action: RollEditorUiAction): GamesEditorUiAction
	data class ScoreSheet(val action: ScoreSheetUiAction): GamesEditorUiAction
	data class ScoreEditor(val action: ScoreEditorUiAction): GamesEditorUiAction
}