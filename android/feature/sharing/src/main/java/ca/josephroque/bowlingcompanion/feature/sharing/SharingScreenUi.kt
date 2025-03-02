package ca.josephroque.bowlingcompanion.feature.sharing

import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingUiState

sealed interface SharingScreenUiState {
	data object Loading : SharingScreenUiState

	data class Sharing(val sharing: SharingUiState) : SharingScreenUiState
}

sealed interface SharingScreenUiAction {
	data class DidStartSharing(val source: SharingSource, val isSystemInDarkTheme: Boolean) : SharingScreenUiAction
	data class Sharing(val action: SharingUiAction) : SharingScreenUiAction
}

sealed interface SharingScreenEvent {
	data object Dismissed : SharingScreenEvent
}
