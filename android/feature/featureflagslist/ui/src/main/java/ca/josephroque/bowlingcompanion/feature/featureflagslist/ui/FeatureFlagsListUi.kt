package ca.josephroque.bowlingcompanion.feature.featureflagslist.ui

import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag

data class FeatureFlagState(val flag: FeatureFlag, val isEnabled: Boolean)

data class FeatureFlagsListUiState(val featureFlags: List<FeatureFlagState>)

sealed interface FeatureFlagsListUiAction {
	data object BackClicked : FeatureFlagsListUiAction
	data object ResetOverridesClicked : FeatureFlagsListUiAction
	data class FeatureFlagToggled(val flag: FeatureFlag, val isEnabled: Boolean) :
		FeatureFlagsListUiAction
}
