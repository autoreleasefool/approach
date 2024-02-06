package ca.josephroque.bowlingcompanion.feature.laneform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneForm
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormFloatingActionButton
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormTopBar
import ca.josephroque.bowlingcompanion.feature.laneform.ui.LaneFormUiAction
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun LaneFormRoute(
	onDismissWithResult: (List<UUID>) -> Unit,
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
						is LaneFormScreenEvent.DismissedWithResult -> onDismissWithResult(it.lanes)
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
	var fabHeight by remember { mutableIntStateOf(0) }
	val fabHeightInDp = with(LocalDensity.current) { fabHeight.toDp() }

	LaunchedEffect(Unit) {
		onAction(LaneFormScreenUiAction.LoadLanes)
	}

	BackHandler {
		onAction(LaneFormScreenUiAction.LaneForm(LaneFormUiAction.BackClicked))
	}

	Scaffold(
		topBar = {
			LaneFormTopBar(
				onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		floatingActionButton = {
			LaneFormFloatingActionButton(
				onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) },
				modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height }
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			LaneFormScreenUiState.Loading -> Unit
			is LaneFormScreenUiState.Loaded -> LaneForm(
				state = state.laneForm,
				onAction = { onAction(LaneFormScreenUiAction.LaneForm(it)) },
				modifier = Modifier.padding(padding),
				contentPadding = PaddingValues(bottom = fabHeightInDp + 16.dp),
			)
		}
	}
}

