package ca.josephroque.bowlingcompanion.feature.gameseditor.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.GamesSettingsRoute
import java.util.UUID

const val SETTINGS_SERIES_ID = "seriesid"
const val CURRENT_GAME_ID = "gameid"
const val gamesSettingsNavigationRoute = "games_settings/{$SETTINGS_SERIES_ID}/{$CURRENT_GAME_ID}"

fun NavController.navigateToGamesSettingsForResult(
	seriesId: UUID,
	currentGameId: UUID,
	navResultCallback: NavResultCallback<UUID>,
) {
	val encodedSeriesId = Uri.encode(seriesId.toString())
	val encodedCurrentGameId = Uri.encode(currentGameId.toString())
	this.navigateForResult("games_settings/$encodedSeriesId/$encodedCurrentGameId", navResultCallback)
}

fun NavGraphBuilder.gamesSettingsScreen(
	onDismissWithResult: (UUID) -> Unit,
) {
	composable(
		route = gamesSettingsNavigationRoute,
		arguments = listOf(
			navArgument(SETTINGS_SERIES_ID) { type = NavType.StringType },
			navArgument(CURRENT_GAME_ID) { type = NavType.StringType },
		),
	) {
		GamesSettingsRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}