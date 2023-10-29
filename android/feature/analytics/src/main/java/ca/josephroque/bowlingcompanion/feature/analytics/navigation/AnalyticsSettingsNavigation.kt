package ca.josephroque.bowlingcompanion.feature.analytics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.analytics.AnalyticsSettingsRoute

const val analyticsSettingsNavigationRoute = "settings/analytics"

fun NavController.navigateToAnalyticsSettings(navOptions: NavOptions? = null) {
	this.navigate(analyticsSettingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.analyticsSettingsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = analyticsSettingsNavigationRoute,
	) {
		AnalyticsSettingsRoute(onBackPressed = onBackPressed)
	}
}