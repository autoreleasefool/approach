package ca.josephroque.bowlingcompanion.feature.statistics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.statistics.StatisticsRoute

const val statisticsNavigationRoute = "statistics"

fun NavController.navigateToStatistics(navOptions: NavOptions? = null) {
	this.navigate(statisticsNavigationRoute, navOptions)
}

fun NavGraphBuilder.statisticsScreen() {
	composable(
		route = statisticsNavigationRoute,
	) {
		StatisticsRoute()
	}
}