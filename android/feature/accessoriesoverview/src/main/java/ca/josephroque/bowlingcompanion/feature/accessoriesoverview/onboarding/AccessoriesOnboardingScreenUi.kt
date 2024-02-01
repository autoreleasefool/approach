package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.onboarding

import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding.AccessoriesOnboardingUiAction

sealed interface AccessoriesOnboardingScreenUiAction {
	data object Dismissed: AccessoriesOnboardingScreenUiAction
	data class AccessoriesOnboarding(val action: AccessoriesOnboardingUiAction): AccessoriesOnboardingScreenUiAction
}

sealed interface AccessoriesOnboardingScreenEvent {
	data object Dismissed: AccessoriesOnboardingScreenEvent
}