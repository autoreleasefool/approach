package ca.josephroque.bowlingcompanion.feature.settings

import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.featureFlags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureFlags.FeatureFlagsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val featureFlagsClient: FeatureFlagsClient,
): ViewModel() {

	private val _settingsState: MutableStateFlow<SettingsUiState> = MutableStateFlow(
		SettingsUiState(isDataExportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_EXPORT))
	)
	val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()
}

data class SettingsUiState(
	val isDataExportsEnabled: Boolean,
)