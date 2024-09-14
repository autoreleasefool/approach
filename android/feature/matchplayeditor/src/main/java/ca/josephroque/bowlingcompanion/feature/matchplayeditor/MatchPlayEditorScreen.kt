package ca.josephroque.bowlingcompanion.feature.matchplayeditor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditor
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorTopBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun MatchPlayEditorRoute(
	onDismiss: () -> Unit,
	onEditOpponent: (BowlerID?, ResourcePickerResultKey) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: MatchPlayEditorViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
) {
	val matchPlayEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(MATCH_PLAY_OPPONENT_RESULT_KEY) { BowlerID(it) }
			.onEach {
				viewModel.handleAction(MatchPlayEditorScreenUiAction.UpdatedOpponent(it.firstOrNull()))
			}
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						MatchPlayEditorScreenEvent.Dismissed -> onDismiss()
						is MatchPlayEditorScreenEvent.EditOpponent ->
							onEditOpponent(it.opponent, MATCH_PLAY_OPPONENT_RESULT_KEY)
					}
				}
		}
	}

	MatchPlayEditorScreen(
		state = matchPlayEditorScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchPlayEditorScreen(
	state: MatchPlayEditorScreenUiState,
	onAction: (MatchPlayEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(MatchPlayEditorScreenUiAction.LoadMatchPlay)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			MatchPlayEditorTopBar(
				onAction = { onAction(MatchPlayEditorScreenUiAction.MatchPlayEditor(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			MatchPlayEditorScreenUiState.Loading -> Unit
			is MatchPlayEditorScreenUiState.Loaded -> MatchPlayEditor(
				state = state.matchPlayEditor,
				onAction = { onAction(MatchPlayEditorScreenUiAction.MatchPlayEditor(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
