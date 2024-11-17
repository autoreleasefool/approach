package ca.josephroque.bowlingcompanion.feature.sharing.ui

data object SharingTopBarUiState

sealed interface SharingTopBarUiAction {
	data object BackClicked : SharingTopBarUiAction
}
