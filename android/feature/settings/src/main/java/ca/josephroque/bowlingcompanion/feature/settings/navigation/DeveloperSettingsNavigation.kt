package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.developer.DeveloperSettingsRoute

fun NavController.navigateToDeveloperSettings(navOptions: NavOptions? = null) {
	this.navigate(Route.DeveloperSettings.route, navOptions)
}

fun NavGraphBuilder.developerSettingsScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.DeveloperSettings.route,
	) {
		DeveloperSettingsRoute(onBackPressed = onBackPressed)
	}
}
