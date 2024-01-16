package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker.StatisticPickerRoute

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

fun NavGraphBuilder.statisticPickerScreen(
	onDismissWithResult: (StatisticID) -> Unit,
) {
	composable(
		route = Route.StatisticsPicker.route,
		arguments = listOf(
			navArgument(Route.StatisticsPicker.ARG_STATISTIC) { type = NavType.EnumType(StatisticID::class.java) },
		),
	) {
		StatisticPickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}

