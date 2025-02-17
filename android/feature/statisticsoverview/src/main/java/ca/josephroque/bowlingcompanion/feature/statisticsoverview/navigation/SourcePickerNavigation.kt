package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker.SourcePickerRoute

fun NavController.navigateToStatisticsSourcePicker(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsSourcePicker.route, navOptions)
}

fun NavGraphBuilder.statisticsSourcePickerSheet(
	onBackPressed: () -> Unit,
	onPickTeam: (TeamID?, ResourcePickerResultKey) -> Unit,
	onPickBowler: (BowlerID?, ResourcePickerResultKey) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, ResourcePickerResultKey) -> Unit,
	onPickSeries: (LeagueID, SeriesID?, ResourcePickerResultKey) -> Unit,
	onPickGame: (SeriesID, GameID?, ResourcePickerResultKey) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
) {
	bottomSheet(
		route = Route.StatisticsSourcePicker.route,
	) {
		SourcePickerRoute(
			onDismiss = onBackPressed,
			onPickTeam = onPickTeam,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onPickSeries = onPickSeries,
			onPickGame = onPickGame,
			onShowStatistics = onShowStatistics,
		)
	}
}
