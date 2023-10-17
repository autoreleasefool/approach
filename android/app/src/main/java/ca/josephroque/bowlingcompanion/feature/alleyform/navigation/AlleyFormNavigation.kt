package ca.josephroque.bowlingcompanion.feature.alleyform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.alleyform.AlleyFormRoute

const val ALLEY_ID = "alleyid"
const val editAlleyNavigationRoute = "edit_alley/{$ALLEY_ID}"
const val addAlleyNavigationRoute = "add_alley"

fun NavController.navigateToNewAlleyForm() {
	this.navigate(addAlleyNavigationRoute) {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.alleyFormScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = editAlleyNavigationRoute,
		arguments = listOf(
			navArgument(ALLEY_ID) { type = NavType.StringType },
		),
	) {
		AlleyFormRoute(
			onBackPressed = onBackPressed,
			onDismiss = onBackPressed,
		)
	}

	composable(
		route = addAlleyNavigationRoute,
	) {
		AlleyFormRoute(
			onBackPressed = onBackPressed,
			onDismiss = onBackPressed,
		)
	}
}