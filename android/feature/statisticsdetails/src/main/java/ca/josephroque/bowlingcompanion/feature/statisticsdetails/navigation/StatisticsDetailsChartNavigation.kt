package ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartRoute
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.sourceType
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToStatisticsDetailsChart(
	filter: TrackableFilter,
	statistic: StatisticID,
	navOptions: NavOptions? = null,
) {
	val type = filter.source.sourceType()
	val id = filter.source.id
	// FIXME: Parse and pass the rest of the filter as arguments
	navigateToStatisticsDetailsChart(type, id, statistic, navOptions)
}

fun NavController.navigateToStatisticsDetailsChart(
	sourceType: StatisticsDetailsSourceType,
	sourceId: UUID,
	statistic: StatisticID,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		Route.StatisticsDetailsChart.createRoute(sourceType.name, sourceId, statistic),
		navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.statisticsDetailsChartSheet(onBackPressed: () -> Unit) {
	bottomSheet(
		route = Route.StatisticsDetailsChart.route,
		arguments = listOf(
			navArgument(Route.StatisticsDetailsChart.ARG_SOURCE_TYPE) {
				type = NavType.EnumType(StatisticsDetailsSourceType::class.java)
			},
			navArgument(Route.StatisticsDetailsChart.ARG_SOURCE_ID) { type = NavType.StringType },
			navArgument(Route.StatisticsDetailsChart.ARG_STATISTIC_ID) {
				type = NavType.EnumType(StatisticID::class.java)
			},
		),
	) {
		StatisticsDetailsChartRoute(onBackPressed = onBackPressed)
	}
}
