package ca.josephroque.bowlingcompanion.feature.teamseriesdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.TeamSeriesDetailsRoute

fun NavController.navigateToTeamSeriesDetails(teamSeriesId: TeamSeriesID) {
	navigate(Route.TeamSeriesDetails.createRoute(teamSeriesId))
}

fun NavGraphBuilder.teamSeriesDetailsScreen(
	onBackPressed: () -> Unit,
	onEditGame: (TeamSeriesID, GameID) -> Unit,
) {
	composable(
		route = Route.TeamSeriesDetails.route,
		arguments = listOf(
			navArgument(Route.TeamSeriesDetails.ARG_TEAM_SERIES) { type = NavType.StringType },
		),
	) {
		TeamSeriesDetailsRoute(
			onBackPressed = onBackPressed,
			onEditGame = onEditGame,
		)
	}
}
