package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import ca.josephroque.bowlingcompanion.core.common.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesNavigationRoute
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
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.dataImportScreen
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.navigateToDataExport
import ca.josephroque.bowlingcompanion.feature.datamanagement.navigation.navigateToDataImport
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesEditorScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesSettingsScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesSettingsForResult
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
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.matchPlayEditorScreen
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.navigateToMatchPlayEditor
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.navigateToOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingScreen
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.navigateToOpponentsList
import ca.josephroque.bowlingcompanion.feature.opponentslist.navigation.opponentsListScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.resourcePickerScreen
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToEvent
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToSeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.seriesDetailsScreen
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.navigateToNewSeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.navigateToSeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.seriesFormScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.acknowledgementsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.developerSettingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementDetails
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToAcknowledgementsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToDeveloperSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToStatisticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsNavigationRoute
import ca.josephroque.bowlingcompanion.feature.settings.navigation.statisticsSettingsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticPickerForResult
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticsWidgetEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticsWidgetLayoutEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticPickerScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetEditorScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetLayoutEditorScreen
import ca.josephroque.bowlingcompanion.ui.ApproachAppState

@Composable
fun ApproachNavHost(
	appState: ApproachAppState,
	modifier: Modifier = Modifier,
	isOnboardingComplete: Boolean?,
	finishActivity: () -> Unit = {},
	startDestination: String = TopLevelDestination.APP_OVERVIEW.graphName,
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
		navigation(
			route = TopLevelDestination.APP_OVERVIEW.graphName,
			startDestination = overviewNavigationRoute,
		) {
			overviewScreen(
				shouldShowOnboarding = shouldShowOnboarding,
				showOnboarding = navController::navigateToOnboarding,
				onEditBowler = navController::navigateToBowlerForm,
				onAddBowler = { navController.navigateToNewBowlerForm(BowlerKind.PLAYABLE) },
				onShowBowlerDetails = navController::navigateToBowlerDetails,
				onEditStatisticsWidgets = {
					navController.navigateToStatisticsWidgetLayoutEditor(it, null)
				},
				onShowStatistics = { /* TODO: onShowStatistics */ },
			)
			onboardingScreen(
				finishActivity = finishActivity,
				onCompleteOnboarding = {
					shouldShowOnboarding.value = false
					navController.popBackStack()
				},
			)
			bowlerFormScreen(
				onBackPressed = navController::popBackStack,
			)
			bowlerDetailsScreen(
				onEditLeague = navController::navigateToLeagueForm,
				onAddLeague = navController::navigateToNewLeagueForm,
				onBackPressed = navController::popBackStack,
				onShowLeagueDetails = navController::navigateToLeagueDetails,
				onShowEventDetails = navController::navigateToEvent,
				onShowGearDetails = { /* FIXME: onShowGearDetails */ },
				onShowPreferredGearPicker = { selectedGear, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = selectedGear,
						navResultCallback = result,
						resourceType = ResourcePickerType.GEAR,
					)
				},
				onEditStatisticsWidgets = { context, bowlerId ->
					navController.navigateToStatisticsWidgetLayoutEditor(
						context = context,
						initialSource = StatisticsWidgetInitialSource.Bowler(bowlerId),
					)
				},
				onShowStatistics = { /* TODO: onShowStatistics */ },
			)
			leagueFormScreen(
				onBackPressed = navController::popBackStack,
			)
			leagueDetailsScreen(
				onEditSeries = navController::navigateToSeriesForm,
				onAddSeries = { leagueId, result ->
					navController.navigateToNewSeriesForm(leagueId, result)
				},
				onShowSeriesDetails = navController::navigateToSeriesDetails,
				onBackPressed = navController::popBackStack,
			)
			seriesFormScreen(
				onDismissWithResult = navController::popBackStackWithResult,
				onEditAlley = { alley, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = alley?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.ALLEY,
					)
				},
			)
			seriesDetailsScreen(
				onBackPressed = navController::popBackStack,
				onEditGame = {
					navController.navigateToGamesEditor(seriesId = it.seriesId, initialGameId = it.gameId)
				},
			)
			avatarFormScreen(
				onDismissWithResult = navController::popBackStackWithResult,
			)
			laneFormScreen(
				onDismissWithResult = navController::popBackStackWithResult,
			)
			gamesEditorScreen(
				onBackPressed = navController::popBackStack,
				onEditMatchPlay = navController::navigateToMatchPlayEditor,
				onEditGear = { gear, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = gear,
						navResultCallback = result,
						resourceType = ResourcePickerType.GEAR,
					)
				},
				onEditAlley = { alley, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = alley?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.ALLEY,
					)
				},
				onEditLanes = { alleyId, lanes, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = lanes,
						navResultCallback = result,
						resourceType = ResourcePickerType.LANE,
						resourceFilter = alleyId.toString(),
					)
				},
				onShowGamesSettings = navController::navigateToGamesSettingsForResult,
				onEditRolledBall = { ball, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = ball?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.GEAR,
						resourceFilter = GearKind.BOWLING_BALL.name,
					)
				},
				onShowStatistics = navController::navigateToStatisticsDetails,
			)
			gamesSettingsScreen(
				onDismissWithResult = navController::popBackStackWithResult,
			)
			resourcePickerScreen(
				onDismissWithResult = navController::popBackStackWithResult,
			)
			statisticsWidgetLayoutEditorScreen(
				onBackPressed = navController::popBackStack,
				onAddWidget = navController::navigateToStatisticsWidgetEditor,
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
						resourceFilter = bowler.toString(),
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
			matchPlayEditorScreen(
				onBackPressed = navController::popBackStack,
				onEditOpponent = { opponent, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = opponent?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.BOWLER,
					)
				},
			)
			statisticsDetailsScreen(
				onBackPressed = navController::popBackStack,
			)
		}

		navigation(
			route = TopLevelDestination.STATISTICS_OVERVIEW.graphName,
			startDestination = statisticsOverviewNavigationRoute,
		) {
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
						resourceFilter = bowler.toString(),
					)
				},
				onPickSeries = { league, series, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = series?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.SERIES,
						resourceFilter = league.toString(),
					)
				},
				onPickGame = { series, game, result ->
					navController.navigateToResourcePickerForResult(
						selectedIds = game?.let { setOf(it) } ?: emptySet(),
						limit = 1,
						navResultCallback = result,
						resourceType = ResourcePickerType.GAME,
						resourceFilter = series.toString(),
					)
				},
				onShowStatistics = navController::navigateToStatisticsDetails,
			)
			statisticsDetailsScreen(
				onBackPressed = navController::popBackStack,
			)
		}

		navigation(
			route = TopLevelDestination.ACCESSORIES_OVERVIEW.graphName,
			startDestination = accessoriesNavigationRoute,
		) {
			accessoriesScreen(
				onAddAlley = navController::navigateToNewAlleyForm,
				onAddGear = navController::navigateToNewGearForm,
				onViewAllAlleys = navController::navigateToAlleysList,
				onViewAllGear = navController::navigateToGearList,
				onShowAlleyDetails = { /* FIXME: onShowAlleyDetails */ },
				onShowGearDetails = { /* FIXME: onShowGearDetails */ },
			)
			alleysListScreen(
				onBackPressed = navController::popBackStack,
				onEditAlley = navController::navigateToAlleyForm,
				onAddAlley = navController::navigateToNewAlleyForm,
				onShowAlleyDetails = { /* FIXME: onShowAlleyDetails */ },
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
				onShowGearDetails = { /* FIXME: onShowGearDetails */ },
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
		}

		navigation(
			route = TopLevelDestination.SETTINGS_OVERVIEW.graphName,
			startDestination = settingsNavigationRoute,
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
	}
}