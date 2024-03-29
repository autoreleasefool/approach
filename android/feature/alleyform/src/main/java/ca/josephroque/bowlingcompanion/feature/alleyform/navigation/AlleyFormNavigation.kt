package ca.josephroque.bowlingcompanion.feature.alleyform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.alleyform.AlleyFormRoute
import java.util.UUID

fun NavController.navigateToNewAlleyForm(navOptions: NavOptions? = null) {
	this.navigate(Route.AddAlley.route, navOptions)
}

fun NavController.navigateToAlleyForm(alleyId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditAlley.createRoute(alleyId), navOptions)
}

fun NavGraphBuilder.alleyFormScreen(
	onBackPressed: () -> Unit,
	onManageLanes: (List<UUID>, NavResultCallback<List<UUID>>) -> Unit,
) {
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
