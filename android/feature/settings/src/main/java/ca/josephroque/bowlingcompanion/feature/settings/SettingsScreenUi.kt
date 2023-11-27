package ca.josephroque.bowlingcompanion.feature.settings

import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiState

sealed interface SettingsScreenUiState {
	data object Loading: SettingsScreenUiState

	data class Loaded(
		val settings: SettingsUiState,
	): SettingsScreenUiState
}

sealed interface SettingsScreenUiAction {
	data class ReceivedVersionInfo(
		val versionName: String,
		val versionCode: String,
	): SettingsScreenUiAction
	data class SettingsAction(val settingsUiAction: SettingsUiAction): SettingsScreenUiAction
}

sealed interface SettingsScreenEvent {
	data object NavigateToOpponents: SettingsScreenEvent
	data object NavigateToStatisticsSettings: SettingsScreenEvent
	data object NavigateToAcknowledgements: SettingsScreenEvent
	data object NavigateToAnalyticsSettings: SettingsScreenEvent
	data object NavigateToDataImportSettings: SettingsScreenEvent
	data object NavigateToDataExportSettings: SettingsScreenEvent
	data object NavigateToDeveloperSettings: SettingsScreenEvent
	data object NavigateToArchives: SettingsScreenEvent
}