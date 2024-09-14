package ca.josephroque.bowlingcompanion.feature.quickplay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.quickplay.QuickPlayRoute

fun NavController.navigateToTeamPlay(team: TeamID?, navOptions: NavOptions? = null) {
	this.navigate(Route.TeamPlay.createRoute(team), navOptions)
}

fun NavGraphBuilder.teamPlay(
	onDismiss: () -> Unit,
	onPickLeague: (BowlerID, LeagueID?, ResourcePickerResultKey) -> Unit,
	onTeamLeaguesSelected: (TeamID, List<LeagueID>) -> Unit,
	onTeamEventsCreated: (TeamSeriesID, GameID) -> Unit,
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
