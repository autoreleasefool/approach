package ca.josephroque.bowlingcompanion.feature.seriesform.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.SeriesFormResultViewModel
import ca.josephroque.bowlingcompanion.feature.seriesform.SeriesFormRoute

fun NavController.navigateToSeriesForm(seriesId: SeriesID, navOptions: NavOptions? = null) {
	this.navigate(Route.EditSeries.createRoute(seriesId), navOptions)
}

fun NavController.navigateToNewSeriesForm(leagueId: LeagueID, navOptions: NavOptions? = null) {
	this.navigate(Route.AddSeries.createRoute(leagueId), navOptions)
}

fun NavController.navigateToNewTeamSeriesForm(
	teamId: TeamID,
	leagues: List<LeagueID>,
	navOptions: NavOptions? = null,
) {
	this.navigate(Route.AddTeamSeries.createRoute(teamId, leagues), navOptions)
}

fun NavGraphBuilder.seriesFormScreen(
	navController: NavController,
	onDismiss: () -> Unit,
	onEditAlley: (AlleyID?, ResourcePickerResultKey) -> Unit,
	onEditLeague: (BowlerID, LeagueID, ResourcePickerResultKey) -> Unit,
) {
	composable(
		route = Route.EditSeries.route,
		arguments = listOf(
			navArgument(Route.EditSeries.ARG_SERIES) { type = NavType.StringType },
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<SeriesFormResultViewModel>()
		} else {
			hiltViewModel<SeriesFormResultViewModel>(parentEntry)
		}

		SeriesFormRoute(
			onDismissWithResult = { result ->
				result?.let { series -> resultViewModel.setResult(series) }
				onDismiss()
			},
			onEditAlley = onEditAlley,
			onEditLeague = onEditLeague,
			onStartTeamSeries = { _, _ ->
				throw NotImplementedError("SeriesFormRoute should not start Team Series")
			},
		)
	}
	composable(
		route = Route.AddSeries.route,
		arguments = listOf(
			navArgument(Route.AddSeries.ARG_LEAGUE) { type = NavType.StringType },
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<SeriesFormResultViewModel>()
		} else {
			hiltViewModel<SeriesFormResultViewModel>(parentEntry)
		}

		SeriesFormRoute(
			onDismissWithResult = { result ->
				result?.let { series -> resultViewModel.setResult(series) }
				onDismiss()
			},
			onEditAlley = onEditAlley,
			onEditLeague = { _, _, _ ->
				throw NotImplementedError("SeriesFormRoute should not edit league when adding a series")
			},
			onStartTeamSeries = { _, _ ->
				throw NotImplementedError("SeriesFormRoute should not start Team Series")
			},
		)
	}
}

fun NavGraphBuilder.teamSeriesFormScreen(
	onDismiss: () -> Unit,
	onStartTeamSeries: (TeamSeriesID, GameID) -> Unit,
	onEditAlley: (AlleyID?, ResourcePickerResultKey) -> Unit,
) {
	composable(
		route = Route.AddTeamSeries.route,
		arguments = listOf(
			navArgument(Route.AddTeamSeries.ARG_TEAM) { type = NavType.StringType },
			navArgument(Route.AddTeamSeries.ARG_LEAGUES) { type = NavType.StringType },
		),
	) {
		SeriesFormRoute(
			onDismissWithResult = { _ -> onDismiss() },
			onStartTeamSeries = onStartTeamSeries,
			onEditAlley = onEditAlley,
			onEditLeague = { _, _, _ ->
				throw NotImplementedError("SeriesFormRoute should not edit League in Team Series Form")
			},
		)
	}
}
