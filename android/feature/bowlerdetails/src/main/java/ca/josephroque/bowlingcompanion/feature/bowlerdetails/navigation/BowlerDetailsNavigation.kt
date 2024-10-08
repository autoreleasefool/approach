package ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.BowlerDetailsRoute

fun NavController.navigateToBowlerDetails(bowlerId: BowlerID, navOptions: NavOptions? = null) {
	this.navigate(Route.BowlerDetails.createRoute(bowlerId), navOptions)
}

fun NavGraphBuilder.bowlerDetailsScreen(
	onBackPressed: () -> Unit,
	onEditLeague: (LeagueID) -> Unit,
	onAddLeague: (BowlerID) -> Unit,
	onShowLeagueDetails: (LeagueID) -> Unit,
	onShowEventDetails: (LeagueID) -> Unit,
	onShowGearDetails: (GearID) -> Unit,
	onShowPreferredGearPicker: (Set<GearID>, ResourcePickerResultKey) -> Unit,
	onEditStatisticsWidgets: (String, BowlerID) -> Unit,
	onShowWidgetStatistics: (TrackableFilter) -> Unit,
	onShowWidgetError: () -> Unit,
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
			onShowWidgetStatistics = onShowWidgetStatistics,
			onShowWidgetNotEnoughDataError = onShowWidgetError,
			onShowWidgetUnavailableError = onShowWidgetError,
		)
	}
}
