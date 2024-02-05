package ca.josephroque.bowlingcompanion.feature.overview.quickplay.onboarding

import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding.QuickPlayOnboardingUiAction

sealed interface QuickPlayOnboardingScreenUiState {
	data object Loading: QuickPlayOnboardingScreenUiState
	data object Loaded: QuickPlayOnboardingScreenUiState
}

sealed interface QuickPlayOnboardingScreenUiAction {
	data class QuickPlayOnboarding(
		val action: QuickPlayOnboardingUiAction,
	): QuickPlayOnboardingScreenUiAction
}

sealed interface QuickPlayOnboardingScreenEvent {
	data object Dismissed: QuickPlayOnboardingScreenEvent
}