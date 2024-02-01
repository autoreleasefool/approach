package ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ResourcePickerRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToResourcePickerForResult(
	selectedIds: Set<UUID>,
	resourceType: ResourcePickerType,
	filter: String? = null,
	titleOverride: String? = null,
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<UUID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.ResourcePicker.createRoute(
			resourceType.toString(),
			filter,
			selectedIds,
			limit,
			Uri.encode(titleOverride),
		),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.resourcePickerSheet(
	onDismissWithResult: (Set<UUID>) -> Unit,
) {
	bottomSheet(
		route = Route.ResourcePicker.route,
		arguments = listOf(
			navArgument(Route.ResourcePicker.RESOURCE_TYPE) { type = NavType.EnumType(ResourcePickerType::class.java) },
			navArgument(Route.ResourcePicker.RESOURCE_FILTER) {
				type = NavType.StringType
				nullable = true
			},
			navArgument(Route.ResourcePicker.SELECTED_IDS) { type = NavType.StringType },
			navArgument(Route.ResourcePicker.SELECTION_LIMIT) { type = NavType.IntType },
			navArgument(Route.ResourcePicker.TITLE_OVERRIDE) {
				type = NavType.StringType
				nullable = true
		  },
		),
	) {
		ResourcePickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}