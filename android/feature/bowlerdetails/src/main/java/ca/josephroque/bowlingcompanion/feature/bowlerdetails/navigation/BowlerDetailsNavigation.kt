package ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.BowlerDetailsRoute
import java.util.UUID

const val BOWLER_ID = "bowlerid"
const val bowlerDetailsNavigationRoute = "bowler/{$BOWLER_ID}"

fun NavController.navigateToBowlerDetails(bowlerId: UUID) {
	val encoded = Uri.encode(bowlerId.toString())
	this.navigate("bowler/$encoded")
}

fun NavGraphBuilder.bowlerDetailsScreen(
	onBackPressed: () -> Unit,
	onEditLeague: (UUID) -> Unit,
	onAddLeague: (UUID) -> Unit,
	onShowLeagueDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowPreferredGearPicker: (Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onEditStatisticsWidgets: (String) -> Unit,
	onShowStatistics: (UUID) -> Unit,
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
			onEditStatisticsWidgets = onEditStatisticsWidgets,
			onShowStatistics = onShowStatistics,
		)
	}
}