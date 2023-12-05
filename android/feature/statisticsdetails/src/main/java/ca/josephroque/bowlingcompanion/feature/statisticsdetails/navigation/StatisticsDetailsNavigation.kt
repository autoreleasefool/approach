package ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.SourceType
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.StatisticsDetailsRoute
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.sourceType
import java.util.UUID

const val SOURCE_TYPE = "source_type"
const val SOURCE_ID = "source_id"
const val statisticsDetailsNavigationRoute = "statistics_details/{$SOURCE_TYPE}/{$SOURCE_ID}"

fun NavController.navigateToStatisticsDetails(filter: TrackableFilter) {
	val type = filter.source.sourceType()
	val id = filter.source.id
	// TODO: Parse and pass the rest of the filter as arguments
	navigateToStatisticsDetails(type, id)
}

fun NavController.navigateToStatisticsDetails(sourceType: SourceType, sourceId: UUID) {
	val encodedId = Uri.encode(sourceId.toString())
	this.navigate("statistics_details/$sourceType/$encodedId") {
		launchSingleTop = true
	}
}

fun NavGraphBuilder.statisticsDetailsScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = statisticsDetailsNavigationRoute,
		arguments = listOf(
			navArgument(SOURCE_TYPE) { type = NavType.EnumType(SourceType::class.java) },
			navArgument(SOURCE_ID) { type = NavType.StringType },
		),
	) {
		StatisticsDetailsRoute(onBackPressed = onBackPressed)
	}
}