package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.navigateToStatisticsSourcePicker
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewScreen

fun NavGraphBuilder.statisticsGraph(
	navController: NavController,
) {
	statisticsOverviewScreen(
		onShowSourcePicker = navController::navigateToStatisticsSourcePicker,
	)
	statisticsDetailsScreen(
		onBackPressed = navController::popBackStack,
	)
}