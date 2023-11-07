package ca.josephroque.bowlingcompanion.feature.bowlerform.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.BowlerFormRoute
import java.util.UUID

const val BOWLER_ID = "bowlerId"
const val BOWLER_KIND = "bowlerKind"
const val editBowlerNavigationRoute = "edit_bowler/{$BOWLER_ID}"
const val addBowlerNavigationRoute = "add_bowler/{$BOWLER_KIND}"

fun NavController.navigateToBowlerForm(bowlerId: UUID) {
	val encoded = Uri.encode(bowlerId.toString())
	this.navigate("edit_bowler/$encoded") {
		launchSingleTop = true
	}
}

fun NavController.navigateToNewBowlerForm(kind: BowlerKind) {
	this.navigate("add_bowler/${kind.name}") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.bowlerFormScreen(
	onBackPressed: () -> Unit
) {
	composable(
		route = editBowlerNavigationRoute,
		arguments = listOf(
			navArgument(BOWLER_ID) { type = NavType.StringType },
		),
	) {
		BowlerFormRoute(
			onDismiss = onBackPressed,
		)
	}
	composable(
		route = addBowlerNavigationRoute,
		arguments = listOf(
			navArgument(BOWLER_KIND) { type = NavType.StringType },
		),
	) {
		BowlerFormRoute(
			onDismiss = onBackPressed,
		)
	}
}