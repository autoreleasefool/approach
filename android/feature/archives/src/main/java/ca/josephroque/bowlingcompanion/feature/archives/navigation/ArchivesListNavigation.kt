package ca.josephroque.bowlingcompanion.feature.archives.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.archives.ArchivesListRoute

fun NavController.navigateToArchivesList(navOptions: NavOptions? = null) {
	this.navigate(Route.ArchivesList.route, navOptions)
}

fun NavGraphBuilder.archivesList(
	onBackPressed: () -> Unit,
) {
	composable(
		route = Route.ArchivesList.route,
	) {
		ArchivesListRoute(
			onBackPressed = onBackPressed,
		)
	}
}

