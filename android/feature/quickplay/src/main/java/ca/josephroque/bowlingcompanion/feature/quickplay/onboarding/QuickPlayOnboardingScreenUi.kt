package ca.josephroque.bowlingcompanion.feature.quickplay.onboarding

import ca.josephroque.bowlingcompanion.feature.quickplay.ui.onboarding.QuickPlayOnboardingUiAction

sealed interface QuickPlayOnboardingScreenUiState {
	data object Loading : QuickPlayOnboardingScreenUiState
	data object Loaded : QuickPlayOnboardingScreenUiState
}

sealed interface QuickPlayOnboardingScreenUiAction {
	data class QuickPlayOnboarding(val action: QuickPlayOnboardingUiAction) :
		QuickPlayOnboardingScreenUiAction
}

sealed interface QuickPlayOnboardingScreenEvent {
	data object Dismissed : QuickPlayOnboardingScreenEvent
}
