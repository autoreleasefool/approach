package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.AccessoriesRoute
import java.util.UUID

fun NavGraphBuilder.accessoriesScreen(
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (AlleyID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowAccessoriesOnboarding: () -> Unit,
) {
	composable(
		route = Route.AccessoriesOverview.route,
	) {
		AccessoriesRoute(
			onAddAlley = onAddAlley,
			onAddGear = onAddGear,
			onViewAllAlleys = onViewAllAlleys,
			onViewAllGear = onViewAllGear,
			onShowAlleyDetails = onShowAlleyDetails,
			onShowGearDetails = onShowGearDetails,
			onShowAccessoriesOnboarding = onShowAccessoriesOnboarding,
		)
	}
}
