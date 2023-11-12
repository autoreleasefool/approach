package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.settings.developer.DeveloperSettingsRoute

const val developerSettingsNavigationRoute = "settings/developer"

fun NavController.navigateToDeveloperSettings(navOptions: NavOptions? = null) {
	this.navigate(developerSettingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.developerSettingsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = developerSettingsNavigationRoute,
	) {
		DeveloperSettingsRoute(onBackPressed = onBackPressed)
	}
}
