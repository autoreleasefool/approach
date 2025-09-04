package ca.josephroque.bowlingcompanion.feature.settings.ui

data class SettingsUiState(val isDevelopmentModeEnabled: Boolean, val versionName: String, val versionCode: String)

sealed interface SettingsUiAction {
	data object OpponentsClicked : SettingsUiAction
	data object StatisticsSettingsClicked : SettingsUiAction
	data object AcknowledgementsClicked : SettingsUiAction
	data object AnalyticsSettingsClicked : SettingsUiAction
	data object DataImportSettingsClicked : SettingsUiAction
	data object DataExportSettingsClicked : SettingsUiAction
	data object DeveloperSettingsClicked : SettingsUiAction
	data object ArchivesClicked : SettingsUiAction
	data object ViewSourceClicked : SettingsUiAction
	data object SendFeedbackClicked : SettingsUiAction
	data object ReportBugClicked : SettingsUiAction
	data object FeatureFlagsClicked : SettingsUiAction
	data object ForceCrashClicked : SettingsUiAction
	data object AchievementsClicked : SettingsUiAction
}
