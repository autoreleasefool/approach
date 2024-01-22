package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
				is LegacyUserOnboardingUiState.AppNameChange -> when (state.state) {
					LegacyUserOnboardingAppNameChangeUiState.Started,
					LegacyUserOnboardingAppNameChangeUiState.ShowingApproachHeader -> false
					LegacyUserOnboardingAppNameChangeUiState.ShowingDetails -> true
				}
				LegacyUserOnboardingUiState.DataImport, is LegacyUserOnboardingUiState.ImportError -> true
			}
		)

		NewApproachHeader(
			isHeaderAtTop = when (state) {
				is LegacyUserOnboardingUiState.AppNameChange -> when (state.state) {
					LegacyUserOnboardingAppNameChangeUiState.Started -> false
					LegacyUserOnboardingAppNameChangeUiState.ShowingApproachHeader,
					LegacyUserOnboardingAppNameChangeUiState.ShowingDetails -> true
				}
				LegacyUserOnboardingUiState.DataImport, is LegacyUserOnboardingUiState.ImportError -> true
			},
			onAction = onAction,
		)

		when (state) {
			is LegacyUserOnboardingUiState.AppNameChange -> AppNameChangeDetails(
				state = state.state,
				onAction = onAction,
			)
			LegacyUserOnboardingUiState.DataImport -> DataImportOnboarding()
			is LegacyUserOnboardingUiState.ImportError -> ImportErrorOnboarding()
		}
	}
}

@Preview
@Composable
private fun LegacyUserOnboardingPreview() {
	Surface {
		LegacyUserOnboarding(
			state = LegacyUserOnboardingUiState.DataImport,
			onAction = {},
		)
	}
}