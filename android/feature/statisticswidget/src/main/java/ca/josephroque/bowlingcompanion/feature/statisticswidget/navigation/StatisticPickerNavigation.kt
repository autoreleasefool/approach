package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.common.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker.StatisticPickerRoute

const val SELECTED_STATISTIC = "selected_statistic"

const val statisticPickerNavigationRoute = "statistic_picker/{$SELECTED_STATISTIC}"

fun NavController.navigateToStatisticPickerForResult(
	selectedStatistic: StatisticID,
	navResultCallback: NavResultCallback<StatisticID>,
) {
	this.navigateForResult(
		"statistic_picker/$selectedStatistic",
		navResultCallback,
	) {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.statisticPickerScreen(
	onDismissWithResult: (StatisticID) -> Unit,
) {
	composable(
		route = statisticPickerNavigationRoute,
		arguments = listOf(
			navArgument(SELECTED_STATISTIC) { type = NavType.EnumType(StatisticID::class.java) },
		),
	) {
		StatisticPickerRoute(
			onDismissWithResult = onDismissWithResult,
		)
	}
}

