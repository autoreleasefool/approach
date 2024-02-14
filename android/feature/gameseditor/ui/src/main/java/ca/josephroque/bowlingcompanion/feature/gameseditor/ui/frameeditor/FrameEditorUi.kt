package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import ca.josephroque.bowlingcompanion.core.model.Pin

enum class AnimationDirection {
	RIGHT_TO_LEFT,
	LEFT_TO_RIGHT,
}

data class FrameEditorUiState(
	val nextAnimationDirection: AnimationDirection? = null,
	val isEnabled: Boolean = false,
	val lockedPins: Set<Pin> = emptySet(),
	val downedPins: Set<Pin> = emptySet(),
)

sealed interface FrameEditorUiAction {
	data object AnimationFinished : FrameEditorUiAction
	data object FrameEditorInteractionStarted : FrameEditorUiAction

	data class DownedPinsChanged(val downedPins: Set<Pin>) : FrameEditorUiAction
}
