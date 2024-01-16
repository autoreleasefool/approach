package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.StatisticsOverviewRoute
import java.util.UUID

fun NavController.navigateToStatisticsOverview(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsOverview.route, navOptions)
}

fun NavGraphBuilder.statisticsOverviewScreen(
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickSeries: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickGame: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
) {
	composable(
		route = Route.StatisticsOverview.route,
	) {
		StatisticsOverviewRoute(
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onPickSeries = onPickSeries,
			onPickGame = onPickGame,
			onShowStatistics = onShowStatistics,
		)
	}
}