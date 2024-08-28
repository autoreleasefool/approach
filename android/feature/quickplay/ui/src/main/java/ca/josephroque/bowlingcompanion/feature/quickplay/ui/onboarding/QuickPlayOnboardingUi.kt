package ca.josephroque.bowlingcompanion.feature.quickplay.ui.onboarding

@Suppress("unused")
data object QuickPlayOnboardingUiState

sealed interface QuickPlayOnboardingUiAction {
	data object BackClicked : QuickPlayOnboardingUiAction
	data object DoneClicked : QuickPlayOnboardingUiAction
}
