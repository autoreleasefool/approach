package ca.josephroque.bowlingcompanion.feature.settings.ui.developer

sealed interface DeveloperSettingsUiAction {
	data object BackClicked: DeveloperSettingsUiAction
}