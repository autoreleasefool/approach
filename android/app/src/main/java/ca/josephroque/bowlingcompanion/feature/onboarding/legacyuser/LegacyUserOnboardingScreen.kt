package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LegacyUserOnboardingScreen(
	modifier: Modifier = Modifier,
	viewModel: LegacyUserOnboardingViewModel = hiltViewModel(),
) {
	Text("legacy user")
}