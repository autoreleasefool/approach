package ca.josephroque.bowlingcompanion.feature.opponentslist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.opponentslist.OpponentsListRoute

fun NavController.navigateToOpponentsList(navOptions: NavOptions? = null) {
	this.navigate(Route.OpponentsList.route, navOptions)
}

fun NavGraphBuilder.opponentsListScreen(
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
	onOpenOpponentDetails: (BowlerID) -> Unit,
	onEditOpponent: (BowlerID) -> Unit,
) {
	composable(
		route = Route.OpponentsList.route,
	) {
		OpponentsListRoute(
			onBackPressed = onBackPressed,
			onAddOpponent = onAddOpponent,
			onOpenOpponentDetails = onOpenOpponentDetails,
			onEditOpponent = onEditOpponent,
		)
	}
}
