package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
		modifier = modifier,
	)
}

@Composable
internal fun OnboardingScreen(
	onboardingUiState: OnboardingUiState,
	modifier: Modifier = Modifier,
) {
	Column (
		modifier = modifier.fillMaxSize()
	) {
		Text(text = "onboarding")
	}
}