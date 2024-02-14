package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.analytics.AnalyticsSettingsRoute

fun NavController.navigateToAnalyticsSettings(navOptions: NavOptions? = null) {
	this.navigate(Route.AnalyticsSettings.route, navOptions)
}

fun NavGraphBuilder.analyticsSettingsScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.AnalyticsSettings.route,
	) {
		AnalyticsSettingsRoute(onBackPressed = onBackPressed)
	}
}
