package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.scores.ScoresListRoute

fun NavController.navigateToScoresList(series: List<SeriesID>, gameIndex: Int, navOptions: NavOptions? = null) {
	this.navigate(
		route = Route.ScoresList.createRoute(series, gameIndex),
		navOptions = navOptions,
	)
}

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
