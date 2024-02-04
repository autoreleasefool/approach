package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesOnboardingSheet
import ca.josephroque.bowlingcompanion.feature.overview.navigation.quickPlay
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.resourcePickerSheet
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.navigateToStatisticsDetails
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsSourcePickerSheet

fun NavGraphBuilder.bottomSheetGraph(
	navController: NavController,
) {
	resourcePickerSheet(
		onDismissWithResult = navController::popBackStackWithResult,
	)
	accessoriesOnboardingSheet(
		onBackPressed = navController::popBackStack,
	)
	quickPlay(
		onBackPressed = navController::popBackStack,
		onBeginRecording = { /* TODO: Start Recording */ },
		onPickBowler = { excluded, result ->
			navController.navigateToResourcePickerForResult(
				selectedIds = emptySet(),
				hiddenIds = excluded,
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
				filter = "${bowler}:${LeagueRecurrence.REPEATING}",
			)
		},
	)
	statisticsSourcePickerSheet(
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
		onPickSeries = { league, series, result ->
			navController.navigateToResourcePickerForResult(
				selectedIds = series?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				navResultCallback = result,
				resourceType = ResourcePickerType.SERIES,
				filter = league.toString(),
			)
		},
		onPickGame = { series, game, result ->
			navController.navigateToResourcePickerForResult(
				selectedIds = game?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				navResultCallback = result,
				resourceType = ResourcePickerType.GAME,
				filter = series.toString(),
			)
		},
		onShowStatistics = navController::navigateToStatisticsDetails,
	)
}