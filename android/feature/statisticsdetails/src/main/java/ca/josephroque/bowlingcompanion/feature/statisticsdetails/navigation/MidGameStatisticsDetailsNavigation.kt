package ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.MidGameStatisticsDetailsRoute
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.sourceType
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToMidGameStatisticsDetails(
	filter: TrackableFilter,
	navOptions: NavOptions? = null,
) {
	val type = filter.source.sourceType()
	val id = filter.source.id
	// FIXME: Parse and pass the rest of the filter as arguments
	navigateToMidGameStatisticsDetails(type, id, navOptions)
}

fun NavController.navigateToMidGameStatisticsDetails(
	sourceType: StatisticsDetailsSourceType,
	sourceId: UUID,
	navOptions: NavOptions? = null,
) {
	this.navigate(Route.MidGameStatisticsDetails.createRoute(sourceType.name, sourceId), navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.midGameStatisticsDetailsScreen(onBackPressed: () -> Unit) {
	bottomSheet(
		route = Route.MidGameStatisticsDetails.route,
		arguments = listOf(
			navArgument(Route.MidGameStatisticsDetails.ARG_SOURCE_TYPE) {
				type = NavType.EnumType(StatisticsDetailsSourceType::class.java)
			},
			navArgument(Route.MidGameStatisticsDetails.ARG_SOURCE_ID) { type = NavType.StringType },
		),
	) {
		MidGameStatisticsDetailsRoute(onBackPressed = onBackPressed)
	}
}
