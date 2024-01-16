package ca.josephroque.bowlingcompanion.feature.gearlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gearlist.GearListRoute
import java.util.UUID

fun NavController.navigateToGearList(navOptions: NavOptions? = null) {
		this.navigate(Route.GearList.route, navOptions)
}

fun NavGraphBuilder.gearListScreen(
	onBackPressed: () -> Unit,
	onEditGear: (UUID) -> Unit,
	onAddGear: () -> Unit,
	onShowGearDetails: (UUID) -> Unit,
) {
	composable(
		route = Route.GearList.route,
	) {
		GearListRoute(
			onBackPressed = onBackPressed,
			onEditGear = onEditGear,
			onAddGear = onAddGear,
			onShowGearDetails = onShowGearDetails,
		)
	}
}