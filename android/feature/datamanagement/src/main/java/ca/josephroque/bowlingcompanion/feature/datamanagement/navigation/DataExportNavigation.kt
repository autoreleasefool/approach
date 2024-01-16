package ca.josephroque.bowlingcompanion.feature.datamanagement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.datamanagement.export.DataExportRoute

fun NavController.navigateToDataExport(navOptions: NavOptions? = null) {
	this.navigate(Route.DataExport.route, navOptions)
}

fun NavGraphBuilder.dataExportScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = Route.DataExport.route,
	) {
		DataExportRoute(onBackPressed = onBackPressed)
	}
}