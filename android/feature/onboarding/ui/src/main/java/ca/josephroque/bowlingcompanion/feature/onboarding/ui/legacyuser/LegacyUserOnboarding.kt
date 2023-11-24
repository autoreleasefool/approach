package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.ApproachAppDescription
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.LegacyCompanionHeader
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.NewApproachHeader

@Composable
fun LegacyUserOnboarding(
	state: LegacyUserOnboardingUiState,
	onAction: (LegacyUserOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(modifier = modifier.fillMaxSize()) {
		LegacyCompanionHeader(state = state)
		NewApproachHeader(state = state, onAction = onAction)
		ApproachAppDescription(state = state, onAction = onAction)
	}
}

@Preview
@Composable
private fun LegacyUserOnboardingPreview() {
	Surface {
		LegacyUserOnboarding(
			state = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = true),
			onAction = {},
		)
	}
}