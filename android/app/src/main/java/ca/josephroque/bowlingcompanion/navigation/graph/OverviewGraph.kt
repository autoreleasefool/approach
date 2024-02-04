package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.avatarFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.bowlerDetailsScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.navigateToBowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.bowlerFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToBowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToNewBowlerForm
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesEditorScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesSettingsScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesSettingsForResult
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.laneFormScreen
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.leagueDetailsScreen
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.navigateToLeagueDetails
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.leagueFormScreen
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToLeagueForm
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToNewLeagueForm
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.matchPlayEditorScreen
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.navigateToMatchPlayEditor
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.navigateToOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.navigateToQuickPlay
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToEvent
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToSeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.seriesDetailsScreen
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.navigateToNewSeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.navigateToSeriesForm
import ca.josephroque.bowlingcompanion.feature.seriesform.navigation.seriesFormScreen
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticPickerForResult
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticsWidgetEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.navigateToStatisticsWidgetLayoutEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticPickerScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetEditorScreen
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetLayoutEditorScreen

fun NavGraphBuilder.overviewGraph(
	navController: NavController,
	shouldShowOnboarding: MutableState<Boolean>,
	finishActivity: () -> Unit,
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
		onShowQuickPlay = navController::navigateToQuickPlay,
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
				filter = alleyId.toString(),
			)
		},
		onShowGamesSettings = navController::navigateToGamesSettingsForResult,
		onEditRolledBall = { ball, result ->
			navController.navigateToResourcePickerForResult(
				selectedIds = ball?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				navResultCallback = result,
				resourceType = ResourcePickerType.GEAR,
				filter = GearKind.BOWLING_BALL.name,
			)
		},
		onShowStatistics = navController::navigateToStatisticsDetails,
	)
	gamesSettingsScreen(
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
				filter = bowler.toString(),
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