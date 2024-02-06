package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesSettingsRoute
import java.util.UUID

fun NavController.navigateToGamesSettingsForResult(
	series: List<UUID>,
	currentGameId: UUID,
	navResultCallback: NavResultCallback<Pair<List<UUID>, UUID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.GameSettings.createRoute(series, currentGameId),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.gamesSettingsScreen(
	onDismissWithResult: (Pair<List<UUID>, UUID>) -> Unit,
) {
	composable(
		route = Route.GameSettings.route,
		arguments = listOf(
			navArgument(Route.GameSettings.ARG_SERIES) { type = NavType.StringType },
			navArgument(Route.GameSettings.ARG_CURRENT_GAME) { type = NavType.StringType },
		),
	) {
		GamesSettingsRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}