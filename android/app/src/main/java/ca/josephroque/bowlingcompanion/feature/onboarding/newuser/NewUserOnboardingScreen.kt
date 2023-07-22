package ca.josephroque.bowlingcompanion.feature.onboarding.newuser

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NewUserOnboardingScreen(
	modifier: Modifier = Modifier,
	viewModel: NewUserOnboardingViewModel = hiltViewModel(),
) {
	Text("new user")
}