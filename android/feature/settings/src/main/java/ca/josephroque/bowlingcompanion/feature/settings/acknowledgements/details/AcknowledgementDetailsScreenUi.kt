package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details

import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiState

sealed interface AcknowledgementDetailsScreenUiState {
	data object Loading : AcknowledgementDetailsScreenUiState
	data class Loaded(
		val acknowledgementDetails: AcknowledgementDetailsUiState,
		val topBar: AcknowledgementDetailsTopBarUiState,
	) : AcknowledgementDetailsScreenUiState
}

sealed interface AcknowledgementDetailsScreenUiAction {
	data class AcknowledgementDetailsAction(
		val action: AcknowledgementDetailsUiAction,
	) : AcknowledgementDetailsScreenUiAction
}

sealed interface AcknowledgementDetailsScreenEvent {
	data object Dismissed : AcknowledgementDetailsScreenEvent
}
