package ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.seriesdetails.EditGameArgs
import ca.josephroque.bowlingcompanion.feature.seriesdetails.SeriesDetailsRoute

fun NavController.navigateToEvent(leagueId: LeagueID, navOptions: NavOptions? = null) {
	this.navigate(Route.EventDetails.createRoute(leagueId), navOptions)
}

fun NavController.navigateToSeriesDetails(seriesId: SeriesID, navOptions: NavOptions? = null) {
	this.navigate(Route.SeriesDetails.createRoute(seriesId), navOptions)
}

fun NavGraphBuilder.seriesDetailsScreen(
	onBackPressed: () -> Unit,
	onEditGame: (EditGameArgs) -> Unit,
) {
	composable(
		route = Route.SeriesDetails.route,
		arguments = listOf(
			navArgument(Route.SeriesDetails.ARG_SERIES) { type = NavType.StringType },
		),
	) {
		SeriesDetailsRoute(
			onBackPressed = onBackPressed,
			onEditGame = onEditGame,
		)
	}
	composable(
		route = Route.EventDetails.route,
		arguments = listOf(
			navArgument(Route.EventDetails.ARG_EVENT) { type = NavType.StringType },
		),
	) {
		SeriesDetailsRoute(
			onBackPressed = onBackPressed,
			onEditGame = onEditGame,
		)
	}
}
