package ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.BowlerDetailsRoute
import java.util.UUID

const val BOWLER_ID = "bowlerid"
const val bowlerDetailsNavigationRoute = "bowler/{$BOWLER_ID}"

fun NavController.navigateToBowlerDetails(bowlerId: BowlerID) {
	val encoded = Uri.encode(bowlerId.value.toString())
	this.navigate("bowler/$encoded") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.bowlerDetailsScreen(
	onBackPressed: () -> Unit,
	onEditLeague: (UUID) -> Unit,
	onAddLeague: (BowlerID) -> Unit,
	onShowLeagueDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowPreferredGearPicker: () -> Unit,
) {
	composable(
		route = bowlerDetailsNavigationRoute,
		arguments = listOf(
			navArgument(BOWLER_ID) { type = NavType.StringType },
		),
	) {
		BowlerDetailsRoute(
			onEditLeague = onEditLeague,
			onAddLeague = onAddLeague,
			onBackPressed = onBackPressed,
			onShowLeagueDetails = onShowLeagueDetails,
			onShowGearDetails = onShowGearDetails,
			onShowPreferredGearPicker = onShowPreferredGearPicker,
		)
	}
}