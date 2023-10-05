package ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.leaguedetails.LeagueDetailsRoute
import java.util.UUID

const val LEAGUE_ID = "leagueid"
const val leagueDetailsNavigationRoute = "league/{$LEAGUE_ID}"

fun NavController.navigateToLeagueDetails(leagueId: UUID) {
	val encoded = Uri.encode(leagueId.toString())
	this.navigate("league/$encoded") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.leagueDetailsScreen(
	onBackPressed: () -> Unit,
	onEditSeries: (UUID) -> Unit,
	onAddSeries: () -> Unit,
	onShowSeriesDetails: (UUID) -> Unit,
) {
	composable(
		route = leagueDetailsNavigationRoute,
		arguments = listOf(
			navArgument(LEAGUE_ID) { type = NavType.StringType },
		),
	) {
		LeagueDetailsRoute(
			onEditSeries = onEditSeries,
			onAddSeries = onAddSeries,
			onShowSeriesDetails = onShowSeriesDetails,
			onBackPressed = onBackPressed,
		)
	}
}