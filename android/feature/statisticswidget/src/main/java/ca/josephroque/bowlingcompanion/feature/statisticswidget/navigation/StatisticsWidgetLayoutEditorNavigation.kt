package ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.layout.StatisticsWidgetLayoutEditorRoute

const val statisticsWidgetLayoutEditorRoute = "statisticswidgetlayouteditor/{$CONTEXT}/{$INITIAL_SOURCE}"

fun NavController.navigateToStatisticsWidgetLayoutEditor(
	context: String,
	initialSource: StatisticsWidgetInitialSource?,
) {
	val encodedContext = Uri.encode(context)
	val encodedInitialSource = initialSource?.let {
		Uri.encode(
			when (it) {
				is StatisticsWidgetInitialSource.Bowler -> "bowler_${it.bowlerId}"
			}
		)
	} ?: "nan"
	this.navigate("statisticswidgetlayouteditor/$encodedContext/$encodedInitialSource")
}

fun NavGraphBuilder.statisticsWidgetLayoutEditorScreen(
	onBackPressed: () -> Unit,
	onAddWidget: (String, StatisticsWidgetInitialSource?, Int) -> Unit,
) {
	composable(
		route = statisticsWidgetLayoutEditorRoute,
		arguments = listOf(
			navArgument(CONTEXT) { type = NavType.StringType },
			navArgument(INITIAL_SOURCE) { type = NavType.StringType },
		),
	) {
		StatisticsWidgetLayoutEditorRoute(
			onBackPressed = onBackPressed,
			onAddWidget = onAddWidget,
		)
	}
}