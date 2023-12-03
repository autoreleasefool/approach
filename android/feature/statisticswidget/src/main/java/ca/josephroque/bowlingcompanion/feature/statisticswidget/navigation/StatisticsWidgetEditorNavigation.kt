package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetEditorRoute
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import java.util.UUID

const val CONTEXT = "context"
const val INITIAL_SOURCE = "initial_source"
const val PRIORITY = "priority"

const val statisticsWidgetEditorNavigationRoute = "statisticswidgeteditor/{$CONTEXT}/{$INITIAL_SOURCE}/{$PRIORITY}"

fun NavController.navigateToStatisticsWidgetEditor(
	context: String,
	initialSource: StatisticsWidgetInitialSource?,
	priority: Int,
) {
	val encodedContext = Uri.encode(context)
	val encodedInitialSource = initialSource?.let {
		Uri.encode(
			when (it) {
				is StatisticsWidgetInitialSource.Bowler -> "bowler_${it.bowlerId}"
			}
		)
	} ?: "nan"
	this.navigate("statisticswidgeteditor/$encodedContext/$encodedInitialSource/$priority") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.statisticsWidgetEditorScreen(
	onBackPressed: () -> Unit,
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickStatistic: (StatisticID, NavResultCallback<StatisticID>) -> Unit,
) {
	composable(
		route = statisticsWidgetEditorNavigationRoute,
		arguments = listOf(
			navArgument(CONTEXT) { type = NavType.StringType },
			navArgument(INITIAL_SOURCE) { type = NavType.StringType },
			navArgument(PRIORITY) { type = NavType.IntType },
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