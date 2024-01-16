package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetEditorRoute
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import java.util.UUID

fun NavController.navigateToStatisticsWidgetEditor(
	context: String,
	initialSource: StatisticsWidgetInitialSource?,
	priority: Int,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.StatisticsWidgetEditor.createRoute(
			context = context,
			initialSource = when (initialSource) {
				is StatisticsWidgetInitialSource.Bowler -> "bowler_${initialSource.bowlerId}"
				null -> null
			},
			priority = priority,
		),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.statisticsWidgetEditorScreen(
	onBackPressed: () -> Unit,
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickStatistic: (StatisticID, NavResultCallback<StatisticID>) -> Unit,
) {
	composable(
		route = Route.StatisticsWidgetEditor.route,
		arguments = listOf(
			navArgument(Route.StatisticsWidgetEditor.CONTEXT) { type = NavType.StringType },
			navArgument(Route.StatisticsWidgetEditor.INITIAL_SOURCE) { type = NavType.StringType },
			navArgument(Route.StatisticsWidgetEditor.PRIORITY) { type = NavType.IntType },
		),
	) {
		StatisticsWidgetEditorRoute(
			onBackPressed = onBackPressed,
			onPickBowler = onPickBowler,
			onPickLeague = onPickLeague,
			onPickStatistic = onPickStatistic,
		)
	}
}