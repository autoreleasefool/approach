package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboarding
import kotlinx.coroutines.launch

@Composable
internal fun OnboardingRoute(
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OnboardingViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						OnboardingScreenEvent.FinishedOnboarding -> onCompleteOnboarding()
					}
				}
		}
	}

	OnboardingScreen(
		state = uiState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun OnboardingScreen(
	state: OnboardingScreenUiState,
	onAction: (OnboardingScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxSize(),
	) {
		when (state) {
			OnboardingScreenUiState.Loading -> Unit
			is OnboardingScreenUiState.LegacyUser ->
				LegacyUserOnboarding(
					state = state.legacyUser,
					onAction = { onAction(OnboardingScreenUiAction.LegacyUserOnboardingAction(it)) },
				)
			is OnboardingScreenUiState.NewUser ->
				NewUserOnboarding(
					state = state.newUser,
					onAction = { onAction(OnboardingScreenUiAction.NewUserOnboardingAction(it)) },
				)
		}
	}
}
