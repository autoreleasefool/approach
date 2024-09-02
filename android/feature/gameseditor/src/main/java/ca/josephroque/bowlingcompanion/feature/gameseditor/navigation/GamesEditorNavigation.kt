package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorArguments
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorRoute

fun NavController.navigateToGamesEditor(
	seriesIds: List<SeriesID>,
	initialGameId: GameID,
	navOptions: NavOptions? = null,
) {
	this.navigate(Route.EditGame.createRoute(seriesIds, initialGameId), navOptions)
}

fun NavController.navigateToGamesEditor(
	teamSeriesId: TeamSeriesID,
	initialGameId: GameID,
	navOptions: NavOptions? = null,
) {
	this.navigate(Route.EditTeamSeries.createRoute(teamSeriesId, initialGameId), navOptions)
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

	composable(
		route = Route.EditTeamSeries.route,
		arguments = listOf(
			navArgument(Route.EditTeamSeries.ARG_TEAM_SERIES) { type = NavType.StringType },
			navArgument(Route.EditTeamSeries.ARG_GAME) { type = NavType.StringType },
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
