package ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.seriesdetails.SeriesDetailsRoute
import java.util.UUID

const val SERIES_ID = "seriesid"
const val seriesDetailsNavigationRoute = "series/{$SERIES_ID}"

fun NavController.navigateToSeriesDetails(seriesId: UUID) {
	val encoded = Uri.encode(seriesId.toString())
	this.navigate("series/$encoded") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.seriesDetailsScreen(
	onBackPressed: () -> Unit,
	onEditGame: (UUID, UUID) -> Unit,
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
}