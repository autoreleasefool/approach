package ca.josephroque.bowlingcompanion.feature.accessoriesoverview

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
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.Accessories
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.AccessoriesTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun AccessoriesRoute(
	onAddAlley: () -> Unit,
	onAddGear: () -> Unit,
	onViewAllAlleys: () -> Unit,
	onViewAllGear: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	onShowGearDetails: (UUID) -> Unit,
	onShowAccessoriesOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AccessoriesViewModel = hiltViewModel(),
) {
	val accessoriesScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AccessoriesScreenUiEvent.AddAlley -> onAddAlley()
						AccessoriesScreenUiEvent.AddGear -> onAddGear()
						AccessoriesScreenUiEvent.ViewAllAlleys -> onViewAllAlleys()
						AccessoriesScreenUiEvent.ViewAllGear -> onViewAllGear()
						is AccessoriesScreenUiEvent.ShowAlleyDetails -> onShowAlleyDetails(it.alleyId)
						is AccessoriesScreenUiEvent.ShowGearDetails -> onShowGearDetails(it.gearId)
						AccessoriesScreenUiEvent.ShowAccessoriesOnboarding -> onShowAccessoriesOnboarding()
					}
				}
		}
	}

	AccessoriesScreen(
		state = accessoriesScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AccessoriesScreen(
	state: AccessoriesScreenUiState,
	onAction: (AccessoriesScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(AccessoriesScreenUiAction.DidAppear)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	Scaffold(
		topBar = {
			AccessoriesTopBar(
				isAccessoryMenuExpanded = when (state) {
					AccessoriesScreenUiState.Loading -> false
					is AccessoriesScreenUiState.Loaded -> state.accessories.isAccessoryMenuExpanded
				},
				onAction = { onAction(AccessoriesScreenUiAction.Accessories(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			AccessoriesScreenUiState.Loading -> Unit
			is AccessoriesScreenUiState.Loaded -> Accessories(
				state = state.accessories,
				onAction = { onAction(AccessoriesScreenUiAction.Accessories(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
