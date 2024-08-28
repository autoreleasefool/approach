package ca.josephroque.bowlingcompanion.feature.quickplay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.quickplay.QuickPlayRoute
import ca.josephroque.bowlingcompanion.feature.quickplay.onboarding.QuickPlayOnboardingRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToQuickPlay(team: UUID?, navOptions: NavOptions? = null) {
	this.navigate(Route.QuickPlay.createRoute(team), navOptions)
}

fun NavController.navigateToQuickPlayOnboarding(navOptions: NavOptions? = null) {
	this.navigate(Route.QuickPlayOnboarding.route, navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.quickPlay(
	onBackPressed: () -> Unit,
	onBeginRecording: (List<SeriesID>, GameID) -> Unit,
	onPickBowler: (Set<BowlerID>, NavResultCallback<Set<BowlerID>>) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, NavResultCallback<Set<LeagueID>>) -> Unit,
	onShowQuickPlayOnboarding: () -> Unit,
) {
	bottomSheet(
		route = Route.QuickPlay.route,
	) {
		QuickPlayRoute(
			onDismiss = onBackPressed,
			onBeginRecording = onBeginRecording,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onShowQuickPlayOnboarding = onShowQuickPlayOnboarding,
		)
	}
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.quickPlayOnboarding(onBackPressed: () -> Unit) {
	bottomSheet(
		route = Route.QuickPlayOnboarding.route,
	) {
		QuickPlayOnboardingRoute(
			onDismiss = onBackPressed,
		)
	}
}
