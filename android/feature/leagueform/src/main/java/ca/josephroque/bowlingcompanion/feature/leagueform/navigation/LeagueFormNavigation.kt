package ca.josephroque.bowlingcompanion.feature.leagueform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.leagueform.LeagueFormRoute
import java.util.UUID

const val BOWLER_ID = "bowlerId"
const val LEAGUE_ID = "leagueId"
const val editLeagueNavigationRoute = "edit_league/{$LEAGUE_ID}"
const val addLeagueNavigationRoute = "add_league/{$BOWLER_ID}"

fun NavController.navigateToLeagueForm(leagueId: UUID) {
	val encoded = Uri.encode(leagueId.toString())
	this.navigate("edit_league/$encoded")
}

fun NavController.navigateToNewLeagueForm(bowlerId: UUID) {
	val encoded = Uri.encode(bowlerId.toString())
	this.navigate("add_league/$encoded")
}

fun NavGraphBuilder.leagueFormScreen(
	onBackPressed: () -> Unit
) {
	composable(
		route = editLeagueNavigationRoute,
		arguments = listOf(
			navArgument(LEAGUE_ID) { type = NavType.StringType },
		),
	) {
		LeagueFormRoute(
			onDismiss = onBackPressed,
		)
	}
	composable(
		route = addLeagueNavigationRoute,
		arguments = listOf(
			navArgument(BOWLER_ID) { type = NavType.StringType },
		),
	) {
		LeagueFormRoute(
			onDismiss = onBackPressed,
		)
	}
}
