package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import ca.josephroque.bowlingcompanion.core.model.Pin

data class FrameEditorUiState(
	val lockedPins: Set<Pin> = emptySet(),
	val downedPins: Set<Pin> = emptySet(),
)

sealed interface FrameEditorUiAction {
	data class DownedPinsChanged(val downedPins: Set<Pin>): FrameEditorUiAction
}