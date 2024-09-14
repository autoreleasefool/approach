package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesOnboardingSheet
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.gamesSettingsScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.navigateToGamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.scoreEditorScreen
import ca.josephroque.bowlingcompanion.feature.gameseditor.navigation.scoresListScreen
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.matchPlayEditorScreen
import ca.josephroque.bowlingcompanion.feature.quickplay.navigation.navigateToQuickPlayOnboarding
import ca.josephroque.bowlingcompanion.feature.quickplay.navigation.quickPlay
import ca.josephroque.bowlingcompanion.feature.quickplay.navigation.quickPlayOnboarding
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToBowlerPickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToGamePickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToLeaguePickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToSeriesPickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToTeamPickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.resourcePickerSheet
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.midGameStatisticsDetailsScreen
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.statisticsDetailsChartSheet
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsSourcePickerSheet
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticPickerSheet
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.statisticsWidgetError

fun NavGraphBuilder.bottomSheetGraph(navController: NavController) {
	resourcePickerSheet(
		navController = navController,
		onDismiss = navController::popBackStack,
		onDismissWithResult = navController::popBackStackWithResult,
	)
	accessoriesOnboardingSheet(
		onBackPressed = navController::popBackStack,
	)
	quickPlay(
		onBackPressed = navController::popBackStack,
		onBeginRecording = navController::navigateToGamesEditor,
		onPickBowler = { excluded, resultKey ->
			navController.navigateToBowlerPickerForResult(
				resultKey = resultKey,
				selectedIds = emptySet(),
				hiddenIds = excluded,
				limit = 1,
				kind = BowlerKind.PLAYABLE,
			)
		},
		onPickLeague = { bowler, league, resultKey ->
			navController.navigateToLeaguePickerForResult(
				selectedIds = league?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				bowlerId = bowler,
				recurrence = LeagueRecurrence.REPEATING,
			)
		},
		onShowQuickPlayOnboarding = navController::navigateToQuickPlayOnboarding,
	)
	quickPlayOnboarding(
		onBackPressed = navController::popBackStack,
	)
	statisticsSourcePickerSheet(
		onBackPressed = navController::popBackStack,
		onPickTeam = { team, resultKey ->
			navController.navigateToTeamPickerForResult(
				selectedIds = team?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
			)
		},
		onPickBowler = { bowler, resultKey ->
			navController.navigateToBowlerPickerForResult(
				selectedIds = bowler?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				kind = BowlerKind.PLAYABLE,
			)
		},
		onPickLeague = { bowler, league, resultKey ->
			navController.navigateToLeaguePickerForResult(
				selectedIds = league?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				bowlerId = bowler,
			)
		},
		onPickSeries = { league, series, resultKey ->
			navController.navigateToSeriesPickerForResult(
				selectedIds = series?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				leagueId = league,
			)
		},
		onPickGame = { series, game, resultKey ->
			navController.navigateToGamePickerForResult(
				selectedIds = game?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				seriesId = series,
			)
		},
		onShowStatistics = navController::navigateToStatisticsDetails,
	)
	gamesSettingsScreen(
		onDismissWithResult = navController::popBackStackWithResult,
	)
	scoresListScreen(
		onDismiss = navController::popBackStack,
	)
	midGameStatisticsDetailsScreen(
		onBackPressed = navController::popBackStack,
	)
	statisticsDetailsChartSheet(
		onBackPressed = navController::popBackStack,
	)
	matchPlayEditorScreen(
		onBackPressed = navController::popBackStack,
		onEditOpponent = { opponent, resultKey ->
			navController.navigateToBowlerPickerForResult(
				selectedIds = opponent?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				resultKey = resultKey,
				kind = BowlerKind.OPPONENT,
			)
		},
	)
	scoreEditorScreen(
		onDismissWithResult = navController::popBackStackWithResult,
	)
	statisticPickerSheet(
		onDismissWithResult = navController::popBackStackWithResult,
	)
	statisticsWidgetError(
		onDismiss = navController::popBackStack,
	)
}
