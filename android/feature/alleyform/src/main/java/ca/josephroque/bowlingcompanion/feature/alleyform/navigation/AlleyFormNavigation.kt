package ca.josephroque.bowlingcompanion.feature.alleyform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.alleyform.AlleyFormRoute
import java.util.UUID

const val ALLEY_ID = "alleyid"
const val editAlleyNavigationRoute = "edit_alley/{$ALLEY_ID}"
const val addAlleyNavigationRoute = "add_alley"

fun NavController.navigateToNewAlleyForm() {
	this.navigate(addAlleyNavigationRoute) {
		launchSingleTop = true
	}
}

fun NavController.navigateToAlleyForm(alleyId: UUID) {
	val encoded = Uri.encode(alleyId.toString())
	this.navigate("edit_alley/$encoded") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.alleyFormScreen(
	onBackPressed: () -> Unit,
	onManageLanes: (List<UUID>, NavResultCallback<List<UUID>>) -> Unit,
) {
	composable(
		route = editAlleyNavigationRoute,
		arguments = listOf(
			navArgument(ALLEY_ID) { type = NavType.StringType },
		),
	) {
		AlleyFormRoute(
			onDismiss = onBackPressed,
			onManageLanes = onManageLanes,
		)
	}

	composable(
		route = addAlleyNavigationRoute,
	) {
		AlleyFormRoute(
			onDismiss = onBackPressed,
			onManageLanes = onManageLanes,
		)
	}
}