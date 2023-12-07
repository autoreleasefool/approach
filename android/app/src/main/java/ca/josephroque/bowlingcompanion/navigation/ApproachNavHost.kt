package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ca.josephroque.bowlingcompanion.core.common.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesScreen
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.alleyFormScreen
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.navigateToAlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.navigateToNewAlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.alleysListScreen
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.navigateToAlleysList
import ca.josephroque.bowlingcompanion.feature.archives.navigation.archivesList
import ca.josephroque.bowlingcompanion.feature.archives.navigation.navigateToArchivesList
import ca.josephroque.bowlingcompanion.feature.settings.navigation.analyticsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.avatarFormScreen
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.navigateToAvatarFormForResult
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.bowlerDetailsScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.navigateToBowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.bowlerFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToBowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToNewBowlerForm
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.dataExportScreen
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.navigateToDataExport
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesEditorScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesEditor
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.gearFormScreen
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.navigateToGearForm
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.navigateToNewGearForm
import ca.josephroque.bowlingcompanion.feature.gearlist.navigation.gearListScreen
import ca.josephroque.bowlingcompanion.feature.gearlist.navigation.navigateToGearList
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.laneFormScreen
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.navigateToLaneFormForResult
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.leagueDetailsScreen
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.navigateToLeagueDetails
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.leagueFormScreen
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToLeagueForm
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToNewLeagueForm
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.navigateToOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingScreen
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.navigateToOpponentsList
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.opponentsListScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.resourcePickerScreen
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToSeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.seriesDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.developerSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementDetails
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToDeveloperSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToStatisticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.statisticsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticPickerForResult
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticsWidgetEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticPickerScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetEditorScreen
import ca.josephroque.bowlingcompanion.ui.ApproachAppState

