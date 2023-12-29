package ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ResourcePickerRoute
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerType
import java.util.UUID

const val RESOURCE_TYPE = "resource_type"
const val RESOURCE_FILTER = "resource_filter"
const val SELECTED_IDS = "selected_ids"
const val SELECTION_LIMIT = "limit"
const val TITLE_OVERRIDE = "title_override"
const val resourcePickerNavigationRoute = "resource_picker?" +
		"type={$RESOURCE_TYPE}&" +
		"filter={$RESOURCE_FILTER}&" +
		"selected={$SELECTED_IDS}&" +
		"limit={$SELECTION_LIMIT}&" +
		"title={$TITLE_OVERRIDE}"

fun NavController.navigateToResourcePickerForResult(
	selectedIds: Set<UUID>,
	resourceType: ResourcePickerType,
	resourceFilter: String? = null,
	titleOverride: String? = null,
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<UUID>>,
) {
	val ids = selectedIds.joinToString(separator = ",") { it.toString() }
	val encodedFilter = Uri.encode(resourceFilter ?: "nan")
	val encodedIds = Uri.encode(ids.ifEmpty { "nan" })
	val encodedLimit = Uri.encode(limit.toString())
	this.navigateForResult(
		resourcePickerNavigationRoute
			.replace("{$RESOURCE_TYPE}", resourceType.toString())
			.replace("{$RESOURCE_FILTER}", encodedFilter)
			.replace("{$SELECTED_IDS}", encodedIds)
			.replace("{$SELECTION_LIMIT}", encodedLimit)
			.replace("{$TITLE_OVERRIDE}", titleOverride ?: "nan"),
		navResultCallback,
	)
}

fun NavGraphBuilder.resourcePickerScreen(
	onDismissWithResult: (Set<UUID>) -> Unit,
) {
	composable(
		route = resourcePickerNavigationRoute,
		arguments = listOf(
			navArgument(RESOURCE_TYPE) { type = NavType.StringType },
			navArgument(RESOURCE_FILTER) { type = NavType.StringType },
			navArgument(SELECTED_IDS) { type = NavType.StringType },
			navArgument(SELECTION_LIMIT) { type = NavType.IntType },
			navArgument(TITLE_OVERRIDE) { type = NavType.StringType },
		),
	) {
		ResourcePickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}