package ca.josephroque.bowlingcompanion.feature.gearform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.gearform.GearFormRoute
import java.util.UUID

const val GEAR_ID = "gearid"

const val editGearNavigationRoute = "edit_gear/{$GEAR_ID}"
const val addGearNavigationRoute = "add_gear"

fun NavController.navigateToGearForm(gearId: UUID) {
	val encoded = UUID.fromString(gearId.toString())
	this.navigate("edit_gear/$encoded")
}

fun NavController.navigateToNewGearForm() {
	this.navigate("add_gear")
}

fun NavGraphBuilder.gearFormScreen(
	onBackPressed: () -> Unit,
	onEditAvatar: (Avatar, NavResultCallback<Avatar>) -> Unit,
	onEditOwner: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
) {
	composable(
		route = editGearNavigationRoute,
		arguments = listOf(
			navArgument(GEAR_ID) { type = NavType.StringType }
		),
	) {
		GearFormRoute(
			onDismiss = onBackPressed,
			onEditAvatar = onEditAvatar,
			onEditOwner = onEditOwner,
		)
	}
	composable(
		route = addGearNavigationRoute,
	) {
		GearFormRoute(
			onDismiss = onBackPressed,
			onEditAvatar = onEditAvatar,
			onEditOwner = onEditOwner,
		)
	}
}