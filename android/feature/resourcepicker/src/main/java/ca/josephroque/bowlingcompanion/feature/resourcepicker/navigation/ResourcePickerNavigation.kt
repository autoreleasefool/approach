package ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.BowlerPickerRoute
import java.util.UUID

const val SELECTED_IDS = "selected_ids"
const val SELECTION_LIMIT = "limit"
const val bowlerPickerNavigationRoute = "pick_bowlers/{$SELECTED_IDS}/{$SELECTION_LIMIT}"

fun NavController.navigateToBowlerPickerForResult(
	selectedIds: Set<UUID>,
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<UUID>>,
) {
	val ids = selectedIds.joinToString(separator = ",") { it.toString() }
	val encodedIds = Uri.encode(ids.ifEmpty { "nan" })
	val encodedLimit = Uri.encode(limit.toString())
	this.navigateForResult("pick_bowlers/$encodedIds/$encodedLimit", navResultCallback)
}

fun NavGraphBuilder.bowlerPickerScreen(
	onDismissWithResult: (Set<UUID>) -> Unit,
) {
	composable(
		route = bowlerPickerNavigationRoute,
		arguments = listOf(
			navArgument(SELECTED_IDS) { type = NavType.StringType },
			navArgument(SELECTION_LIMIT) { type = NavType.IntType },
		),
	) {
		BowlerPickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}