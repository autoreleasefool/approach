package ca.josephroque.bowlingcompanion.feature.gameseditor

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
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresList
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListTopBar
import kotlinx.coroutines.launch

@Composable
internal fun ScoresListRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: ScoresListViewModel = hiltViewModel(),
) {
	val scoresListScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						ScoresListScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	ScoresListScreen(
		state = scoresListScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScoresListScreen(
	state: ScoresListScreenUiState,
	onAction: (ScoresListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			ScoresListTopBar(
				gameIndex = (state as? ScoresListScreenUiState.Loaded)?.scoresList?.gameIndex ?: 0,
				onAction = { onAction(ScoresListScreenUiAction.ScoresList(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			ScoresListScreenUiState.Loading -> Unit
			is ScoresListScreenUiState.Loaded -> ScoresList(
				state = state.scoresList,
				onAction = { onAction(ScoresListScreenUiAction.ScoresList(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
