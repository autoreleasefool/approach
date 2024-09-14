package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.GamesSettingsResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.settings.GamesSettingsRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToGamesSettingsForResult(
	teamSeriesId: TeamSeriesID?,
	seriesIds: List<SeriesID>,
	currentGameId: GameID,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.GameSettings.createRoute(teamSeriesId, seriesIds, currentGameId),
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.gamesSettingsScreen(navController: NavController, onDismiss: () -> Unit) {
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
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<GamesSettingsResultViewModel>()
		} else {
			hiltViewModel<GamesSettingsResultViewModel>(parentEntry)
		}

		GamesSettingsRoute(
			onDismissWithResult = { result ->
				resultViewModel.setResult(result)
				onDismiss()
			},
		)
	}
}
