package ca.josephroque.bowlingcompanion.feature.sharing.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.sharing.SharingRoute

fun NavController.navigateToSharingSeries(series: SeriesID, navOptions: NavOptions? = null) {
	this.navigate(Route.SharingSeries.createRoute(series), navOptions)
}

fun NavGraphBuilder.seriesSharingScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.SharingSeries.route,
		arguments = listOf(
			navArgument(Route.SharingSeries.ARG_SERIES) { type = NavType.StringType },
		),
	) {
		SharingRoute(
			onDismiss = onBackPressed,
		)
	}
}
