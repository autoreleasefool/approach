package ca.josephroque.bowlingcompanion.feature.statisticsoverview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker.SourcePickerRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToStatisticsSourcePicker(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsSourcePicker.route, navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.statisticsSourcePickerSheet(
	onBackPressed: () -> Unit,
	onPickBowler: (BowlerID?, NavResultCallback<Set<BowlerID>>) -> Unit,
	onPickLeague: (BowlerID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickSeries: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickGame: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
) {
	bottomSheet(
		route = Route.StatisticsSourcePicker.route,
	) {
		SourcePickerRoute(
			onDismiss = onBackPressed,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onPickSeries = onPickSeries,
			onPickGame = onPickGame,
			onShowStatistics = onShowStatistics,
		)
	}
}
