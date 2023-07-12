package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ca.josephroque.bowlingcompanion.feature.accessories.navigation.accessoriesScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.statistics.navigation.statisticsScreen
import ca.josephroque.bowlingcompanion.ui.ApproachAppState

@Composable
fun ApproachNavHost(
	appState: ApproachAppState,
	modifier: Modifier = Modifier,
	startDestination: String = overviewNavigationRoute
) {
	val navController = appState.navController
	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier,
	) {
		overviewScreen()
		statisticsScreen()
		accessoriesScreen()
		settingsScreen()
	}
}