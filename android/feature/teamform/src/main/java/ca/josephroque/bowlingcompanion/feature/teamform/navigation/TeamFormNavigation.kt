package ca.josephroque.bowlingcompanion.feature.teamform.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamform.TeamFormRoute
import java.util.UUID

fun NavController.navigateToNewTeamForm(navOptions: NavOptions? = null) {
	this.navigate(Route.AddTeam.route, navOptions)
}

fun NavController.navigateToTeamForm(teamId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditTeam.createRoute(teamId), navOptions)
}

fun NavGraphBuilder.teamFormScreen(
	onBackPressed: () -> Unit,
	onManageTeamMembers: (List<UUID>, NavResultCallback<List<UUID>>) -> Unit,
) {
	composable(
		route = Route.EditTeam.route,
		arguments = listOf(
			navArgument(Route.EditTeam.ARG_TEAM) { type = NavType.StringType },
		),
	) {
		TeamFormRoute(
			onDismiss = onBackPressed,
			onManageTeamMembers = onManageTeamMembers,
		)
	}

	composable(
		route = Route.AddTeam.route,
	) {
		TeamFormRoute(
			onDismiss = onBackPressed,
			onManageTeamMembers = onManageTeamMembers,
		)
	}
}