package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding

sealed interface QuickPlayOnboardingUiAction {
	data object BackClicked: QuickPlayOnboardingUiAction
	data object DoneClicked: QuickPlayOnboardingUiAction
}