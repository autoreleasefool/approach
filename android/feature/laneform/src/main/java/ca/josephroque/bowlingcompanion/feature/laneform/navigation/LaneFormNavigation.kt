package ca.josephroque.bowlingcompanion.feature.laneform.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.navigation.LaneFormResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.laneform.LaneFormRoute

fun NavController.navigateToLaneFormForResult(existingLanes: List<LaneID>, navOptions: NavOptions? = null) {
	this.navigate(
		route = Route.EditLanes.createRoute(existingLanes.map { it.toString() }),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.laneFormScreen(navController: NavController, onDismiss: () -> Unit) {
	composable(
		route = Route.EditLanes.route,
		arguments = listOf(
			navArgument(Route.EditLanes.ARG_LANES) { type = NavType.StringType },
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<LaneFormResultViewModel>()
		} else {
			hiltViewModel<LaneFormResultViewModel>(parentEntry)
		}

		LaneFormRoute(
			onDismissWithResult = { ids ->
				resultViewModel.setResult(ids)
				onDismiss()
			},
		)
	}
}
