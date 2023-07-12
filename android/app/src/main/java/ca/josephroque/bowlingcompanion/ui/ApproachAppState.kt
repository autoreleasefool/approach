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
import ca.josephroque.bowlingcompanion.feature.accessories.navigation.navigateToAccessories
import ca.josephroque.bowlingcompanion.feature.overview.navigation.navigateToOverview
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.settings.navigation.navigateToSettings
import ca.josephroque.bowlingcompanion.feature.statistics.navigation.navigateToStatistics
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
			overviewNavigationRoute -> TopLevelDestination.OVERVIEW
			else -> null
		}

	val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

	fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
		val topLevelNavOptions = navOptions {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}

			launchSingleTop = true
			restoreState = true
		}

		when (topLevelDestination) {
			TopLevelDestination.OVERVIEW -> navController.navigateToOverview(topLevelNavOptions)
			TopLevelDestination.STATISTICS -> navController.navigateToStatistics(topLevelNavOptions)
			TopLevelDestination.ACCESSORIES -> navController.navigateToAccessories(topLevelNavOptions)
			TopLevelDestination.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
		}
	}
}