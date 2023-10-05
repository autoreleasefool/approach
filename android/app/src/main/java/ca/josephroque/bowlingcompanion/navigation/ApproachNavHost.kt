package ca.josephroque.bowlingcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.accessories.navigation.accessoriesScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.bowlerDetailsScreen
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.navigation.navigateToBowlerDetails
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.bowlerFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToBowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.navigateToNewBowlerForm
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.leagueDetailsScreen
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.navigateToLeagueDetails
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.leagueFormScreen
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.navigateToNewLeagueForm
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.navigateToOnboarding
import ca.josephroque.bowlingcompanion.feature.onboarding.navigation.onboardingScreen
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewNavigationRoute
import ca.josephroque.bowlingcompanion.feature.overview.navigation.overviewScreen
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.navigateToSeriesDetails
import ca.josephroque.bowlingcompanion.feature.seriesdetails.navigation.seriesDetailsScreen
import ca.josephroque.bowlingcompanion.feature.settings.navigation.settingsScreen
import ca.josephroque.bowlingcompanion.feature.statistics.navigation.statisticsScreen
import ca.josephroque.bowlingcompanion.ui.ApproachAppState

@Composable
fun ApproachNavHost(
	appState: ApproachAppState,
	modifier: Modifier = Modifier,
	isOnboardingComplete: Boolean = true,
	finishActivity: () -> Unit = {},
	startDestination: String = overviewNavigationRoute
) {
	val navController = appState.navController

	val shouldShowOnboarding = remember(isOnboardingComplete) {
		mutableStateOf(!isOnboardingComplete)
	}

	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier,
	) {
		overviewScreen(
			shouldShowOnboarding = shouldShowOnboarding,
			showOnboarding = navController::navigateToOnboarding,
			onEditBowler = navController::navigateToBowlerForm,
			onAddBowler = { navController.navigateToNewBowlerForm(BowlerKind.PLAYABLE) },
			onShowBowlerDetails = navController::navigateToBowlerDetails,
		)
		statisticsScreen()
		accessoriesScreen()
		settingsScreen()
		bowlerFormScreen(
			onBackPressed = { navController.popBackStack() },
		)
		bowlerDetailsScreen(
			onEditLeague = {},
			onAddLeague = navController::navigateToNewLeagueForm,
			onBackPressed = { navController.popBackStack() },
			onShowLeagueDetails = navController::navigateToLeagueDetails,
		)
		leagueFormScreen(
			onBackPressed = { navController.popBackStack() },
		)
		leagueDetailsScreen(
			onEditSeries = {},
			onAddSeries = {},
			onShowSeriesDetails = navController::navigateToSeriesDetails,
			onBackPressed = { navController.popBackStack() },
		)
		seriesDetailsScreen(
			onEditGame = {},
		)
		onboardingScreen(
			finishActivity = finishActivity,
			onCompleteOnboarding = {
				shouldShowOnboarding.value = false
				navController.popBackStack()
			},
		)
	}
}