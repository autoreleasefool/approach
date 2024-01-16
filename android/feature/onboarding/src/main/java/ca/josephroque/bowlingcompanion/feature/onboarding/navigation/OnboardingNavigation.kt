package ca.josephroque.bowlingcompanion.feature.onboarding.navigation

import androidx.activity.compose.BackHandler
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.onboarding.OnboardingRoute

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) {
	this.navigate(Route.Onboarding.route, navOptions)
}

fun NavGraphBuilder.onboardingScreen(
	finishActivity: () -> Unit,
	onCompleteOnboarding: () -> Unit,
) {
	composable(
		route = Route.Onboarding.route,
	) {
		BackHandler {
			finishActivity()
		}

		OnboardingRoute(onCompleteOnboarding)
	}
}