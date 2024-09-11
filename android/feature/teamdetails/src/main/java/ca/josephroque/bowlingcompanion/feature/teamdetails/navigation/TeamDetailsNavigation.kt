package ca.josephroque.bowlingcompanion.feature.teamdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamdetails.TeamDetailsRoute

fun NavController.navigateToTeamDetails(teamId: TeamID, navOptions: NavOptions? = null) {
	this.navigate(Route.TeamDetails.createRoute(teamId), navOptions)
}

fun NavGraphBuilder.teamDetailsScreen(
	onBackPressed: () -> Unit,
	onAddSeries: (TeamID) -> Unit,
	onViewSeries: (TeamSeriesID) -> Unit,
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
			onViewSeries = onViewSeries,
		)
	}
}
