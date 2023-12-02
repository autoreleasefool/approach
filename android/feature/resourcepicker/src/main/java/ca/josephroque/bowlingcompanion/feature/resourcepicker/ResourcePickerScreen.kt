package ca.josephroque.bowlingcompanion.feature.resourcepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import ca.josephroque.bowlingcompanion.core.model.ui.GearRow
import ca.josephroque.bowlingcompanion.core.model.ui.LeagueRow
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePicker
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBar
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourcePickerTopBarUiState
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun ResourcePickerRoute(
	onDismissWithResult: (Set<UUID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: ResourcePickerViewModel = hiltViewModel(),
) {
	val resourcePickerScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is ResourcePickerScreenEvent.Dismissed -> onDismissWithResult(it.result)
					}
				}
		}
	}

	ResourcePickerScreen(
		state = resourcePickerScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResourcePickerScreen(
	state: ResourcePickerScreenUiState,
	onAction: (ResourcePickerScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(ResourcePickerScreenUiAction.LoadResources)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			ResourcePickerTopBar(
				state = when (state) {
					ResourcePickerScreenUiState.Loading -> ResourcePickerTopBarUiState()
					is ResourcePickerScreenUiState.Loaded -> state.topBar
			  },
				onAction = { onAction(ResourcePickerScreenUiAction.ResourcePickerAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			ResourcePickerScreenUiState.Loading -> Unit
			is ResourcePickerScreenUiState.Loaded ->
				ResourcePicker(
					state = state.picker,
					onAction = { onAction(ResourcePickerScreenUiAction.ResourcePickerAction(it)) },
					itemContent = {
						when (it) {
							is ResourceItem.Bowler -> BowlerRow(name = it.name)
							is ResourceItem.League -> LeagueRow(name = it.name)
							is ResourceItem.Gear -> GearRow(
								name = it.name,
								ownerName = it.ownerName,
								kind = it.kind,
								avatar = it.avatar,
							)
						}
					},
					modifier = Modifier.padding(padding),
				)
		}
	}
}