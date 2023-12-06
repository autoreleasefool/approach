package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.compose.foundation.layout.Box
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
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneForm
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormFloatingActionButton
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormTopBar
import kotlinx.coroutines.launch

@Composable
internal fun LaneFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LaneFormViewModel = hiltViewModel(),
) {
	val laneFormScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						LaneFormScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	LaneFormScreen(
		state = laneFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaneFormScreen(
	state: LaneFormScreenUiState,
	onAction: (LaneFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	LaunchedEffect(Unit) {
		onAction(LaneFormScreenUiAction.LoadLanes)
	}

	Scaffold(
		topBar = {
			LaneFormTopBar(
				onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			LaneFormFloatingActionButton(onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) })
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		Box(
			modifier = Modifier.padding(padding),
		) {
			when (state) {
				LaneFormScreenUiState.Loading -> Unit
				is LaneFormScreenUiState.Loaded -> LaneForm(
					state = state.laneForm,
					onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) },
				)
			}
		}
	}
}

