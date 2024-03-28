package ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.leaguedetails.LeagueDetailsRoute
import java.util.UUID

fun NavController.navigateToLeagueDetails(leagueId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.LeagueDetails.createRoute(leagueId), navOptions)
}

fun NavGraphBuilder.leagueDetailsScreen(
	onBackPressed: () -> Unit,
	onEditSeries: (UUID) -> Unit,
	onAddSeries: (UUID, NavResultCallback<UUID?>) -> Unit,
	onShowSeriesDetails: (UUID) -> Unit,
	onUsePreBowl: (UUID) -> Unit,
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
