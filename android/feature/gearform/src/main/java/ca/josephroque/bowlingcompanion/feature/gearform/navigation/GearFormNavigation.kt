package ca.josephroque.bowlingcompanion.feature.gearform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gearform.GearFormRoute

fun NavController.navigateToGearForm(gearId: GearID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditGear.createRoute(gearId), navOptions)
}

fun NavController.navigateToNewGearForm(navOptions: NavOptions? = null) {
	this.navigate(Route.AddGear.route, navOptions)
}

fun NavGraphBuilder.gearFormScreen(
	onBackPressed: () -> Unit,
	onEditAvatar: (Avatar) -> Unit,
	onEditOwner: (BowlerID?, ResourcePickerResultKey) -> Unit,
) {
	composable(
		route = Route.EditGear.route,
		arguments = listOf(
			navArgument(Route.EditGear.ARG_GEAR) { type = NavType.StringType },
		),
	) {
		GearFormRoute(
			onDismiss = onBackPressed,
			onEditAvatar = onEditAvatar,
			onEditOwner = onEditOwner,
		)
	}
	composable(
		route = Route.AddGear.route,
	) {
		GearFormRoute(
			onDismiss = onBackPressed,
			onEditAvatar = onEditAvatar,
			onEditOwner = onEditOwner,
		)
	}
}
