package ca.josephroque.bowlingcompanion.feature.matchplayeditor

import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiAction
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiState
import java.util.UUID

sealed interface MatchPlayEditorScreenUiState {
	data object Loading: MatchPlayEditorScreenUiState

	data class Loaded(
		val matchPlayEditor: MatchPlayEditorUiState,
	): MatchPlayEditorScreenUiState
}

sealed interface MatchPlayEditorScreenUiAction {
	data object LoadMatchPlay: MatchPlayEditorScreenUiAction
	data class UpdatedOpponent(val opponent: UUID?): MatchPlayEditorScreenUiAction
	data class MatchPlayEditor(val action: MatchPlayEditorUiAction): MatchPlayEditorScreenUiAction
}

sealed interface MatchPlayEditorScreenEvent {
	data object Dismissed: MatchPlayEditorScreenEvent
	data class EditOpponent(val opponent: UUID?): MatchPlayEditorScreenEvent
}