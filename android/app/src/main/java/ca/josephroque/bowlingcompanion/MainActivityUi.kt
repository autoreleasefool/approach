package ca.josephroque.bowlingcompanion

import ca.josephroque.bowlingcompanion.ui.ApproachAppUiState

sealed interface MainActivityUiState {
	data object Loading : MainActivityUiState
	data class Success(val appState: ApproachAppUiState, val isLaunchComplete: Boolean) : MainActivityUiState
}

internal fun MainActivityUiState.isLaunchComplete(): Boolean = when (this) {
	MainActivityUiState.Loading -> false
	is MainActivityUiState.Success -> this.isLaunchComplete
}
