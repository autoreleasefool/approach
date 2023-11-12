package ca.josephroque.bowlingcompanion.feature.settings.developer

import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettingsUiAction

sealed interface DeveloperSettingsScreenUiAction {
	data class DeveloperSettingsAction(val action: DeveloperSettingsUiAction): DeveloperSettingsScreenUiAction
}

sealed interface DeveloperSettingsScreenEvent {
	data object Dismissed: DeveloperSettingsScreenEvent
}