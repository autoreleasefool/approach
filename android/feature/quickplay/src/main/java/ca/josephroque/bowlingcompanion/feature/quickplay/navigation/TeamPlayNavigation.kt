package ca.josephroque.bowlingcompanion.feature.quickplay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.quickplay.QuickPlayRoute
import java.util.UUID

fun NavController.navigateToTeamPlay(team: UUID?, navOptions: NavOptions? = null) {
	this.navigate(Route.TeamPlay.createRoute(team), navOptions)
}

fun NavGraphBuilder.teamPlay(
	onDismiss: () -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onTeamLeaguesSelected: (UUID, List<UUID>) -> Unit,
	onTeamEventsCreated: (UUID, UUID) -> Unit,
	onShowTeamPlayOnboarding: () -> Unit,
) {
	composable(
		route = Route.TeamPlay.route,
		arguments = listOf(
			navArgument(Route.TeamPlay.ARG_TEAM) { type = NavType.StringType },
		),
	) {
		QuickPlayRoute(
			onDismiss = onDismiss,
			onTeamLeaguesSelected = onTeamLeaguesSelected,
			onTeamEventsCreated = onTeamEventsCreated,
			onPickLeague = onPickLeague,
			onShowQuickPlayOnboarding = onShowTeamPlayOnboarding,
			onPickBowler = { _, _ -> throw NotImplementedError("Team Play should not pick Bowler") },
			onBeginRecordingSeries = { _, _ ->
				throw NotImplementedError("Team Play should not record Series")
			},
		)
	}
}
