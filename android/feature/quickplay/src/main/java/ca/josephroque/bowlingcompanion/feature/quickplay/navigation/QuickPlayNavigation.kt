package ca.josephroque.bowlingcompanion.feature.quickplay.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.quickplay.QuickPlayRoute
import ca.josephroque.bowlingcompanion.feature.quickplay.onboarding.QuickPlayOnboardingRoute

fun NavController.navigateToQuickPlay(navOptions: NavOptions? = null) {
	this.navigate(Route.QuickPlay.route, navOptions)
}

fun NavController.navigateToQuickPlayOnboarding(navOptions: NavOptions? = null) {
	this.navigate(Route.QuickPlayOnboarding.route, navOptions)
}

fun NavGraphBuilder.quickPlay(
	onBackPressed: () -> Unit,
	onBeginRecording: (List<SeriesID>, GameID) -> Unit,
	onPickBowler: (Set<BowlerID>, ResourcePickerResultKey) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, ResourcePickerResultKey) -> Unit,
	onShowQuickPlayOnboarding: () -> Unit,
) {
	bottomSheet(
		route = Route.QuickPlay.route,
	) {
		QuickPlayRoute(
			onDismiss = onBackPressed,
			onBeginRecordingSeries = onBeginRecording,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onShowQuickPlayOnboarding = onShowQuickPlayOnboarding,
			onTeamLeaguesSelected = { _, _ ->
				throw NotImplementedError("Quick Play should not record Team Series")
			},
			onTeamEventsCreated = { _, _ ->
				throw NotImplementedError("Quick Play should not record Team Event")
			},
		)
	}
}

fun NavGraphBuilder.quickPlayOnboarding(onBackPressed: () -> Unit) {
	bottomSheet(
		route = Route.QuickPlayOnboarding.route,
	) {
		QuickPlayOnboardingRoute(
			onDismiss = onBackPressed,
		)
	}
}
