package ca.josephroque.bowlingcompanion.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.SettingsRoute

fun NavGraphBuilder.settingsScreen(
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	openDataImportSettings: () -> Unit,
	openDataExportSettings: () -> Unit,
	openArchives: () -> Unit,
) {
	composable(
		route = Route.Settings.route,
	) {
		SettingsRoute(
			openOpponents = openOpponents,
			openStatisticsSettings = openStatisticsSettings,
			openAcknowledgements = openAcknowledgements,
			openAnalyticsSettings = openAnalyticsSettings,
			openDeveloperSettings = openDeveloperSettings,
			openDataImportSettings = openDataImportSettings,
			openDataExportSettings = openDataExportSettings,
			openArchives = openArchives,
		)
	}
}
