package ca.josephroque.bowlingcompanion.feature.laneform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.laneform.LaneFormRoute
import java.util.UUID

const val ALLEY_ID = "alleyid"
const val editLanesNavigationRoute = "edit_alley/{$ALLEY_ID}/lanes"

fun NavController.navigateToLaneForm(alleyId: UUID) {
	val encoded = Uri.encode(alleyId.toString())
	this.navigate("edit_alley/$encoded/lanes") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.laneFormScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = editLanesNavigationRoute,
		arguments = listOf(
			navArgument(ALLEY_ID) { type = NavType.StringType },
		),
	) {
		LaneFormRoute(
			onBackPressed = onBackPressed,
			onDismiss = onBackPressed,
		)
	}
}