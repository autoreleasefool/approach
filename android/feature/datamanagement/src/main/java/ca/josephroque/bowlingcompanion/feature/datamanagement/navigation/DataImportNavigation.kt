package ca.josephroque.bowlingcompanion.feature.datamanagement.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.datamanagement.import.DataImportRoute

const val dataImportNavigationRoute = "settings/data_import"

fun NavController.navigateToDataImport(navOptions: NavOptions? = null) {
	this.navigate(dataImportNavigationRoute, navOptions)
}

fun NavGraphBuilder.dataImportScreen(
	onBackPressed: () -> Unit,
) {
	composable(
		route = dataImportNavigationRoute,
	) {
		DataImportRoute(onBackPressed = onBackPressed)
	}
}