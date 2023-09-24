package ca.josephroque.bowlingcompanion.feature.overview.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.overview.OverviewRoute
import java.util.UUID

const val overviewNavigationRoute = "overview"

fun NavController.navigateToOverview(navOptions: NavOptions? = null) {
	this.navigate(overviewNavigationRoute, navOptions)
}

fun NavGraphBuilder.overviewScreen(
	shouldShowOnboarding: State<Boolean>,
	showOnboarding: () -> Unit,
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	onShowBowlerDetails: (UUID) -> Unit,
) {
	composable(
		route = overviewNavigationRoute,
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
			)
		}
	}
}
