package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.navigation.graph.accessoriesGraph
import ca.josephroque.bowlingcompanion.navigation.graph.bottomSheetGraph
import ca.josephroque.bowlingcompanion.navigation.graph.overviewGraph
import ca.josephroque.bowlingcompanion.navigation.graph.settingsGraph
import ca.josephroque.bowlingcompanion.navigation.graph.statisticsGraph
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
				startDestination = Route.Overview.route,
			) {
				overviewGraph(
					navController = navController,
					shouldShowOnboarding = shouldShowOnboarding,
					finishActivity = finishActivity,
				)
			}

			navigation(
				route = TopLevelDestination.STATISTICS_OVERVIEW.graphName,
				startDestination = Route.StatisticsOverview.route,
			) {
				statisticsGraph(navController = navController)
			}

			navigation(
				route = TopLevelDestination.ACCESSORIES_OVERVIEW.graphName,
				startDestination = Route.AccessoriesOverview.route,
			) {
				accessoriesGraph(navController = navController)
			}

			navigation(
				route = TopLevelDestination.SETTINGS_OVERVIEW.graphName,
				startDestination = Route.Settings.route,
			) {
				settingsGraph(navController = navController)
			}

			bottomSheetGraph(navController = navController)
	}
}