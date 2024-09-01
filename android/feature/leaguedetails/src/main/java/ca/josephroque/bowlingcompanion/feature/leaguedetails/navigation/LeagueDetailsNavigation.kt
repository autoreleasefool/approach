package ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.leaguedetails.LeagueDetailsRoute

fun NavController.navigateToLeagueDetails(leagueId: LeagueID, navOptions: NavOptions? = null) {
	this.navigate(Route.LeagueDetails.createRoute(leagueId), navOptions)
}

fun NavGraphBuilder.leagueDetailsScreen(
	onBackPressed: () -> Unit,
	onEditSeries: (SeriesID) -> Unit,
	onAddSeries: (LeagueID, NavResultCallback<SeriesID?>) -> Unit,
	onShowSeriesDetails: (SeriesID) -> Unit,
	onUsePreBowl: (LeagueID) -> Unit,
) {
	composable(
		route = Route.LeagueDetails.route,
		arguments = listOf(
			navArgument(Route.LeagueDetails.ARG_LEAGUE) { type = NavType.StringType },
		),
	) {
		LeagueDetailsRoute(
			onEditSeries = onEditSeries,
			onAddSeries = onAddSeries,
			onShowSeriesDetails = onShowSeriesDetails,
			onBackPressed = onBackPressed,
			onUsePreBowl = onUsePreBowl,
		)
	}
}
