package ca.josephroque.bowlingcompanion.feature.settings

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	featureFlagsClient: FeatureFlagsClient,
): ApproachViewModel<SettingsScreenEvent>() {
	private val _settingsState: MutableStateFlow<SettingsUiState> = MutableStateFlow(
		SettingsUiState(
			isDataImportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_IMPORT),
			isDataExportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_EXPORT),
			versionName = "",
			versionCode = "",
		)
	)

	val uiState: StateFlow<SettingsScreenUiState> = _settingsState
		.map { SettingsScreenUiState.Loaded(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SettingsScreenUiState.Loading,
		)

	fun handleAction(action: SettingsScreenUiAction) {
		when (action) {
			is SettingsScreenUiAction.ReceivedVersionInfo -> updateVersionInfo(action.versionName, action.versionCode)
			is SettingsScreenUiAction.SettingsAction -> handleSettingsAction(action.settingsUiAction)
		}
	}

	private fun handleSettingsAction(action: SettingsUiAction) {
		when (action) {
			SettingsUiAction.OpponentsClicked -> sendEvent(SettingsScreenEvent.NavigateToOpponents)
			SettingsUiAction.StatisticsSettingsClicked -> sendEvent(SettingsScreenEvent.NavigateToStatisticsSettings)
			SettingsUiAction.AcknowledgementsClicked -> sendEvent(SettingsScreenEvent.NavigateToAcknowledgements)
			SettingsUiAction.AnalyticsSettingsClicked -> sendEvent(SettingsScreenEvent.NavigateToAnalyticsSettings)
			SettingsUiAction.DataImportSettingsClicked -> sendEvent(SettingsScreenEvent.NavigateToDataImportSettings)
			SettingsUiAction.DataExportSettingsClicked -> sendEvent(SettingsScreenEvent.NavigateToDataExportSettings)
			SettingsUiAction.DeveloperSettingsClicked -> sendEvent(SettingsScreenEvent.NavigateToDeveloperSettings)
			SettingsUiAction.ArchivesClicked -> sendEvent(SettingsScreenEvent.NavigateToArchives)
		}
	}

	private fun updateVersionInfo(versionName: String, versionCode: String) {
		_settingsState.value = _settingsState.value.copy(
			versionName = versionName,
			versionCode = versionCode,
		)
	}
}