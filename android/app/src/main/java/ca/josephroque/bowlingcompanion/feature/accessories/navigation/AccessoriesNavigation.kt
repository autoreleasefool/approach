package ca.josephroque.bowlingcompanion.feature.accessories.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.accessories.AccessoriesRoute

const val accessoriesNavigationRoute = "accessories"

fun NavController.navigateToAccessories(navOptions: NavOptions? = null) {
	this.navigate(accessoriesNavigationRoute, navOptions)
}

fun NavGraphBuilder.accessoriesScreen() {
	composable(
		route = accessoriesNavigationRoute,
	) {
		AccessoriesRoute()
	}
}