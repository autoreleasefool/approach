package ca.josephroque.bowlingcompanion.feature.opponentslist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.opponentslist.OpponentsListRoute
import java.util.UUID

const val opponentsListNavigationRoute = "opponents"

fun NavController.navigateToOpponentsList(navOptions: NavOptions? = null) {
	this.navigate(opponentsListNavigationRoute, navOptions)
}

fun NavGraphBuilder.opponentsListScreen(
	onBackPressed: () -> Unit,
	onAddOpponent: () -> Unit,
	onOpenOpponentDetails: (UUID) -> Unit,
	onEditOpponent: (UUID) -> Unit,
) {
	composable(
		route = opponentsListNavigationRoute,
	) {
		OpponentsListRoute(
			onBackPressed = onBackPressed,
			onAddOpponent = onAddOpponent,
			onOpenOpponentDetails = onOpenOpponentDetails,
			onEditOpponent = onEditOpponent,
		)
	}
}