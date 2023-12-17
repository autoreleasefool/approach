package ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser

sealed interface NewUserOnboardingUiState {
	data object ShowingWelcomeMessage: NewUserOnboardingUiState

	data class ShowingLogbook(
		val name: String
	): NewUserOnboardingUiState
}

sealed interface NewUserOnboardingUiAction {
	data object GetStartedClicked: NewUserOnboardingUiAction
	data object AddBowlerClicked: NewUserOnboardingUiAction

	data class NameChanged(
		val name: String,
	): NewUserOnboardingUiAction
}