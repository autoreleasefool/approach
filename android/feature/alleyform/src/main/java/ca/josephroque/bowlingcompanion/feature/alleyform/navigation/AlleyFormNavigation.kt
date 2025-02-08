package ca.josephroque.bowlingcompanion.feature.alleyform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.alleyform.AlleyFormRoute

fun NavController.navigateToNewAlleyForm(navOptions: NavOptions? = null) {
	this.navigate(Route.AddAlley.route, navOptions)
}

fun NavController.navigateToAlleyForm(alleyId: AlleyID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditAlley.createRoute(alleyId), navOptions)
}

fun NavGraphBuilder.alleyFormScreen(onBackPressed: () -> Unit, onManageLanes: (List<LaneID>) -> Unit) {
	composable(
		route = Route.EditAlley.route,
		arguments = listOf(
			navArgument(Route.EditAlley.ARG_ALLEY) { type = NavType.StringType },
		),
	) {
		AlleyFormRoute(
			onDismiss = onBackPressed,
			onManageLanes = onManageLanes,
		)
	}

	composable(
		route = Route.AddAlley.route,
	) {
		AlleyFormRoute(
			onDismiss = onBackPressed,
			onManageLanes = onManageLanes,
		)
	}
}
