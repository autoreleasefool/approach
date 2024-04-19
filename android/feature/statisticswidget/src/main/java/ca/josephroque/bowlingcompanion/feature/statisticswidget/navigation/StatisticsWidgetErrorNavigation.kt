package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticswidget.error.StatisticsWidgetErrorRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

fun NavController.navigateToStatisticsWidgetError(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsWidgetError.route, navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.statisticsWidgetError(onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.StatisticsWidgetError.route,
	) {
		StatisticsWidgetErrorRoute(
			onDismissed = onDismiss,
		)
	}
}
