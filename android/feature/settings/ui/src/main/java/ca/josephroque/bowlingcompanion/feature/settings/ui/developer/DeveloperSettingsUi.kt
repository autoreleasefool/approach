package ca.josephroque.bowlingcompanion.feature.settings.ui.developer

@Suppress("unused")
data object DeveloperSettingsUiState

sealed interface DeveloperSettingsUiAction {
	data object BackClicked : DeveloperSettingsUiAction
}
