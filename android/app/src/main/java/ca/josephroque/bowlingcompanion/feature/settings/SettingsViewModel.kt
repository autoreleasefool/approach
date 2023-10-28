package ca.josephroque.bowlingcompanion.feature.settings

import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	featureFlagsClient: FeatureFlagsClient,
): ViewModel() {

	private val _settingsState: MutableStateFlow<SettingsUiState> = MutableStateFlow(
		SettingsUiState(
			isDataImportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_IMPORT),
			isDataExportsEnabled = featureFlagsClient.isEnabled(FeatureFlag.DATA_EXPORT),
		)
	)
	val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()
}

data class SettingsUiState(
	val isDataImportsEnabled: Boolean,
	val isDataExportsEnabled: Boolean,
) {
	val isDataSectionVisible: Boolean
		get() = isDataImportsEnabled || isDataExportsEnabled
}