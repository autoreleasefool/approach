package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements

import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiState

sealed interface AcknowledgementsSettingsScreenUiState {
	data object Loading: AcknowledgementsSettingsScreenUiState

	data class Loaded(
		val acknowledgements: AcknowledgementsUiState,
	): AcknowledgementsSettingsScreenUiState
}

sealed interface AcknowledgementsSettingsScreenUiAction {
	data class AcknowledgementsAction(val action: AcknowledgementsUiAction): AcknowledgementsSettingsScreenUiAction
}

sealed interface AcknowledgementsSettingsScreenEvent {
	data object Dismissed: AcknowledgementsSettingsScreenEvent
	data class NavigatedToAcknowledgement(val name: String): AcknowledgementsSettingsScreenEvent
}