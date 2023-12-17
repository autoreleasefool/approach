package ca.josephroque.bowlingcompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination
import ca.josephroque.bowlingcompanion.navigation.navigateToAccessoriesGraph
import ca.josephroque.bowlingcompanion.navigation.navigateToOverviewGraph
import ca.josephroque.bowlingcompanion.navigation.navigateToSettingsGraph
import ca.josephroque.bowlingcompanion.navigation.navigateToStatisticsGraph

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

	val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

	fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
		if (navController.currentDestination?.isTopLevelDestinationInHierarchy(topLevelDestination) == true) {
			return
		}

		val topLevelNavOptions = navOptions {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}

			launchSingleTop = true
			restoreState = true
		}

		when (topLevelDestination) {
			TopLevelDestination.APP_OVERVIEW -> navController.navigateToOverviewGraph(topLevelNavOptions)
			TopLevelDestination.STATISTICS_OVERVIEW -> navController.navigateToStatisticsGraph(topLevelNavOptions)
			TopLevelDestination.ACCESSORIES_OVERVIEW -> navController.navigateToAccessoriesGraph(topLevelNavOptions)
			TopLevelDestination.SETTINGS_OVERVIEW -> navController.navigateToSettingsGraph(topLevelNavOptions)
		}
	}
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
	this?.hierarchy?.any {
		return@any it.route?.contains(destination.name, true) ?: false
	} ?: false