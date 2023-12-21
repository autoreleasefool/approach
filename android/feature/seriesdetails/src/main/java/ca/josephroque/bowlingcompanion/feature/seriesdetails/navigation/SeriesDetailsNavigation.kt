package ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.seriesdetails.EditGameArgs
import ca.josephroque.bowlingcompanion.feature.seriesdetails.SeriesDetailsRoute
import java.util.UUID

const val SERIES_ID = "seriesid"
const val EVENT_ID = "eventid"
const val seriesDetailsNavigationRoute = "series/{$SERIES_ID}"
const val eventDetailsNavigationRoute = "event/{$EVENT_ID}"

fun NavController.navigateToEvent(leagueId: UUID) {
	val encoded = Uri.encode(leagueId.toString())
	this.navigate("event/$encoded")
}

fun NavController.navigateToSeriesDetails(seriesId: UUID) {
	val encoded = Uri.encode(seriesId.toString())
	this.navigate("series/$encoded")
}

fun NavGraphBuilder.seriesDetailsScreen(
	onBackPressed: () -> Unit,
	onEditGame: (EditGameArgs) -> Unit,
) {
	composable(
		route = seriesDetailsNavigationRoute,
		arguments = listOf(
			navArgument(SERIES_ID) { type = NavType.StringType },
		),
	) {
		SeriesDetailsRoute(
			onBackPressed = onBackPressed,
			onEditGame = onEditGame,
		)
	}
	composable(
		route = eventDetailsNavigationRoute,
		arguments = listOf(
			navArgument(EVENT_ID) { type = NavType.StringType },
		),
	) {
		SeriesDetailsRoute(
			onBackPressed = onBackPressed,
			onEditGame = onEditGame,
		)
	}
}