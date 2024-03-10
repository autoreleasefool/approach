package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.StatisticsOverviewRoute

fun NavGraphBuilder.statisticsOverviewScreen(onShowSourcePicker: () -> Unit) {
	composable(
		route = Route.StatisticsOverview.route,
	) {
		StatisticsOverviewRoute(
			onShowSourcePicker = onShowSourcePicker,
		)
	}
}
