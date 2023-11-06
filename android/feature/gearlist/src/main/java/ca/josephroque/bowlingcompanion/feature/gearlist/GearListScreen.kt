package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearList
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBar
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.gearList
import java.util.UUID

@Composable
internal fun GearListRoute(
	onBackPressed: () -> Unit,
	onEditGear: (UUID) -> Unit,
	onAddGear: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GearListViewModel = hiltViewModel(),
) {
	val gearListState by viewModel.gearListState.collectAsStateWithLifecycle()
	val gearListTopBarState by viewModel.gearListTopBarState.collectAsStateWithLifecycle()

	GearListScreen(
		gearListTopBarState = gearListTopBarState,
		gearListState = gearListState,
		onBackPressed = onBackPressed,
		onAddGear = onAddGear,
		onGearClick = onEditGear,
		onGearFilterChanged = viewModel::updateGearFilter,
		onShowGearFilter = viewModel::showGearFilter,
		onMinimizeGearFilter = viewModel::hideGearFilter,
		modifier = modifier,
	)
}

@Composable
private fun GearListScreen(
	gearListTopBarState: GearListTopBarState,
	gearListState: GearListUiState,
	onBackPressed: () -> Unit,
	onGearClick: (UUID) -> Unit,
	onAddGear: () -> Unit,
	onGearFilterChanged: (GearKind?) -> Unit,
	onShowGearFilter: () -> Unit,
	onMinimizeGearFilter: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			GearListTopBar(
				state = gearListTopBarState,
				onBackPressed = onBackPressed,
				onAddGear = onAddGear,
				onGearFilterChanged = onGearFilterChanged,
				onMinimizeGearFilter = onMinimizeGearFilter,
				onShowGearFilter = onShowGearFilter,
			)
		}
	) { padding ->
		GearList(
			state = gearListState,
			onGearClick = onGearClick,
			onAddGear = onAddGear,
			modifier = modifier
				.fillMaxSize()
				.padding(padding),
		)
	}
}