package ca.josephroque.bowlingcompanion.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateToOverviewGraph(navOptions: NavOptions? = null) {
	this.navigate(TopLevelDestination.APP_OVERVIEW.graphName, navOptions)
}

fun NavController.navigateToAccessoriesGraph(navOptions: NavOptions? = null) {
	this.navigate(TopLevelDestination.ACCESSORIES_OVERVIEW.graphName, navOptions)
}

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
	this.navigate(TopLevelDestination.SETTINGS_OVERVIEW.graphName, navOptions)
}

fun NavController.navigateToStatisticsGraph(navOptions: NavOptions? = null) {
	this.navigate(TopLevelDestination.STATISTICS_OVERVIEW.graphName, navOptions)
}
