package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticswidget.error.StatisticsWidgetErrorRoute

fun NavController.navigateToStatisticsWidgetError(navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsWidgetError.route, navOptions)
}

fun NavGraphBuilder.statisticsWidgetError(onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.StatisticsWidgetError.route,
	) {
		StatisticsWidgetErrorRoute(
			onDismissed = onDismiss,
		)
	}
}
