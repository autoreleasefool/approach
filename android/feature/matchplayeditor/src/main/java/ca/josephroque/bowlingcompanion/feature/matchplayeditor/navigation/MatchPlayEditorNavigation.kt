package ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.MatchPlayEditorRoute
import java.util.UUID

const val GAME_ID = "gameid"

const val editMatchPlayNavigationRoute = "edit_match_play/{$GAME_ID}"

fun NavController.navigateToMatchPlayEditor(gameId: UUID) {
	val encoded = UUID.fromString(gameId.toString())
	this.navigate("edit_match_play/$encoded")
}

fun NavGraphBuilder.matchPlayEditorScreen(
	onBackPressed: () -> Unit,
	onEditOpponent: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	composable(
		route = editMatchPlayNavigationRoute,
		arguments = listOf(
			navArgument(GAME_ID) { type = NavType.StringType }
		),
	) {
		MatchPlayEditorRoute(
			onDismiss = onBackPressed,
			onEditOpponent = onEditOpponent,
		)
	}
}