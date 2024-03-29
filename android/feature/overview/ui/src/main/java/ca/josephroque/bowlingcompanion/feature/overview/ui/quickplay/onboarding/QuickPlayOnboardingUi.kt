package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding

@Suppress("unused")
data object QuickPlayOnboardingUiState

sealed interface QuickPlayOnboardingUiAction {
	data object BackClicked : QuickPlayOnboardingUiAction
	data object DoneClicked : QuickPlayOnboardingUiAction
}
