package ca.josephroque.bowlingcompanion.feature.seriesform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.seriesform.SeriesFormRoute
import java.util.UUID

const val LEAGUE_ID = "leagueid"
const val SERIES_ID = "seriesid"

const val editSeriesNavigationRoute = "edit_series/{$SERIES_ID}"
const val addSeriesNavigationRoute = "add_series/{$LEAGUE_ID}"

fun NavController.navigateToSeriesForm(seriesId: UUID) {
	val encoded = UUID.fromString(seriesId.toString())
	this.navigate("edit_series/$encoded")
}

fun NavController.navigateToNewSeriesForm(leagueId: UUID, result: NavResultCallback<UUID?>) {
	val encoded = UUID.fromString(leagueId.toString())
	this.navigateForResult("add_series/$encoded", result)
}

fun NavGraphBuilder.seriesFormScreen(
	onDismissWithResult: (UUID?) -> Unit,
	onEditAlley: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	composable(
		route = editSeriesNavigationRoute,
		arguments = listOf(
			navArgument(SERIES_ID) { type = NavType.StringType }
		),
	) {
		SeriesFormRoute(
			onDismissWithResult = onDismissWithResult,
			onEditAlley = onEditAlley,
		)
	}
	composable(
		route = addSeriesNavigationRoute,
		arguments = listOf(
			navArgument(LEAGUE_ID) { type = NavType.StringType }
		),
	) {
		SeriesFormRoute(
			onDismissWithResult = onDismissWithResult,
			onEditAlley = onEditAlley,
		)
	}
}