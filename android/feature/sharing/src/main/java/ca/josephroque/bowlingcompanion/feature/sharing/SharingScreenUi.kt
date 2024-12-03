package ca.josephroque.bowlingcompanion.feature.sharing

import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingUiState

sealed interface SharingScreenUiState {
	data object Loading : SharingScreenUiState

	data class SharingSeries(val seriesSharing: SeriesSharingUiState) : SharingScreenUiState
}

sealed interface SharingScreenUiAction {
	data class DidStartSharing(val source: SharingSource) : SharingScreenUiAction
	data class SeriesSharingAction(val action: SeriesSharingUiAction) : SharingScreenUiAction
}

sealed interface SharingScreenEvent {
	data object Dismissed : SharingScreenEvent
}
