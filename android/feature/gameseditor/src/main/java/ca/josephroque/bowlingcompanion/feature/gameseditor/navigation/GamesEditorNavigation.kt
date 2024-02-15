package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorArguments
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorRoute
import java.util.UUID

fun NavController.navigateToGamesEditor(
	seriesIds: List<UUID>,
	initialGameId: UUID,
	navOptions: NavOptions? = null,
) {
	this.navigate(Route.EditGame.createRoute(seriesIds, initialGameId), navOptions)
}

fun NavGraphBuilder.gamesEditorScreen(
	onBackPressed: () -> Unit,
	onEditMatchPlay: (GamesEditorArguments.EditMatchPlay) -> Unit,
	onEditGear: (GamesEditorArguments.EditGear) -> Unit,
	onEditRolledBall: (GamesEditorArguments.EditRolledBall) -> Unit,
	onEditAlley: (GamesEditorArguments.EditAlley) -> Unit,
	onEditLanes: (GamesEditorArguments.EditLanes) -> Unit,
	onShowGamesSettings: (GamesEditorArguments.ShowGamesSettings) -> Unit,
	onShowStatistics: (GamesEditorArguments.ShowStatistics) -> Unit,
	onShowBowlerScores: (GamesEditorArguments.ShowBowlerScores) -> Unit,
	onEditScore: (GamesEditorArguments.EditScore) -> Unit,
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
			onShowBowlerScores = onShowBowlerScores,
			onEditScore = onEditScore,
		)
	}
}
