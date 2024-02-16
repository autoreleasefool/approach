package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.lanes

data object CopyLanesDialogUiState

sealed interface CopyLanesDialogUiAction {
	data object Dismissed : CopyLanesDialogUiAction
	data object CopyToAllClicked : CopyLanesDialogUiAction
}
