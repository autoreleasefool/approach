package ca.josephroque.bowlingcompanion.feature.datamanagement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.datamanagement.dataimport.DataImportRoute

fun NavController.navigateToDataImport(navOptions: NavOptions? = null) {
	this.navigate(Route.DataImport.route, navOptions)
}

fun NavGraphBuilder.dataImportScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = Route.DataImport.route,
	) {
		DataImportRoute(onBackPressed = onBackPressed)
	}
}