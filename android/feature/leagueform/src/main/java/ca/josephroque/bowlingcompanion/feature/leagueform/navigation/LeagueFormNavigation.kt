package ca.josephroque.bowlingcompanion.feature.leagueform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.leagueform.LeagueFormRoute

fun NavController.navigateToLeagueForm(leagueId: LeagueID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditLeague.createRoute(leagueId), navOptions)
}

fun NavController.navigateToNewLeagueForm(bowlerId: BowlerID, navOptions: NavOptions? = null) {
	this.navigate(Route.AddLeague.createRoute(bowlerId), navOptions)
}

fun NavGraphBuilder.leagueFormScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.EditLeague.route,
		arguments = listOf(
			navArgument(Route.EditLeague.ARG_LEAGUE) { type = NavType.StringType },
		),
	) {
		LeagueFormRoute(
			onDismiss = onBackPressed,
		)
	}
	composable(
		route = Route.AddLeague.route,
		arguments = listOf(
			navArgument(Route.AddLeague.ARG_BOWLER) { type = NavType.StringType },
		),
	) {
		LeagueFormRoute(
			onDismiss = onBackPressed,
		)
	}
}
