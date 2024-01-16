package ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.MatchPlayEditorRoute
import java.util.UUID

fun NavController.navigateToMatchPlayEditor(gameId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditMatchPlay.createRoute(gameId), navOptions)
}

fun NavGraphBuilder.matchPlayEditorScreen(
	onBackPressed: () -> Unit,
	onEditOpponent: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	composable(
		route = Route.EditMatchPlay.route,
		arguments = listOf(
			navArgument(Route.EditMatchPlay.ARG_GAME) { type = NavType.StringType }
		),
	) {
		MatchPlayEditorRoute(
			onDismiss = onBackPressed,
			onEditOpponent = onEditOpponent,
		)
	}
}