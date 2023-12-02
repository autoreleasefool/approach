package ca.josephroque.bowlingcompanion.feature.gearlist

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearList
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBar
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListTopBarUiState
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun GearListRoute(
	onBackPressed: () -> Unit,
	onEditGear: (UUID) -> Unit,
	onAddGear: () -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GearListViewModel = hiltViewModel(),
) {
	val gearListScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						GearListScreenEvent.Dismissed -> onBackPressed()
						GearListScreenEvent.NavigateToAddGear -> onAddGear()
						is GearListScreenEvent.NavigateToEditGear -> onEditGear(it.id)
					}
				}
		}
	}

	GearListScreen(
		state = gearListScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GearListScreen(
	state: GearListScreenUiState,
	onAction: (GearListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			GearListTopBar(
				state = when (state) {
					GearListScreenUiState.Loading -> GearListTopBarUiState()
					is GearListScreenUiState.Loaded -> state.topBar
			  },
				onAction = { onAction(GearListScreenUiAction.GearListAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			GearListScreenUiState.Loading -> Unit
			is GearListScreenUiState.Loaded ->
				GearList(
					state = state.gearList,
					onAction = { onAction(GearListScreenUiAction.GearListAction(it)) },
					modifier = Modifier.padding(padding),
				)
		}
	}
}