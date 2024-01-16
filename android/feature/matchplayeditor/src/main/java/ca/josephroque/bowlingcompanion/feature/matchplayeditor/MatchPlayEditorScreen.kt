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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditor
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun MatchPlayEditorRoute(
	onDismiss: () -> Unit,
	onEditOpponent: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: MatchPlayEditorViewModel = hiltViewModel(),
) {
	val matchPlayEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						MatchPlayEditorScreenEvent.Dismissed -> onDismiss()
						is MatchPlayEditorScreenEvent.EditOpponent ->
							onEditOpponent(it.opponent) { ids ->
								viewModel.handleAction(MatchPlayEditorScreenUiAction.UpdatedOpponent(ids.firstOrNull()))
							}
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
				gameIndex = when (state) {
					MatchPlayEditorScreenUiState.Loading -> 0
					is MatchPlayEditorScreenUiState.Loaded -> state.matchPlayEditor.gameIndex
			  },
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