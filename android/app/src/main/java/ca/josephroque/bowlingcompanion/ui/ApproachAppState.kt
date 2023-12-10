package ca.josephroque.bowlingcompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesNavigationRoute
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.navigateToAccessories
import ca.josephroque.bowlingcompanion.feature.overview.navigation.navigateToOverview
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToSettings
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsNavigationRoute
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.navigateToStatisticsOverview
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation.statisticsOverviewNavigationRoute
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

@Composable
fun rememberApproachAppState(
	navController: NavHostController = rememberNavController()
): ApproachAppState {
	return remember(navController) {
		ApproachAppState(navController)
	}
}

@Stable
class ApproachAppState(
	val navController: NavHostController,
) {
	val currentDestination: NavDestination?
		@Composable get() = navController
			.currentBackStackEntryAsState().value?.destination

	val currentTopLevelDestination: TopLevelDestination?
		@Composable get() = when(currentDestination?.route) {
			overviewNavigationRoute -> TopLevelDestination.APP_OVERVIEW
			accessoriesNavigationRoute -> TopLevelDestination.ACCESSORIES_OVERVIEW
			settingsNavigationRoute -> TopLevelDestination.SETTINGS_OVERVIEW
			statisticsOverviewNavigationRoute -> TopLevelDestination.STATISTICS_OVERVIEW
			else -> null
		}

	val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

	fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
		val topLevelNavOptions = navOptions {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}

			launchSingleTop = true
			restoreState = true
		}

		when (topLevelDestination) {
			TopLevelDestination.APP_OVERVIEW -> navController.navigateToOverview(topLevelNavOptions)
			TopLevelDestination.STATISTICS_OVERVIEW -> navController.navigateToStatisticsOverview(topLevelNavOptions)
			TopLevelDestination.ACCESSORIES_OVERVIEW -> navController.navigateToAccessories(topLevelNavOptions)
			TopLevelDestination.SETTINGS_OVERVIEW -> navController.navigateToSettings(topLevelNavOptions)
		}
	}
}