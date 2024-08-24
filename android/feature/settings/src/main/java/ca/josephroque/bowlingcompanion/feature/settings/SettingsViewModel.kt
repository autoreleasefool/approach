package ca.josephroque.bowlingcompanion.feature.settings

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ReportedBug
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.SentFeedback
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedAcknowledgements
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedAnalytics
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedArchived
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedDataExport
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedDataImport
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedDeveloper
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedOpponents
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedSource
import ca.josephroque.bowlingcompanion.core.analytics.trackable.settings.ViewedStatistics
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
	featureFlagsClient: FeatureFlagsClient,
	systemInfoService: SystemInfoService,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<SettingsScreenEvent>() {
	private val settingsState: MutableStateFlow<SettingsUiState> = MutableStateFlow(
		SettingsUiState(
			isDataImportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_IMPORT),
			isDataExportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_EXPORT),
			isDevelopmentModeEnabled = featureFlagsClient.isEnabled(FeatureFlag.DEVELOPER_MODE),
			versionName = systemInfoService.versionName,
			versionCode = systemInfoService.versionCode,
		),
	)

	val uiState: StateFlow<SettingsScreenUiState> = settingsState
		.map { SettingsScreenUiState.Loaded(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SettingsScreenUiState.Loading,
		)

	fun handleAction(action: SettingsScreenUiAction) {
		when (action) {
			is SettingsScreenUiAction.SettingsAction -> handleSettingsAction(action.settingsUiAction)
		}
	}

	private fun handleSettingsAction(action: SettingsUiAction) {
		when (action) {
			SettingsUiAction.OpponentsClicked -> showOpponents()
			SettingsUiAction.StatisticsSettingsClicked -> showStatisticsSettings()
			SettingsUiAction.AcknowledgementsClicked -> showAcknowledgements()
			SettingsUiAction.AnalyticsSettingsClicked -> showAnalytics()
			SettingsUiAction.DataImportSettingsClicked -> showDataImport()
			SettingsUiAction.DataExportSettingsClicked -> showDataExport()
			SettingsUiAction.DeveloperSettingsClicked -> showDeveloperSettings()
			SettingsUiAction.ArchivesClicked -> showArchives()
			SettingsUiAction.ViewSourceClicked -> showSource()
			SettingsUiAction.SendFeedbackClicked -> showFeedback()
			SettingsUiAction.ReportBugClicked -> showBugReport()
			SettingsUiAction.FeatureFlagsClicked -> showFeatureFlags()
		}
	}

	private fun showFeedback() {
		analyticsClient.trackEvent(SentFeedback)
	}

	private fun showSource() {
		analyticsClient.trackEvent(ViewedSource)
	}

	private fun showBugReport() {
		analyticsClient.trackEvent(ReportedBug)
	}

	private fun showArchives() {
		sendEvent(SettingsScreenEvent.NavigateToArchives)
		analyticsClient.trackEvent(ViewedArchived)
	}

	private fun showFeatureFlags() {
		sendEvent(SettingsScreenEvent.NavigateToFeatureFlags)
	}

	private fun showDeveloperSettings() {
		sendEvent(SettingsScreenEvent.NavigateToDeveloperSettings)
		analyticsClient.trackEvent(ViewedDeveloper)
	}

	private fun showDataExport() {
		sendEvent(SettingsScreenEvent.NavigateToDataExportSettings)
		analyticsClient.trackEvent(ViewedDataExport)
	}

	private fun showDataImport() {
		sendEvent(SettingsScreenEvent.NavigateToDataImportSettings)
		analyticsClient.trackEvent(ViewedDataImport)
	}

	private fun showAnalytics() {
		sendEvent(SettingsScreenEvent.NavigateToAnalyticsSettings)
		analyticsClient.trackEvent(ViewedAnalytics)
	}

	private fun showAcknowledgements() {
		sendEvent(SettingsScreenEvent.NavigateToAcknowledgements)
		analyticsClient.trackEvent(ViewedAcknowledgements)
	}

	private fun showStatisticsSettings() {
		sendEvent(SettingsScreenEvent.NavigateToStatisticsSettings)
		analyticsClient.trackEvent(ViewedStatistics)
	}

	private fun showOpponents() {
		sendEvent(SettingsScreenEvent.NavigateToOpponents)
		analyticsClient.trackEvent(ViewedOpponents)
	}
}
