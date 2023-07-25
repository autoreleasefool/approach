package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser.LegacyUserOnboardingScreen
import ca.josephroque.bowlingcompanion.feature.onboarding.newuser.NewUserOnboardingScreen

@Composable
internal fun OnboardingRoute(
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OnboardingViewModel = hiltViewModel(),
) {
	val onboardingUiState by viewModel.uiState.collectAsStateWithLifecycle()

	when (onboardingUiState) {
		OnboardingUiState.NewUser, OnboardingUiState.Loading, OnboardingUiState.LegacyUser -> Unit
		OnboardingUiState.Completed -> onCompleteOnboarding()
	}

	OnboardingScreen(
		onboardingUiState = onboardingUiState,
		onCompleteOnboarding = onCompleteOnboarding,
		modifier = modifier,
	)
}

@Composable
internal fun OnboardingScreen(
	onboardingUiState: OnboardingUiState,
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row (
		modifier = modifier.fillMaxSize()
	) {
		when (onboardingUiState) {
			OnboardingUiState.Loading, OnboardingUiState.Completed -> Unit
			OnboardingUiState.NewUser -> NewUserOnboardingScreen(
				onCompleteOnboarding = onCompleteOnboarding,
			)
			OnboardingUiState.LegacyUser -> LegacyUserOnboardingScreen(
				onCompleteOnboarding = onCompleteOnboarding,
			)
		}
	}
}