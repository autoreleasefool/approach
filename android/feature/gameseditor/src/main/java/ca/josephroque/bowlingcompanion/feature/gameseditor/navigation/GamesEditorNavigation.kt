package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorRoute
import java.util.UUID

fun NavController.navigateToGamesEditor(seriesId: UUID, initialGameId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditGame.createRoute(seriesId, initialGameId), navOptions)
}

fun NavGraphBuilder.gamesEditorScreen(
	onBackPressed: () -> Unit,
	onEditMatchPlay: (UUID) -> Unit,
	onEditGear: (Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onEditAlley: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onEditLanes: (UUID, Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onShowGamesSettings: (UUID, UUID, NavResultCallback<UUID>) -> Unit,
	onEditRolledBall: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
) {
	composable(
		route = Route.EditGame.route,
		arguments = listOf(
			navArgument(Route.EditGame.ARG_SERIES) { type = NavType.StringType },
			navArgument(Route.EditGame.ARG_GAME) { type = NavType.StringType },
		),
	) {
		GamesEditorRoute(
			onBackPressed = onBackPressed,
			onEditMatchPlay = onEditMatchPlay,
			onEditGear = onEditGear,
			onEditAlley = onEditAlley,
			onEditLanes = onEditLanes,
			onShowGamesSettings = onShowGamesSettings,
			onEditRolledBall = onEditRolledBall,
			onShowStatistics = onShowStatistics,
		)
	}
}