package ca.josephroque.bowlingcompanion.feature.gearlist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.gearlist.GearListRoute
import java.util.UUID

const val gearListNavigationRoute = "gear"

fun NavController.navigateToGearList(navOptions: NavOptions? = null) {
		this.navigate(gearListNavigationRoute, navOptions)
}

fun NavGraphBuilder.gearListScreen(
	onBackPressed: () -> Unit,
	onEditGear: (UUID) -> Unit,
	onAddGear: () -> Unit,
) {
	composable(
		route = gearListNavigationRoute,
	) {
		GearListRoute(
			onBackPressed = onBackPressed,
			onEditGear = onEditGear,
			onAddGear = onAddGear,
		)
	}
}