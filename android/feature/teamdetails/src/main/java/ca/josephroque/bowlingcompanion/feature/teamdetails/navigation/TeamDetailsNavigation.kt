package ca.josephroque.bowlingcompanion.feature.teamdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.TeamDetailsRoute
import java.util.UUID

fun NavController.navigateToTeamDetails(teamId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.TeamDetails.createRoute(teamId))
}

fun NavGraphBuilder.teamDetailsScreen(
	onBackPressed: () -> Unit,
	onAddSeries: (UUID, NavResultCallback<UUID?>) -> Unit,
) {
	composable(
		route = Route.TeamDetails.route,
		arguments = listOf(
			navArgument(Route.TeamDetails.ARG_TEAM) { type = NavType.StringType },
		),
	) {
		TeamDetailsRoute(
			onBackPressed = onBackPressed,
			onAddSeries = onAddSeries,
		)
	}
}
