package ca.josephroque.bowlingcompanion.feature.onboarding.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.onboarding.OnboardingRoute

const val onboardingNavigationRoute = "onboarding"

fun NavController.navigateToOnboarding() {
	this.navigate("onboarding")
}

fun NavGraphBuilder.onboardingScreen(
	finishActivity: () -> Unit,
	onCompleteOnboarding: () -> Unit,
) {
	composable(
		route = onboardingNavigationRoute,
	) {
		BackHandler {
			finishActivity()
		}

		OnboardingRoute(onCompleteOnboarding)
	}
}