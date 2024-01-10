package ca.josephroque.bowlingcompanion.feature.datamanagement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.datamanagement.export.DataExportRoute

const val dataExportNavigationRoute = "settings/data_export"

fun NavController.navigateToDataExport(navOptions: NavOptions? = null) {
	this.navigate(dataExportNavigationRoute, navOptions)
}

fun NavGraphBuilder.dataExportScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = dataExportNavigationRoute,
	) {
		DataExportRoute(onBackPressed = onBackPressed)
	}
}