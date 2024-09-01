package ca.josephroque.bowlingcompanion.feature.seriesform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.seriesform.prebowl.SeriesPreBowlFormRoute
import java.util.UUID

fun NavController.navigateToSeriesPreBowlForm(leagueId: LeagueID, navOptions: NavOptions? = null) {
	this.navigate(Route.SeriesPreBowl.createRoute(leagueId), navOptions)
}

fun NavGraphBuilder.seriesPreBowlFormScreen(
	onDismiss: () -> Unit,
	onShowSeriesPicker: (LeagueID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	composable(
		route = Route.SeriesPreBowl.route,
		arguments = listOf(
			navArgument(Route.SeriesPreBowl.ARG_LEAGUE) { type = NavType.StringType },
		),
	) {
		SeriesPreBowlFormRoute(
			onDismiss = onDismiss,
			onShowSeriesPicker = onShowSeriesPicker,
		)
	}
}
