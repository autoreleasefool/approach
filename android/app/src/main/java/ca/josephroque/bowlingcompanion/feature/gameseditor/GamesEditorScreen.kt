package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState

@Composable
internal fun GamesEditorRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesEditorViewModel = hiltViewModel(),
) {
	val frameEditorState by viewModel.frameEditorState.collectAsStateWithLifecycle()

	GamesEditorScreen(
		frameEditorState = frameEditorState,
		onBackPressed = onBackPressed,
		modifier = modifier,
	)
}

@Composable
internal fun GamesEditorScreen(
	frameEditorState: FrameEditorUiState,
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
) {

}