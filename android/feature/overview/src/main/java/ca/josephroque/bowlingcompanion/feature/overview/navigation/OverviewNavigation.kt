package ca.josephroque.bowlingcompanion.feature.overview.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.overview.OverviewRoute
import java.util.UUID

fun NavController.navigateToOverview(navOptions: NavOptions? = null) {
	this.navigate(Route.Overview.route, navOptions)
}

fun NavGraphBuilder.overviewScreen(
	shouldShowOnboarding: State<Boolean>,
	showOnboarding: () -> Unit,
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (UUID) -> Unit,
	onEditStatisticsWidgets: (String) -> Unit,
	onShowStatistics: (UUID) -> Unit,
) {
	composable(
		route = Route.Overview.route,
	) {
		LaunchedEffect(shouldShowOnboarding) {
			if (shouldShowOnboarding.value) {
				showOnboarding()
			}
		}

		if (!shouldShowOnboarding.value) {
			OverviewRoute(
				onEditBowler = onEditBowler,
				onAddBowler = onAddBowler,
				onShowBowlerDetails = onShowBowlerDetails,
				onEditStatisticsWidgets = onEditStatisticsWidgets,
				onShowStatistics = onShowStatistics,
			)
		}
	}
}
