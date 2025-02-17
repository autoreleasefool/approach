package ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.MatchPlayEditorRoute

fun NavController.navigateToMatchPlayEditor(gameId: GameID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditMatchPlay.createRoute(gameId), navOptions)
}

fun NavGraphBuilder.matchPlayEditorScreen(
	onBackPressed: () -> Unit,
	onEditOpponent: (BowlerID?, ResourcePickerResultKey) -> Unit,
) {
	bottomSheet(
		route = Route.EditMatchPlay.route,
		arguments = listOf(
			navArgument(Route.EditMatchPlay.ARG_GAME) { type = NavType.StringType },
		),
	) {
		MatchPlayEditorRoute(
			onDismiss = onBackPressed,
			onEditOpponent = onEditOpponent,
		)
	}
}
