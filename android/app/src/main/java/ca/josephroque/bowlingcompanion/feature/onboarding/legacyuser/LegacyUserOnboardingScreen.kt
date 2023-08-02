package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser.ui.ApproachAppDescription
import ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser.ui.LegacyCompanionHeader
import ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser.ui.NewApproachHeader

@Composable
internal fun LegacyUserOnboardingScreen(
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LegacyUserOnboardingViewModel = hiltViewModel(),
) {
	val legacyUserOnboardingUiState by viewModel.uiState.collectAsStateWithLifecycle()

	when (legacyUserOnboardingUiState) {
		LegacyUserOnboardingUiState.Complete -> onCompleteOnboarding()
		is LegacyUserOnboardingUiState.ImportingData,
		is LegacyUserOnboardingUiState.ShowingApproachHeader,
		LegacyUserOnboardingUiState.Started -> Unit
	}

	LegacyUserOnboarding(
		legacyUserOnboardingUiState = legacyUserOnboardingUiState,
		handleEvent = viewModel::handleEvent,
		modifier = modifier,
	)
}

@Composable
internal fun LegacyUserOnboarding(
	legacyUserOnboardingUiState: LegacyUserOnboardingUiState,
	handleEvent: (LegacyUserOnboardingUiEvent) -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.fillMaxSize(),
	) {
		LegacyCompanionHeader(
			uiState = legacyUserOnboardingUiState,
		)
		NewApproachHeader(
			uiState = legacyUserOnboardingUiState,
			onHeaderClicked = { handleEvent(LegacyUserOnboardingUiEvent.ApproachIconClicked) },
			onHeaderAnimationFinished = { handleEvent(LegacyUserOnboardingUiEvent.ApproachHeaderAnimationFinished) },
		)
		ApproachAppDescription(
			uiState = legacyUserOnboardingUiState,
			onGetStartedClicked = { handleEvent(LegacyUserOnboardingUiEvent.GetStartedClicked) },
		)
	}
}

@Preview
@Composable
internal fun LegacyUserOnboardingPreview() {
	Surface {
		LegacyUserOnboarding(
			legacyUserOnboardingUiState = LegacyUserOnboardingUiState.Started,
			handleEvent = { },
		)
	}
}