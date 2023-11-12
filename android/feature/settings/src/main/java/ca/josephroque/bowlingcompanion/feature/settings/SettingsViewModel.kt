package ca.josephroque.bowlingcompanion.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	featureFlagsClient: FeatureFlagsClient,
): ViewModel() {
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

	private val _events: MutableStateFlow<SettingsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: SettingsScreenUiAction) {
		when (action) {
			SettingsScreenUiAction.HandledNavigation -> _events.value = null
			is SettingsScreenUiAction.ReceivedVersionInfo -> updateVersionInfo(action.versionName, action.versionCode)
			is SettingsScreenUiAction.SettingsAction -> handleSettingsAction(action.settingsUiAction)
		}
	}

	private fun handleSettingsAction(action: SettingsUiAction) {
		when (action) {
			SettingsUiAction.OpponentsClicked -> _events.value = SettingsScreenEvent.NavigateToOpponents
			SettingsUiAction.StatisticsSettingsClicked -> _events.value = SettingsScreenEvent.NavigateToStatisticsSettings
			SettingsUiAction.AcknowledgementsClicked -> _events.value = SettingsScreenEvent.NavigateToAcknowledgements
			SettingsUiAction.AnalyticsSettingsClicked -> _events.value = SettingsScreenEvent.NavigateToAnalyticsSettings
			SettingsUiAction.DataImportSettingsClicked -> _events.value = SettingsScreenEvent.NavigateToDataImportSettings
			SettingsUiAction.DataExportSettingsClicked -> _events.value = SettingsScreenEvent.NavigateToDataExportSettings
			SettingsUiAction.DeveloperSettingsClicked -> _events.value = SettingsScreenEvent.NavigateToDeveloperSettings
		}
	}

	private fun updateVersionInfo(versionName: String, versionCode: String) {
		_settingsState.value = _settingsState.value.copy(
			versionName = versionName,
			versionCode = versionCode,
		)
	}
}