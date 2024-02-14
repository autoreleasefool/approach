package ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements

import ca.josephroque.bowlingcompanion.core.model.Acknowledgement

data class AcknowledgementsUiState(
	val acknowledgements: List<Acknowledgement>,
)

sealed interface AcknowledgementsUiAction {
	data object BackClicked : AcknowledgementsUiAction

	data class AcknowledgementClicked(val name: String) : AcknowledgementsUiAction
}
