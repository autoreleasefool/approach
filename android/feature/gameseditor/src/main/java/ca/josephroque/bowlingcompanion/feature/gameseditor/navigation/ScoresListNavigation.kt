package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.scores.ScoresListRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToScoresList(
	gameIndex: Int,
	series: List<SeriesID>,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.ScoresList.createRoute(series, gameIndex),
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.scoresListScreen(onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.ScoresList.route,
		arguments = listOf(
			navArgument(Route.ScoresList.ARG_SERIES) { type = NavType.StringType },
			navArgument(Route.ScoresList.ARG_GAME_INDEX) { type = NavType.IntType },
		),
	) {
		ScoresListRoute(
			onDismiss = onDismiss,
		)
	}
}
