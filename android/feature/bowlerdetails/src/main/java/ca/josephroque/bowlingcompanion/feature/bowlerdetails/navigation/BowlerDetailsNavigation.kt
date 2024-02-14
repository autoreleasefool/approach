package ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.BowlerDetailsRoute
import java.util.UUID

fun NavController.navigateToBowlerDetails(bowlerId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.BowlerDetails.createRoute(bowlerId), navOptions)
}

fun NavGraphBuilder.bowlerDetailsScreen(
	onBackPressed: () -> Unit,
	onEditLeague: (UUID) -> Unit,
	onAddLeague: (UUID) -> Unit,
	onShowLeagueDetails: (UUID) -> Unit,
	onShowEventDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowPreferredGearPicker: (Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onEditStatisticsWidgets: (String, UUID) -> Unit,
	onShowStatistics: (UUID) -> Unit,
) {
	composable(
		route = Route.BowlerDetails.route,
		arguments = listOf(
			navArgument(Route.BowlerDetails.ARG_BOWLER) { type = NavType.StringType },
		),
	) {
		BowlerDetailsRoute(
			onEditLeague = onEditLeague,
			onAddLeague = onAddLeague,
			onBackPressed = onBackPressed,
			onShowLeagueDetails = onShowLeagueDetails,
			onShowEventDetails = onShowEventDetails,
			onShowGearDetails = onShowGearDetails,
			onShowPreferredGearPicker = onShowPreferredGearPicker,
			onEditStatisticsWidgets = onEditStatisticsWidgets,
			onShowStatistics = onShowStatistics,
		)
	}
}
