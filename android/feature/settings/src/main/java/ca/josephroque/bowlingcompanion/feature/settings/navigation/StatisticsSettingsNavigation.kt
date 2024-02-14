package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.statistics.StatisticsSettingsRoute

fun NavController.navigateToStatisticsSettings(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsSettings.route, navOptions)
}

fun NavGraphBuilder.statisticsSettingsScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.StatisticsSettings.route,
	) {
		StatisticsSettingsRoute(onBackPressed = onBackPressed)
	}
}
