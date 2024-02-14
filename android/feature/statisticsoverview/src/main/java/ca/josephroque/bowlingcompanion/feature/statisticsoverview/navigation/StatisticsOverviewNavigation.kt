package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.StatisticsOverviewRoute

fun NavController.navigateToStatisticsOverview(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsOverview.route, navOptions)
}

fun NavGraphBuilder.statisticsOverviewScreen(onShowSourcePicker: () -> Unit) {
	composable(
		route = Route.StatisticsOverview.route,
	) {
		StatisticsOverviewRoute(
			onShowSourcePicker = onShowSourcePicker,
		)
	}
}
