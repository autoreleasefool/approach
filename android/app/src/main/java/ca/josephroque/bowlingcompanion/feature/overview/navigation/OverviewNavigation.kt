package ca.josephroque.bowlingcompanion.feature.overview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.overview.OverviewRoute

const val overviewNavigationRoute = "overview"

fun NavController.navigateToOverview(navOptions: NavOptions? = null) {
	this.navigate(overviewNavigationRoute, navOptions)
}

fun NavGraphBuilder.overviewScreen() {
	composable(
		route = overviewNavigationRoute,
	) {
		OverviewRoute()
	}
}
