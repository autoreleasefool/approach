package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.navigation.popBackStackWithResult
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesOnboardingSheet
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.resourcePickerSheet

fun NavGraphBuilder.bottomSheetGraph(
	navController: NavController,
) {
	resourcePickerSheet(
		onDismissWithResult = navController::popBackStackWithResult,
	)
	accessoriesOnboardingSheet(
		onBackPressed = navController::popBackStack,
	)
}