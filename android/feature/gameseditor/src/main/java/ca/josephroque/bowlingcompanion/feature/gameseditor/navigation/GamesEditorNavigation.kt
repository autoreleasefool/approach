package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
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
	onEditMatchPlay: (GameID) -> Unit,
	onEditGear: (Set<GearID>, NavResultCallback<Set<GearID>>) -> Unit,
	onEditRolledBall: (GearID?, NavResultCallback<Set<GearID>>) -> Unit,
	onEditAlley: (AlleyID?, NavResultCallback<Set<AlleyID>>) -> Unit,
	onEditLanes: (AlleyID, Set<LaneID>, NavResultCallback<Set<LaneID>>) -> Unit,
	onShowGamesSettings:
	(TeamSeriesID?, List<SeriesID>, GameID, NavResultCallback<Pair<List<SeriesID>, GameID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
	onShowBowlerScores: (List<SeriesID>, gameIndex: Int) -> Unit,
	onEditScore:
	(score: Int, GameScoringMethod, NavResultCallback<Pair<GameScoringMethod, Int>>) -> Unit,
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
