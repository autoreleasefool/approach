package ca.josephroque.bowlingcompanion.feature.laneform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.laneform.LaneFormRoute
import java.util.UUID

fun NavController.navigateToLaneFormForResult(
	existingLanes: List<UUID>,
	navResultCallback: NavResultCallback<List<UUID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.EditLanes.createRoute(existingLanes.map { it.toString() }),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.laneFormScreen(
	onDismissWithResult: (List<UUID>) -> Unit,
) {
	composable(
		route = Route.EditLanes.route,
		arguments = listOf(
			navArgument(Route.EditLanes.ARG_LANES) { type = NavType.StringType },
		),
	) {
		LaneFormRoute(
			onDismissWithResult = onDismissWithResult
		)
	}
}