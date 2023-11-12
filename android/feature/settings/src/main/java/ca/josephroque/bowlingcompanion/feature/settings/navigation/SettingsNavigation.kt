package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.settings.SettingsRoute

const val settingsNavigationRoute = "settings"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
	this.navigate(settingsNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen(
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	openDataImportSettings: () -> Unit,
	openDataExportSettings: () -> Unit,
) {
	composable(
		route = settingsNavigationRoute,
	) {
		SettingsRoute(
			openOpponents = openOpponents,
			openStatisticsSettings = openStatisticsSettings,
			openAcknowledgements = openAcknowledgements,
			openAnalyticsSettings = openAnalyticsSettings,
			openDeveloperSettings = openDeveloperSettings,
			openDataImportSettings = openDataImportSettings,
			openDataExportSettings = openDataExportSettings,
		)
	}
}