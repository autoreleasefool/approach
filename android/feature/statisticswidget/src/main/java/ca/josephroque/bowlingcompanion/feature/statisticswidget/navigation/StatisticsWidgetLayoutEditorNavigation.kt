package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.layout.StatisticsWidgetLayoutEditorRoute

fun NavController.navigateToStatisticsWidgetLayoutEditor(
	context: String,
	initialSource: StatisticsWidgetInitialSource?,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.StatisticsWidgetLayoutEditor.createRoute(
			context = context,
			initialSource = when (initialSource) {
				is StatisticsWidgetInitialSource.Bowler -> "bowler_${initialSource.bowlerId}"
				null -> null
			},
		),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.statisticsWidgetLayoutEditorScreen(
	onBackPressed: () -> Unit,
	onAddWidget: (String, StatisticsWidgetInitialSource?, Int) -> Unit,
) {
	composable(
		route = Route.StatisticsWidgetLayoutEditor.route,
		arguments = listOf(
			navArgument(Route.StatisticsWidgetLayoutEditor.CONTEXT) { type = NavType.StringType },
			navArgument(Route.StatisticsWidgetLayoutEditor.INITIAL_SOURCE) { type = NavType.StringType },
		),
	) {
		StatisticsWidgetLayoutEditorRoute(
			onBackPressed = onBackPressed,
			onAddWidget = onAddWidget,
		)
	}
}