@Composable
fun ApproachNavHost(
	appState: ApproachAppState,
	modifier: Modifier = Modifier,
	isOnboardingComplete: Boolean?,
	finishActivity: () -> Unit = {},
	startDestination: String = overviewNavigationRoute
) {
	val navController = appState.navController

	isOnboardingComplete ?: return

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
			// TODO: Change to appropriate navigation for statistics widget editor
//			onEditStatisticsWidgets = navController::navigateToStatisticsWidgetLayoutEditor,
			onEditStatisticsWidgets = { navController.navigateToStatisticsWidgetEditor(it, null, 0) },
			onShowStatistics = { /* TODO: onShowStatistics */ },
		)
		statisticsOverviewScreen(
			onPickBowler = { bowler, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = bowler?.let { setOf(it) } ?: emptySet(),
					limit = 1,
					navResultCallback = result,
					resourceType = ResourcePickerType.BOWLER,
				)
			},
			onPickLeague = { bowler, league, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = league?.let { setOf(it) } ?: emptySet(),
					limit = 1,
					navResultCallback = result,
					resourceType = ResourcePickerType.LEAGUE,
					resourceParentId = bowler,
				)
			},
			onPickSeries = { _, _ -> /* TODO: onPickSeries */ },
			onPickGame = { _, _ -> /* TODO: onPickGame */ },
			onShowStatistics = navController::navigateToStatisticsDetails,
		)
		accessoriesScreen(
			onAddAlley = navController::navigateToNewAlleyForm,
			onAddGear = navController::navigateToNewGearForm,
			onViewAllAlleys = navController::navigateToAlleysList,
			onViewAllGear = navController::navigateToGearList,
			onShowAlleyDetails = { /* TODO: onShowAlleyDetails */ },
			onShowGearDetails = { /* TODO: onShowGearDetails */ },
		)
		settingsScreen(
			openOpponents = navController::navigateToOpponentsList,
			openStatisticsSettings = navController::navigateToStatisticsSettings,
			openAcknowledgements = navController::navigateToAcknowledgementsSettings,
			openAnalyticsSettings = navController::navigateToAnalyticsSettings,
			openDeveloperSettings = navController::navigateToDeveloperSettings,
			openDataExportSettings = navController::navigateToDataExport,
			openDataImportSettings = { /* TODO: openDataImportSettings */ },
			openArchives = navController::navigateToArchivesList,
		)
		dataExportScreen(
			onBackPressed = navController::popBackStack,
		)
		developerSettingsScreen(
			onBackPressed = navController::popBackStack,
		)
		bowlerFormScreen(
			onBackPressed = navController::popBackStack,
		)
		bowlerDetailsScreen(
			onEditLeague = navController::navigateToLeagueForm,
			onAddLeague = navController::navigateToNewLeagueForm,
			onBackPressed = navController::popBackStack,
			onShowLeagueDetails = navController::navigateToLeagueDetails,
			onShowGearDetails = { /* TODO: onShowGearDetails */ },
			onShowPreferredGearPicker = { selectedGear, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = selectedGear,
					navResultCallback = result,
					resourceType = ResourcePickerType.GEAR,
				)
			},
			onEditStatisticsWidgets = { /* TODO: onEditStatisticsWidgets */ },
			onShowStatistics = { /* TODO: onShowStatistics */ },
		)
		leagueFormScreen(
			onBackPressed = navController::popBackStack,
		)
		leagueDetailsScreen(
			onEditSeries = { /* TODO: onEditSeries */ },
			onAddSeries = { /* TODO: onAddSeries */ },
			onShowSeriesDetails = navController::navigateToSeriesDetails,
			onBackPressed = navController::popBackStack,
		)
		seriesDetailsScreen(
			onBackPressed = navController::popBackStack,
			onEditGame = {
				navController.navigateToGamesEditor(seriesId = it.seriesId, initialGameId = it.gameId)
			},
		)
		onboardingScreen(
			finishActivity = finishActivity,
			onCompleteOnboarding = {
				shouldShowOnboarding.value = false
				navController.popBackStack()
			},
		)
		opponentsListScreen(
			onBackPressed = navController::popBackStack,
			onAddOpponent = { navController.navigateToNewBowlerForm(BowlerKind.OPPONENT) },
			onOpenOpponentDetails = { /* TODO: onOpenOpponentDetails */ },
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
		alleysListScreen(
			onBackPressed = navController::popBackStack,
			onEditAlley = navController::navigateToAlleyForm,
			onAddAlley = navController::navigateToNewAlleyForm,
			onShowAlleyDetails = { /* TODO: onShowAlleyDetails */ },
		)
		alleyFormScreen(
			onBackPressed = navController::popBackStack,
			onManageLanes = { lanes, result ->
				navController.navigateToLaneFormForResult(
					existingLanes = lanes,
					navResultCallback = result,
				)
			},
		)
		gearListScreen(
			onBackPressed = navController::popBackStack,
			onEditGear = navController::navigateToGearForm,
			onAddGear = navController::navigateToNewGearForm,
			onShowGearDetails = { /* TODO: onShowGearDetails */ },
		)
		avatarFormScreen(
			onDismissWithResult = navController::popBackStackWithResult,
		)
		laneFormScreen(
			onDismissWithResult = navController::popBackStackWithResult,
		)
		gamesEditorScreen(
			onBackPressed = navController::popBackStack,
		)
		resourcePickerScreen(
			onDismissWithResult = navController::popBackStackWithResult,
		)
		gearFormScreen(
			onBackPressed = navController::popBackStack,
			onEditAvatar = navController::navigateToAvatarFormForResult,
			onEditOwner = { owner, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = owner?.let { setOf(it) } ?: emptySet(),
					limit = 1,
					navResultCallback = result,
					resourceType = ResourcePickerType.BOWLER,
				)
			}
		)
		statisticsDetailsScreen(
			onBackPressed = navController::popBackStack,
		)
		statisticsWidgetEditorScreen(
			onBackPressed = navController::popBackStack,
			onPickBowler = { bowler, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = bowler?.let { setOf(it) } ?: emptySet(),
					limit = 1,
					navResultCallback = result,
					resourceType = ResourcePickerType.BOWLER,
				)
			},
			onPickLeague = { bowler, league, result ->
				navController.navigateToResourcePickerForResult(
					selectedIds = league?.let { setOf(it) } ?: emptySet(),
					limit = 1,
					navResultCallback = result,
					resourceType = ResourcePickerType.LEAGUE,
					resourceParentId = bowler,
				)
			},
			onPickStatistic = { statistic, result ->
				navController.navigateToStatisticPickerForResult(
					selectedStatistic = statistic,
					navResultCallback = result,
				)
			},
		)
		statisticPickerScreen(
			onDismissWithResult = navController::popBackStackWithResult,
		)
	}
}