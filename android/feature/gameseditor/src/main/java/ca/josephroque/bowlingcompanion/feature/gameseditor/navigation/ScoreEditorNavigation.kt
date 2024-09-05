package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.scoreeditor.ScoreEditorRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToScoreEditorForResult(
	score: Int,
	scoringMethod: GameScoringMethod,
	navResultCallback: NavResultCallback<Pair<GameScoringMethod, Int>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.ScoreEditor.createRoute(scoringMethod, score),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.scoreEditorScreen(onDismissWithResult: (Pair<GameScoringMethod, Int>) -> Unit) {
	bottomSheet(
		route = Route.ScoreEditor.route,
		arguments = listOf(
			navArgument(Route.ScoreEditor.ARG_SCORING_METHOD) {
				type = NavType.EnumType(GameScoringMethod::class.java)
			},
			navArgument(Route.ScoreEditor.ARG_SCORE) { type = NavType.IntType },
		),
	) {
		ScoreEditorRoute(onDismissWithResult = onDismissWithResult)
	}
}
