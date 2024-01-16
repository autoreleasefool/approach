package ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.StatisticsDetailsRoute
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.sourceType
import java.util.UUID

fun NavController.navigateToStatisticsDetails(filter: TrackableFilter, navOptions: NavOptions? = null) {
	val type = filter.source.sourceType()
	val id = filter.source.id
	// FIXME: Parse and pass the rest of the filter as arguments
	navigateToStatisticsDetails(type, id, navOptions)
}

fun NavController.navigateToStatisticsDetails(sourceType: StatisticsDetailsSourceType, sourceId: UUID, navOptions: NavOptions? = null) {
	this.navigate(Route.StatisticsDetails.createRoute(sourceType.name, sourceId), navOptions)
}

fun NavGraphBuilder.statisticsDetailsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = Route.StatisticsDetails.route,
		arguments = listOf(
			navArgument(Route.StatisticsDetails.ARG_SOURCE_TYPE) { type = NavType.EnumType(StatisticsDetailsSourceType::class.java) },
			navArgument(Route.StatisticsDetails.ARG_SOURCE_ID) { type = NavType.StringType },
		),
	) {
		StatisticsDetailsRoute(onBackPressed = onBackPressed)
	}
}