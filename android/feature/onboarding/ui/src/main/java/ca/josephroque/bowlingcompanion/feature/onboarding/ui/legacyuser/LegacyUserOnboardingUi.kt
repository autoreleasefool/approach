package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

sealed interface LegacyUserOnboardingUiState {
	data object Started: LegacyUserOnboardingUiState

	data class ShowingApproachHeader(
		val isDetailsVisible: Boolean = false
	): LegacyUserOnboardingUiState

	data object ImportingData: LegacyUserOnboardingUiState
}

sealed interface LegacyUserOnboardingUiAction {
	data object GetStartedClicked: LegacyUserOnboardingUiAction
	data object NewApproachHeaderClicked: LegacyUserOnboardingUiAction
	data object NewApproachHeaderAnimationFinished: LegacyUserOnboardingUiAction
}