package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.ApproachAppDescription
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.LegacyCompanionHeader

@Composable
fun AppNameChangeDetails(
	state: LegacyUserOnboardingUiState.AppNameChange,
	onAction: (AppNameChangeUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LegacyCompanionHeader(isVisible = state.isShowingLegacyHeader)

	ApproachAppDescription(
		isVisible = state.isShowingDetails,
		onAction = onAction,
		modifier = modifier,
	)
}

