package ca.josephroque.bowlingcompanion.feature.settings.ui

data class SettingsUiState(
	val isDataImportsEnabled: Boolean,
	val isDataExportsEnabled: Boolean,
	val versionName: String,
	val versionCode: String,
) {
	val isDataSectionVisible: Boolean
		get() = isDataImportsEnabled || isDataExportsEnabled
}

sealed interface SettingsUiAction {
	data object OpponentsClicked: SettingsUiAction
	data object StatisticsSettingsClicked: SettingsUiAction
	data object AcknowledgementsClicked: SettingsUiAction
	data object AnalyticsSettingsClicked: SettingsUiAction
	data object DataImportSettingsClicked: SettingsUiAction
	data object DataExportSettingsClicked: SettingsUiAction
	data object DeveloperSettingsClicked: SettingsUiAction
	data object ArchivesClicked: SettingsUiAction
}