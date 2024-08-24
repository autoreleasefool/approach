package ca.josephroque.bowlingcompanion.feature.featureflagslist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.featureflagslist.FeatureFlagsListRoute

fun NavController.navigateToFeatureFlagsList(navOptions: NavOptions? = null) {
	this.navigate(Route.FeatureFlagsList.route, navOptions)
}

fun NavGraphBuilder.featureFlagsList(onBackPressed: () -> Unit) {
	composable(
		route = Route.FeatureFlagsList.route,
	) {
		FeatureFlagsListRoute(
			onBackPressed = onBackPressed,
		)
	}
}
