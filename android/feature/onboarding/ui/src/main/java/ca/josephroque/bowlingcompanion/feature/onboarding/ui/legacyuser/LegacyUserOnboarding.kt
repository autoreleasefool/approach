package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.ApproachAppAnimatedIcons
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components.NewApproachHeader

@Composable
fun LegacyUserOnboarding(
	state: LegacyUserOnboardingUiState,
	onAction: (LegacyUserOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(modifier = modifier.fillMaxSize()) {
		ApproachAppAnimatedIcons(
			isVisible = when (state) {
				is LegacyUserOnboardingUiState.AppNameChange -> state.isShowingApproachHeader || state.isShowingDetails
				is LegacyUserOnboardingUiState.DataImport, is LegacyUserOnboardingUiState.ImportError -> true
			}
		)

		NewApproachHeader(
			isHeaderAtTop = when (state) {
				is LegacyUserOnboardingUiState.AppNameChange -> state.isShowingApproachHeader
				is LegacyUserOnboardingUiState.DataImport, is LegacyUserOnboardingUiState.ImportError -> true
			},
			onAction = onAction,
		)

		when (state) {
			is LegacyUserOnboardingUiState.AppNameChange -> AppNameChangeDetails(
				state = state,
				onAction = { onAction(LegacyUserOnboardingUiAction.AppNameChange(it)) },
				modifier = Modifier.padding(top = 128.dp),
			)
			is LegacyUserOnboardingUiState.DataImport -> DataImportOnboarding(
				state = state,
				onAction = { onAction(LegacyUserOnboardingUiAction.DataImport(it)) },
				modifier = Modifier.padding(top = 144.dp),
			)
			is LegacyUserOnboardingUiState.ImportError -> ImportErrorOnboarding(
				state = state,
				onAction = { onAction(LegacyUserOnboardingUiAction.ImportError(it)) },
				modifier = Modifier.padding(top = 144.dp),
			)
		}
	}
}

@Preview
@Composable
private fun LegacyUserOnboardingPreview() {
	Surface {
		LegacyUserOnboarding(
			state = LegacyUserOnboardingUiState.AppNameChange(),
			onAction = {},
		)
	}
}