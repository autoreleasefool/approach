package ca.josephroque.bowlingcompanion.feature.resourcepicker.navigation

import android.net.Uri
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
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.navigation.navigateForResult
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ResourcePickerRoute
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import java.util.UUID

fun NavController.navigateToTeamPickerForResult(
	selectedIds: Set<TeamID>,
	hiddenIds: Set<TeamID> = emptySet(),
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<TeamID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		resourceType = ResourcePickerType.TEAM,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { TeamID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToBowlerPickerForResult(
	selectedIds: Set<BowlerID>,
	hiddenIds: Set<BowlerID> = emptySet(),
	limit: Int = 0,
	kind: BowlerKind? = null,
	navResultCallback: NavResultCallback<Set<BowlerID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = kind?.name,
		resourceType = ResourcePickerType.BOWLER,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { BowlerID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToLeaguePickerForResult(
	selectedIds: Set<LeagueID>,
	hiddenIds: Set<LeagueID> = emptySet(),
	limit: Int = 0,
	bowlerId: BowlerID? = null,
	recurrence: LeagueRecurrence? = null,
	navResultCallback: NavResultCallback<Set<LeagueID>>,
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
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { LeagueID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToSeriesPickerForResult(
	selectedIds: Set<SeriesID>,
	hiddenIds: Set<SeriesID> = emptySet(),
	limit: Int = 0,
	leagueId: LeagueID? = null,
	preBowl: SeriesPreBowl? = null,
	navResultCallback: NavResultCallback<Set<SeriesID>>,
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
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { SeriesID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToGamePickerForResult(
	selectedIds: Set<GameID>,
	hiddenIds: Set<GameID> = emptySet(),
	limit: Int = 0,
	seriesId: SeriesID? = null,
	navResultCallback: NavResultCallback<Set<GameID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = seriesId?.value?.toString(),
		resourceType = ResourcePickerType.GAME,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { GameID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToAlleyPickerForResult(
	selectedIds: Set<AlleyID>,
	hiddenIds: Set<AlleyID> = emptySet(),
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<AlleyID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		resourceType = ResourcePickerType.ALLEY,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { AlleyID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToGearPickerForResult(
	resultKey: String,
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

fun NavController.navigateToGearPickerForResult(
	selectedIds: Set<GearID>,
	hiddenIds: Set<GearID> = emptySet(),
	limit: Int = 0,
	kind: GearKind? = null,
	navResultCallback: NavResultCallback<Set<GearID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = kind?.name,
		resourceType = ResourcePickerType.GEAR,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { GearID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToLanePickerForResult(
	alleyId: AlleyID,
	selectedIds: Set<LaneID>,
	hiddenIds: Set<LaneID> = emptySet(),
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<LaneID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateToResourcePickerForResult(
		selectedIds = selectedIds.map { it.value }.toSet(),
		hiddenIds = hiddenIds.map { it.value }.toSet(),
		limit = limit,
		filter = alleyId.toString(),
		resourceType = ResourcePickerType.LANE,
		navResultCallback = @JvmSerializableLambda { ids ->
			navResultCallback(ids.map { LaneID(it) }.toSet())
		},
		navOptions = navOptions,
	)
}

fun NavController.navigateToResourcePickerForResult(
	resultKey: String,
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
			resultKey = resultKey,
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

fun NavController.navigateToResourcePickerForResult(
	selectedIds: Set<UUID>,
	hiddenIds: Set<UUID> = emptySet(),
	resourceType: ResourcePickerType,
	filter: String? = null,
	titleOverride: String? = null,
	limit: Int = 0,
	navResultCallback: NavResultCallback<Set<UUID>>,
	navOptions: NavOptions? = null,
) {
	this.navigateForResult(
		route = Route.ResourcePicker.createRoute(
			resultKey = null,
			resourceType.toString(),
			filter,
			selectedIds,
			hiddenIds,
			limit,
			Uri.encode(titleOverride),
		),
		navResultCallback = navResultCallback,
		navOptions = navOptions,
	)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.resourcePickerSheet(
	navController: NavController,
	onDismiss: () -> Unit,
	onDismissWithResult: (Set<UUID>) -> Unit,
) {
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
				if (resultKey == null) {
					onDismissWithResult(ids)
				} else {
					resultViewModel.setSelectedIds(resultKey, ids)
					onDismiss()
				}
			},
		)
	}
}
