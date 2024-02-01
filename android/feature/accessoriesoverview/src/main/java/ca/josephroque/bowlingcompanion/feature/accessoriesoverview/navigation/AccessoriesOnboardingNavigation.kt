package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.onboarding.AccessoriesOnboardingRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToAccessoriesOnboarding(navOptions: NavOptions? = null) {
	this.navigate(Route.AccessoriesOnboarding.route, navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.accessoriesOnboardingSheet(
	onBackPressed: () -> Unit,
) {
	bottomSheet(
		route = Route.AccessoriesOnboarding.route,
	) {
		AccessoriesOnboardingRoute(
			onDismiss = onBackPressed,
		)
	}
}