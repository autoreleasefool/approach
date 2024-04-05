package ca.josephroque.bowlingcompanion.feature.onboarding

import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiState

sealed interface OnboardingScreenUiState {
	data object Loading : OnboardingScreenUiState

	data class LegacyUser(
		val legacyUser: LegacyUserOnboardingUiState =
			LegacyUserOnboardingUiState.AppNameChange(),
	) : OnboardingScreenUiState

	data class NewUser(
		val newUser: NewUserOnboardingUiState =
			NewUserOnboardingUiState.ShowingWelcomeMessage,
	) : OnboardingScreenUiState
}

sealed interface OnboardingScreenUiAction {
	data class LegacyUserOnboardingAction(
		val action: LegacyUserOnboardingUiAction,
	) : OnboardingScreenUiAction

	data class NewUserOnboardingAction(
		val action: NewUserOnboardingUiAction,
	) : OnboardingScreenUiAction
}

sealed interface OnboardingScreenEvent {
	data object FinishedOnboarding : OnboardingScreenEvent
	data object MigrateOpponents : OnboardingScreenEvent
}
