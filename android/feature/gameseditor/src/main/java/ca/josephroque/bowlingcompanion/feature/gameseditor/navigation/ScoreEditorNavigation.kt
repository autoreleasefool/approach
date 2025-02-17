package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.ScoreEditorResultViewModel
import ca.josephroque.bowlingcompanion.feature.gameseditor.scoreeditor.ScoreEditorRoute

fun NavController.navigateToScoreEditorForResult(
	score: Int,
	scoringMethod: GameScoringMethod,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.ScoreEditor.createRoute(scoringMethod, score),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.scoreEditorScreen(navController: NavController, onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.ScoreEditor.route,
		arguments = listOf(
			navArgument(Route.ScoreEditor.ARG_SCORING_METHOD) {
				type = NavType.EnumType(GameScoringMethod::class.java)
			},
			navArgument(Route.ScoreEditor.ARG_SCORE) { type = NavType.IntType },
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<ScoreEditorResultViewModel>()
		} else {
			hiltViewModel<ScoreEditorResultViewModel>(parentEntry)
		}

		ScoreEditorRoute(onDismissWithResult = { score ->
			resultViewModel.setResult(score)
			onDismiss()
		})
	}
}
