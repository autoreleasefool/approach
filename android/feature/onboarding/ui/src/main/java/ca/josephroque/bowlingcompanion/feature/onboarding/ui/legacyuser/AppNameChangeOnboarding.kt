package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.ApproachAppDescription
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.LegacyCompanionHeader

@Composable
fun AppNameChangeDetails(
	state: LegacyUserOnboardingAppNameChangeUiState,
	onAction: (LegacyUserOnboardingUiAction) -> Unit,
) {
	LegacyCompanionHeader(state = state)

	ApproachAppDescription(
		isVisible = when (state) {
			LegacyUserOnboardingAppNameChangeUiState.Started,
			LegacyUserOnboardingAppNameChangeUiState.ShowingApproachHeader
			-> false
			LegacyUserOnboardingAppNameChangeUiState.ShowingDetails -> true
		},
		onAction = onAction,
		modifier = Modifier.padding(top = 128.dp),
	)
}

