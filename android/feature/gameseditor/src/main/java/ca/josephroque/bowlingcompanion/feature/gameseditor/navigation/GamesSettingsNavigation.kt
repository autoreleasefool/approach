package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.settings.GamesSettingsRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToGamesSettingsForResult(
	teamSeriesId: TeamSeriesID?,
	seriesIds: List<SeriesID>,
	currentGameId: GameID,
	navResultCallback: NavResultCallback<Pair<List<SeriesID>, GameID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.GameSettings.createRoute(teamSeriesId, seriesIds, currentGameId),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.gamesSettingsScreen(
	onDismissWithResult: (Pair<List<SeriesID>, GameID>) -> Unit,
) {
	bottomSheet(
		route = Route.GameSettings.route,
		arguments = listOf(
			navArgument(Route.GameSettings.ARG_TEAM_SERIES) {
				type = NavType.StringType
				nullable = true
			},
			navArgument(Route.GameSettings.ARG_SERIES) { type = NavType.StringType },
			navArgument(Route.GameSettings.ARG_CURRENT_GAME) { type = NavType.StringType },
		),
	) {
		GamesSettingsRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}
