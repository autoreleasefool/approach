package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetailsChart
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.navigateToStatisticsSourcePicker
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewScreen

fun NavGraphBuilder.statisticsGraph(navController: NavController) {
	statisticsOverviewScreen(
		onShowSourcePicker = navController::navigateToStatisticsSourcePicker,
	)
	statisticsDetailsScreen(
		onBackPressed = navController::popBackStack,
		onShowStatisticChart = navController::navigateToStatisticsDetailsChart,
	)
}
