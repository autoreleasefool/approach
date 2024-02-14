package ca.josephroque.bowlingcompanion.navigation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.accessoriesScreen
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.navigation.navigateToAccessoriesOnboarding
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.alleyFormScreen
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.navigateToAlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.navigateToNewAlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.alleysListScreen
import ca.josephroque.bowlingcompanion.feature.alleyslist.navigation.navigateToAlleysList
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.navigateToAvatarFormForResult
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.gearFormScreen
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.navigateToGearForm
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.navigateToNewGearForm
import ca.josephroque.bowlingcompanion.feature.gearlist.navigation.gearListScreen
import ca.josephroque.bowlingcompanion.feature.gearlist.navigation.navigateToGearList
import ca.josephroque.bowlingcompanion.feature.laneform.navigation.navigateToLaneFormForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation.navigateToResourcePickerForResult

fun NavGraphBuilder.accessoriesGraph(navController: NavController) {
	accessoriesScreen(
		onAddAlley = navController::navigateToNewAlleyForm,
		onAddGear = navController::navigateToNewGearForm,
		onViewAllAlleys = navController::navigateToAlleysList,
		onViewAllGear = navController::navigateToGearList,
		onShowAlleyDetails = { /* FIXME: onShowAlleyDetails */ },
		onShowGearDetails = { /* FIXME: onShowGearDetails */ },
		onShowAccessoriesOnboarding = navController::navigateToAccessoriesOnboarding,
	)
	alleysListScreen(
		onBackPressed = navController::popBackStack,
		onEditAlley = navController::navigateToAlleyForm,
		onAddAlley = navController::navigateToNewAlleyForm,
		onShowAlleyDetails = { /* FIXME: onShowAlleyDetails */ },
	)
	alleyFormScreen(
		onBackPressed = navController::popBackStack,
		onManageLanes = { lanes, result ->
			navController.navigateToLaneFormForResult(
				existingLanes = lanes,
				navResultCallback = result,
			)
		},
	)
	gearListScreen(
		onBackPressed = navController::popBackStack,
		onEditGear = navController::navigateToGearForm,
		onAddGear = navController::navigateToNewGearForm,
		onShowGearDetails = { /* FIXME: onShowGearDetails */ },
	)
	gearFormScreen(
		onBackPressed = navController::popBackStack,
		onEditAvatar = navController::navigateToAvatarFormForResult,
		onEditOwner = { owner, result ->
			navController.navigateToResourcePickerForResult(
				selectedIds = owner?.let { setOf(it) } ?: emptySet(),
				limit = 1,
				navResultCallback = result,
				resourceType = ResourcePickerType.BOWLER,
			)
		},
	)
}
