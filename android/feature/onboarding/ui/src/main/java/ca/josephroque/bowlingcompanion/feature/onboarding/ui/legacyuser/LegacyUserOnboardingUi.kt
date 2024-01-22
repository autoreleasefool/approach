package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

sealed interface LegacyUserOnboardingUiState {
	data class AppNameChange(
		val state: LegacyUserOnboardingAppNameChangeUiState,
	): LegacyUserOnboardingUiState

	data object DataImport: LegacyUserOnboardingUiState

	data class ImportError(
		val message: String,
		val exception: Exception?,
	): LegacyUserOnboardingUiState
}

sealed interface LegacyUserOnboardingAppNameChangeUiState {
	data object Started: LegacyUserOnboardingAppNameChangeUiState
	data object ShowingApproachHeader: LegacyUserOnboardingAppNameChangeUiState
	data object ShowingDetails: LegacyUserOnboardingAppNameChangeUiState
}

sealed interface LegacyUserOnboardingUiAction {
	data object GetStartedClicked: LegacyUserOnboardingUiAction
	data object NewApproachHeaderClicked: LegacyUserOnboardingUiAction
	data object NewApproachHeaderAnimationFinished: LegacyUserOnboardingUiAction
}