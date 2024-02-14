package ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details

import ca.josephroque.bowlingcompanion.core.model.Acknowledgement

data class AcknowledgementDetailsUiState(
	val acknowledgement: Acknowledgement,
)

sealed interface AcknowledgementDetailsUiAction {
	data object BackClicked : AcknowledgementDetailsUiAction
}

data class AcknowledgementDetailsTopBarUiState(
	val name: String = "",
)
