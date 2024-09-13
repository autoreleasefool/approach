package ca.josephroque.bowlingcompanion.feature.gameseditor.scoreeditor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorTopBar
import kotlinx.coroutines.launch

@Composable
internal fun ScoreEditorRoute(
	onDismissWithResult: (Pair<GameScoringMethod, Int>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: ScoreEditorViewModel = hiltViewModel(),
) {
	val scoreEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is ScoreEditorScreenEvent.Dismissed -> onDismissWithResult(
							it.scoringMethod to it.score,
						)
					}
				}
		}
	}

	ScoreEditorScreen(
		state = scoreEditorScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun ScoreEditorScreen(
	state: ScoreEditorScreenUiState,
	onAction: (ScoreEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			ScoreEditorTopBar(onAction = { onAction(ScoreEditorScreenUiAction.ScoreEditor(it)) })
		},
	) { padding ->
		when (state) {
			ScoreEditorScreenUiState.Loading -> Unit
			is ScoreEditorScreenUiState.Loaded -> ScoreEditor(
				state = state.scoreEditor,
				onAction = { onAction(ScoreEditorScreenUiAction.ScoreEditor(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}
