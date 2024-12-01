package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding

@Suppress("unused")
data object AccessoriesOnboardingUiState

sealed interface AccessoriesOnboardingUiAction {
	data object GetStartedClicked : AccessoriesOnboardingUiAction
	data object SheetDismissed : AccessoriesOnboardingUiAction
}
