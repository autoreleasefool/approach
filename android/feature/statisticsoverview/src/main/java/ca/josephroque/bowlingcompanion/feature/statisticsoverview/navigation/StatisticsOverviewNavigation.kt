package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.StatisticsOverviewRoute
import java.util.UUID

const val statisticsOverviewNavigationRoute = "statistics_overview"

fun NavController.navigateToStatisticsOverview(navOptions: NavOptions? = null) {
	this.navigate(statisticsOverviewNavigationRoute, navOptions)
}

fun NavGraphBuilder.statisticsOverviewScreen(
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickSeries: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickGame: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
) {
	composable(
		route = statisticsOverviewNavigationRoute,
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