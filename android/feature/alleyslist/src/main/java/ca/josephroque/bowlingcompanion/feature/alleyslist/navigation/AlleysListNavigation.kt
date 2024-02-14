package ca.josephroque.bowlingcompanion.feature.alleyslist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.alleyslist.AlleysListRoute
import java.util.UUID

fun NavController.navigateToAlleysList(navOptions: NavOptions? = null) {
	this.navigate(Route.AlleysList.route, navOptions)
}

fun NavGraphBuilder.alleysListScreen(
	onBackPressed: () -> Unit,
	onEditAlley: (UUID) -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onAddAlley: () -> Unit,
) {
	composable(
		route = Route.AlleysList.route,
	) {
		AlleysListRoute(
			onBackPressed = onBackPressed,
			onEditAlley = onEditAlley,
			onAddAlley = onAddAlley,
			onShowAlleyDetails = onShowAlleyDetails,
		)
	}
}
