package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.settings.statistics.StatisticsSettingsRoute

const val statisticsSettingsNavigationRoute = "settings/statistics"

fun NavController.navigateToStatisticsSettings(navOptions: NavOptions? = null) {
	this.navigate(statisticsSettingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.statisticsSettingsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = statisticsSettingsNavigationRoute,
	) {
		StatisticsSettingsRoute(onBackPressed = onBackPressed)
	}
}