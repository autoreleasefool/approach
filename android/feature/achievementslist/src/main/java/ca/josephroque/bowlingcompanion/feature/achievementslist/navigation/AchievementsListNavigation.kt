package ca.josephroque.bowlingcompanion.feature.achievementslist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.achievementslist.AchievementsListRoute

fun NavController.navigateToAchievementsList(navOptions: NavOptions? = null) {
	this.navigate(Route.AchievementsList.route, navOptions)
}

fun NavGraphBuilder.achievementsListScreen(onBackPressed: () -> Unit) {
	composable(
		route = Route.AchievementsList.route,
	) {
		AchievementsListRoute(
			onBackPressed = onBackPressed,
		)
	}
}
