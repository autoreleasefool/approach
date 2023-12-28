package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import ca.josephroque.bowlingcompanion.core.model.Pin

data class FrameEditorUiState(
	val gameIndex: Int = 0,
	val isEnabled: Boolean = false,
	val lockedPins: Set<Pin> = emptySet(),
	val downedPins: Set<Pin> = emptySet(),
)

sealed interface FrameEditorUiAction {
	data object FrameEditorInteractionStarted: FrameEditorUiAction
	data class DownedPinsChanged(val downedPins: Set<Pin>): FrameEditorUiAction
}