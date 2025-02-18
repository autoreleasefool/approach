@file:Suppress("UsingMaterialAndMaterial3Libraries")

package ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation

import android.net.Uri
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ResourcePickerRoute
import java.util.UUID

fun NavController.navigateToTeamPickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<TeamID>,
	hiddenIds: Set<TeamID> = emptySet(),
	limit: Int = 0,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		resourceType = ResourcePickerType.TEAM,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToBowlerPickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<BowlerID>,
	hiddenIds: Set<BowlerID> = emptySet(),
	limit: Int = 0,
	kind: BowlerKind? = null,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = kind?.name,
		resourceType = ResourcePickerType.BOWLER,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToLeaguePickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<LeagueID>,
	hiddenIds: Set<LeagueID> = emptySet(),
	limit: Int = 0,
	bowlerId: BowlerID? = null,
	recurrence: LeagueRecurrence? = null,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = if (bowlerId != null && recurrence != null) {
			"$bowlerId:$recurrence"
		} else {
			bowlerId?.value?.toString()
				?: recurrence?.toString()
		},
		resourceType = ResourcePickerType.LEAGUE,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToSeriesPickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<SeriesID>,
	hiddenIds: Set<SeriesID> = emptySet(),
	limit: Int = 0,
	leagueId: LeagueID? = null,
	preBowl: SeriesPreBowl? = null,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = if (leagueId != null && preBowl != null) {
			"$leagueId:$preBowl"
		} else {
			leagueId?.value?.toString()
				?: preBowl?.toString()
		},
		resourceType = ResourcePickerType.SERIES,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToGamePickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<GameID>,
	hiddenIds: Set<GameID> = emptySet(),
	limit: Int = 0,
	seriesId: SeriesID? = null,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = seriesId?.value?.toString(),
		resourceType = ResourcePickerType.GAME,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToAlleyPickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<AlleyID>,
	hiddenIds: Set<AlleyID> = emptySet(),
	limit: Int = 0,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		resourceType = ResourcePickerType.ALLEY,
		resultKey = resultKey,
		navOptions = navOptions,
	)
}

fun NavController.navigateToGearPickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<GearID>,
	hiddenIds: Set<GearID> = emptySet(),
	limit: Int = 0,
	kind: GearKind? = null,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		resultKey = resultKey,
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = kind?.name,
		resourceType = ResourcePickerType.GEAR,
		navOptions = navOptions,
	)
}

fun NavController.navigateToLanePickerForResult(
	resultKey: ResourcePickerResultKey,
	alleyId: AlleyID,
	selectedIds: Set<LaneID>,
	hiddenIds: Set<LaneID> = emptySet(),
	limit: Int = 0,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		resultKey = resultKey,
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = alleyId.toString(),
		resourceType = ResourcePickerType.LANE,
		navOptions = navOptions,
	)
}

fun NavController.navigateToResourcePickerForResult(
	resultKey: ResourcePickerResultKey,
	selectedIds: Set<UUID>,
	hiddenIds: Set<UUID> = emptySet(),
	resourceType: ResourcePickerType,
	filter: String? = null,
	titleOverride: String? = null,
	limit: Int = 0,
	navOptions: NavOptions? = null,
) {
	this.navigate(
		route = Route.ResourcePicker.createRoute(
			resultKey = resultKey.value,
			resourceType.toString(),
			filter,
			selectedIds,
			hiddenIds,
			limit,
			Uri.encode(titleOverride),
		),
		navOptions = navOptions,
	)
}

fun NavGraphBuilder.resourcePickerSheet(navController: NavController, onDismiss: () -> Unit) {
	bottomSheet(
		route = Route.ResourcePicker.route,
		arguments = listOf(
			navArgument(Route.ResourcePicker.RESULT_KEY) {
				type = NavType.StringType
				nullable = true
			},
			navArgument(Route.ResourcePicker.RESOURCE_TYPE) {
				type = NavType.EnumType(ResourcePickerType::class.java)
			},
			navArgument(Route.ResourcePicker.RESOURCE_FILTER) {
				type = NavType.StringType
				nullable = true
			},
			navArgument(Route.ResourcePicker.SELECTED_IDS) { type = NavType.StringType },
			navArgument(Route.ResourcePicker.HIDDEN_IDS) { type = NavType.StringType },
			navArgument(Route.ResourcePicker.SELECTION_LIMIT) { type = NavType.IntType },
			navArgument(Route.ResourcePicker.TITLE_OVERRIDE) {
				type = NavType.StringType
				nullable = true
			},
		),
	) {
		val parentEntry = remember(it) {
			navController.previousBackStackEntry
		}

		val resultViewModel = if (parentEntry == null) {
			hiltViewModel<ResourcePickerResultViewModel>()
		} else {
			hiltViewModel<ResourcePickerResultViewModel>(parentEntry)
		}

		ResourcePickerRoute(
			onDismissWithResult = { resultKey, ids ->
				resultViewModel.setSelectedIds(resultKey, ids)
				onDismiss()
			},
		)
	}
}
