package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding

sealed interface AccessoriesOnboardingUiAction {
	data object GetStartedClicked: AccessoriesOnboardingUiAction
}