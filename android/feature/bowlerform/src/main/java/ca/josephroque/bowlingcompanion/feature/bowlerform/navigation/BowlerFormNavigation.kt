package ca.josephroque.bowlingcompanion.feature.bowlerform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.bowlerform.BowlerFormRoute
import java.util.UUID

fun NavController.navigateToBowlerForm(bowlerId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditBowler.createRoute(bowlerId), navOptions)
}

fun NavController.navigateToNewBowlerForm(kind: BowlerKind, navOptions: NavOptions? = null) {
	this.navigate(Route.AddBowler.createRoute(kind.name), navOptions)
}

fun NavGraphBuilder.bowlerFormScreen(
	onBackPressed: () -> Unit
) {
	composable(
		route = Route.EditBowler.route,
		arguments = listOf(
			navArgument(Route.EditBowler.ARG_BOWLER) { type = NavType.StringType },
		),
	) {
		BowlerFormRoute(
			onDismiss = onBackPressed,
		)
	}
	composable(
		route = Route.AddBowler.route,
		arguments = listOf(
			navArgument(Route.AddBowler.ARG_KIND) { type = NavType.EnumType(BowlerKind::class.java) },
		),
	) {
		BowlerFormRoute(
			onDismiss = onBackPressed,
		)
	}
}