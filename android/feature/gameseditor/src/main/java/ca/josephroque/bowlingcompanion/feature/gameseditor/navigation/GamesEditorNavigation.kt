package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesEditorRoute
import java.util.UUID

const val SERIES_ID = "seriesid"
const val INITIAL_GAME_ID = "gameid"
const val gamesEditorNavigationRoute = "edit_games/{$SERIES_ID}/{$INITIAL_GAME_ID}"

fun NavController.navigateToGamesEditor(seriesId: UUID, initialGameId: UUID) {
	val seriesIdEncoded = Uri.encode(seriesId.toString())
	val gameIdEncoded = Uri.encode(initialGameId.toString())
	this.navigate("edit_games/$seriesId/$gameIdEncoded") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.gamesEditorScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = gamesEditorNavigationRoute,
		arguments = listOf(
			navArgument(SERIES_ID) { type = NavType.StringType },
			navArgument(INITIAL_GAME_ID) { type = NavType.StringType },
		),
	) {
		GamesEditorRoute(
			onBackPressed = onBackPressed,
		)
	}
}