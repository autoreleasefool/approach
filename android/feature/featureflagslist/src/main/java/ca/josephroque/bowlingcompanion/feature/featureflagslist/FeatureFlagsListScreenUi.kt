package ca.josephroque.bowlingcompanion.feature.featureflagslist

import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsListUiAction
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsListUiState

sealed interface FeatureFlagsListScreenUiState {
	data object Loading : FeatureFlagsListScreenUiState

	data class Loaded(val featureFlagsList: FeatureFlagsListUiState) : FeatureFlagsListScreenUiState
}

sealed interface FeatureFlagsListScreenUiAction {
	data class List(val action: FeatureFlagsListUiAction) : FeatureFlagsListScreenUiAction
}

sealed interface FeatureFlagsListScreenEvent {
	data object Dismissed : FeatureFlagsListScreenEvent
}
