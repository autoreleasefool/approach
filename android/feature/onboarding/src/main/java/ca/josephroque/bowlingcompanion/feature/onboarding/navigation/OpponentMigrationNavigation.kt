package ca.josephroque.bowlingcompanion.feature.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.onboarding.opponentmigration.OpponentMigrationRoute

fun NavController.navigateToOpponentMigration(navOptions: NavOptions? = null) {
	this.navigate(Route.OpponentMigration.route, navOptions)
}

fun NavGraphBuilder.opponentMigrationScreen(
	onDismiss: () -> Unit,
	onCompleteMigration: () -> Unit,
) {
	composable(
		route = Route.OpponentMigration.route,
	) {
		OpponentMigrationRoute(
			onDismiss = onDismiss,
			onCompleteMigration = onCompleteMigration,
		)
	}
}
