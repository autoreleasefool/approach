package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.archives.navigation.archivesList
import ca.josephroque.bowlingcompanion.feature.archives.navigation.navigateToArchivesList
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToBowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToNewBowlerForm
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.dataExportScreen
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.dataImportScreen
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.navigateToDataExport
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.navigateToDataImport
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.navigateToOpponentsList
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.opponentsListScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.analyticsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.developerSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementDetails
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToDeveloperSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToStatisticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.statisticsSettingsScreen

fun NavGraphBuilder.settingsGraph(
	navController: NavController,
) {
	settingsScreen(
		openOpponents = navController::navigateToOpponentsList,
		openStatisticsSettings = navController::navigateToStatisticsSettings,
		openAcknowledgements = navController::navigateToAcknowledgementsSettings,
		openAnalyticsSettings = navController::navigateToAnalyticsSettings,
		openDeveloperSettings = navController::navigateToDeveloperSettings,
		openDataExportSettings = navController::navigateToDataExport,
		openDataImportSettings = navController::navigateToDataImport,
		openArchives = navController::navigateToArchivesList,
	)
	dataExportScreen(
		onBackPressed = navController::popBackStack,
	)
	dataImportScreen(
		onBackPressed = navController::popBackStack,
	)
	developerSettingsScreen(
		onBackPressed = navController::popBackStack,
	)
	opponentsListScreen(
		onBackPressed = navController::popBackStack,
		onAddOpponent = { navController.navigateToNewBowlerForm(BowlerKind.OPPONENT) },
		onOpenOpponentDetails = { /* FIXME: onOpenOpponentDetails */ },
		onEditOpponent = navController::navigateToBowlerForm,
	)
	analyticsSettingsScreen(
		onBackPressed = navController::popBackStack,
	)
	statisticsSettingsScreen(
		onBackPressed = navController::popBackStack,
	)
	acknowledgementsSettingsScreen(
		onBackPressed = navController::popBackStack,
		onShowAcknowledgementDetails = navController::navigateToAcknowledgementDetails,
	)
	acknowledgementDetailsScreen(
		onBackPressed = navController::popBackStack,
	)
	archivesList(
		onBackPressed = navController::popBackStack,
	)
}