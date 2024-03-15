package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker.StatisticPickerRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToStatisticPickerForResult(
	selectedStatistic: StatisticID,
	navResultCallback: NavResultCallback<StatisticID>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.StatisticsPicker.createRoute(selectedStatistic.name),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.statisticPickerSheet(onDismissWithResult: (StatisticID) -> Unit) {
	bottomSheet(
		route = Route.StatisticsPicker.route,
		arguments = listOf(
			navArgument(Route.StatisticsPicker.ARG_STATISTIC) {
				type = NavType.EnumType(StatisticID::class.java)
			},
		),
	) {
		StatisticPickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}
