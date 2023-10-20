package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.accessories.navigation.accessoriesScreen
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.alleyFormScreen
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.navigateToNewAlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.alleysListScreen
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.navigateToAlleysList
import ca.josephroque.bowlingcompanion.feature.analytics.navigation.analyticsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.analytics.navigation.navigateToAnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.bowlerDetailsScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.navigateToBowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.bowlerFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToBowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToNewBowlerForm
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.laneFormScreen
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.navigateToLaneForm
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.leagueDetailsScreen
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.navigateToLeagueDetails
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.leagueFormScreen
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToNewLeagueForm
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.navigateToOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingScreen
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.navigateToOpponentsList
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.opponentsListScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToSeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.seriesDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.statistics.navigation.statisticsScreen
import ca.josephroque.bowlingcompanion.feature.statisticssettings.navigation.navigateToStatisticsSettings
import ca.josephroque.bowlingcompanion.feature.statisticssettings.navigation.statisticsSettingsScreen
import ca.josephroque.bowlingcompanion.ui.ApproachAppState

@Composable
fun ApproachNavHost(
	appState: ApproachAppState,
	modifier: Modifier = Modifier,
	isOnboardingComplete: Boolean = true,
	finishActivity: () -> Unit = {},
	startDestination: String = overviewNavigationRoute
) {
	val navController = appState.navController

	val shouldShowOnboarding = remember(isOnboardingComplete) {
		mutableStateOf(!isOnboardingComplete)
	}

	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier,
	) {
		overviewScreen(
			shouldShowOnboarding = shouldShowOnboarding,
			showOnboarding = navController::navigateToOnboarding,
			onEditBowler = navController::navigateToBowlerForm,
			onAddBowler = { navController.navigateToNewBowlerForm(BowlerKind.PLAYABLE) },
			onShowBowlerDetails = navController::navigateToBowlerDetails,
		)
		statisticsScreen()
		accessoriesScreen(
			onAddAlley = navController::navigateToNewAlleyForm,
			onAddGear = { /* TODO: onAddGear */ },
			onViewAllAlleys = navController::navigateToAlleysList,
			onViewAllGear = { /* TODO: onViewAllGear */ },
			onShowAlleyDetails = { /* TODO: onShowAlleyDetails */ },
			onShowGearDetails = { /* TODO: onShowGearDetails */ },
		)
		settingsScreen(
			openOpponents = navController::navigateToOpponentsList,
			openStatisticsSettings = navController::navigateToStatisticsSettings,
			openAcknowledgements = { /* TODO: openAcknowledgements */ },
			openAnalyticsSettings = navController::navigateToAnalyticsSettings,
			openDeveloperSettings = { /* TODO: openDeveloperSettings */ },
			openDataExportSettings = { /* TODO: openDataExportSettings */ },
			openDataImportSettings = { /* TODO: openDataImportSettings */ },
		)
		bowlerFormScreen(
			onBackPressed = { navController.popBackStack() },
		)
		bowlerDetailsScreen(
			onEditLeague = { /* TODO: onEditLeague */ },
			onAddLeague = navController::navigateToNewLeagueForm,
			onBackPressed = { navController.popBackStack() },
			onShowLeagueDetails = navController::navigateToLeagueDetails,
			onShowGearDetails = { /* TODO: onShowGearDetails */ },
			onShowPreferredGearPicker = { /* TODO: onShowPreferredGearPicker */ },
		)
		leagueFormScreen(
			onBackPressed = { navController.popBackStack() },
		)
		leagueDetailsScreen(
			onEditSeries = { /* TODO: onEditSeries */ },
			onAddSeries = { /* TODO: onAddSeries */ },
			onShowSeriesDetails = navController::navigateToSeriesDetails,
			onBackPressed = { navController.popBackStack() },
		)
		seriesDetailsScreen(
			onEditGame = { /* TODO: onEditGame */ },
		)
		onboardingScreen(
			finishActivity = finishActivity,
			onCompleteOnboarding = {
				shouldShowOnboarding.value = false
				navController.popBackStack()
			},
		)
		opponentsListScreen(
			onBackPressed = { navController.popBackStack() },
			onAddOpponent = { navController.navigateToNewBowlerForm(BowlerKind.OPPONENT) },
			onOpenOpponentDetails = { /* TODO: onOpenOpponentDetails */ },
		)
		analyticsSettingsScreen(
			onBackPressed = { navController.popBackStack() },
		)
		statisticsSettingsScreen(
			onBackPressed = { navController.popBackStack() },
		)
		alleysListScreen(
			onBackPressed = { navController.popBackStack() },
			onEditAlley = { /* TODO: onEditAlley */ },
			onAddAlley = navController::navigateToNewAlleyForm,
			onShowAlleyDetails = { /* TODO: onShowAlleyDetails */ },
		)
		alleyFormScreen(
			onBackPressed = { navController.popBackStack() },
			onManageLanes = navController::navigateToLaneForm,
		)
		laneFormScreen(
			onBackPressed = { navController.popBackStack() },
		)
	}
}