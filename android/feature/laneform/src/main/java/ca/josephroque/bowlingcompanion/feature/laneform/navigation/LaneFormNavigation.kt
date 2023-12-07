package ca.josephroque.bowlingcompanion.feature.laneform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.laneform.LaneFormRoute
import java.util.UUID

const val LANE_IDS = "laneids"
const val editLanesNavigationRoute = "edit_lanes/{$LANE_IDS}"

fun NavController.navigateToLaneFormForResult(
	existingLanes: List<UUID>,
	navResultCallback: NavResultCallback<List<UUID>>,
) {
	val encoded = Uri.encode(existingLanes.joinToString(",") { it.toString() }).ifEmpty { "nan" }
	this.navigateForResult("edit_lanes/$encoded", navResultCallback)
}

fun NavGraphBuilder.laneFormScreen(
	onDismissWithResult: (List<UUID>) -> Unit,
) {
	composable(
		route = editLanesNavigationRoute,
		arguments = listOf(
			navArgument(LANE_IDS) { type = NavType.StringType },
		),
	) {
		LaneFormRoute(
			onDismissWithResult = onDismissWithResult
		)
	}
}