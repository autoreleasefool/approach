package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.Accessories
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesTopBar
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesUiState
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import java.util.UUID

@Composable
internal fun AccessoriesRoute(
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AccessoriesViewModel = hiltViewModel(),
) {
	val accessoriesState by viewModel.uiState.collectAsStateWithLifecycle()
	val gearListState by viewModel.gearListState.collectAsStateWithLifecycle()
	val alleysListState by viewModel.alleysListState.collectAsStateWithLifecycle()

	AccessoriesScreen(
		accessoriesState = accessoriesState,
		alleysListState = alleysListState,
		gearListState = gearListState,
		onAddAccessory = viewModel::expandAccessoryMenu,
		onMinimizeAddAccessoryMenu = viewModel::minimizeAccessoryMenu,
		onAddAlley = {
			viewModel.minimizeAccessoryMenu()
			onAddAlley()
		},
		onAddGear = {
			viewModel.minimizeAccessoryMenu()
			onAddGear()
		},
		onViewAllAlleys = onViewAllAlleys,
		onViewAllGear = onViewAllGear,
		onShowAlleyDetails = onShowAlleyDetails,
		onShowGearDetails = onShowGearDetails,
		modifier = modifier,
	)
}

@Composable
internal fun AccessoriesScreen(
	accessoriesState: AccessoriesUiState,
	alleysListState: AlleysListUiState?,
	gearListState: GearListUiState?,
	onAddAccessory: () -> Unit,
	onMinimizeAddAccessoryMenu: () -> Unit,
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AccessoriesTopBar(
				isAccessoryMenuExpanded = accessoriesState.isAccessoryMenuExpanded,
				onAddAccessory = onAddAccessory,
				onMinimizeAddAccessoryMenu = onMinimizeAddAccessoryMenu,
				onAddAlley = onAddAlley,
				onAddGear = onAddGear,
			)
		}
	) { padding ->
		Accessories(
			accessoriesState = accessoriesState,
			alleysListState = alleysListState,
			gearListState = gearListState,
			onViewAllAlleys = onViewAllAlleys,
			onViewAllGear = onViewAllGear,
			onShowAlleyDetails = onShowAlleyDetails,
			onShowGearDetails = onShowGearDetails,
			modifier = modifier.padding(padding),
		)
	}
}
