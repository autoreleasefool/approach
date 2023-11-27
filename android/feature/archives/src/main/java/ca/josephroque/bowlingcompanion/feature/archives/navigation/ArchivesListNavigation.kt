package ca.josephroque.bowlingcompanion.feature.archives.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.feature.archives.ArchivesListRoute

const val archivesListNavigationRoute = "archives"

fun NavController.navigateToArchivesList(navOptions: NavOptions? = null) {
	this.navigate(archivesListNavigationRoute, navOptions)
}

fun NavGraphBuilder.archivesList(
	onBackPressed: () -> Unit,
) {
	composable(
		route = archivesListNavigationRoute,
	) {
		ArchivesListRoute(
			onBackPressed = onBackPressed,
		)
	}
}

