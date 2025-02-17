package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.StatisticPickerResultViewModel
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker.StatisticPickerRoute

fun NavController.navigateToStatisticPickerForResult(selectedStatistic: StatisticID, navOptions: NavOptions? = null) {
	this.navigate(
		route = Route.StatisticsPicker.createRoute(selectedStatistic.name),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.statisticPickerSheet(navController: NavController, onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.StatisticsPicker.route,
		arguments = listOf(
			navArgument(Route.StatisticsPicker.ARG_STATISTIC) {
				type = NavType.EnumType(StatisticID::class.java)
			},
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<StatisticPickerResultViewModel>()
		} else {
			hiltViewModel<StatisticPickerResultViewModel>(parentEntry)
		}

		StatisticPickerRoute(
			onDismissWithResult = { statisticID ->
				resultViewModel.setResult(statisticID)
				onDismiss()
			},
		)
	}
}
