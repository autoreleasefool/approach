package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreEditorTopBar(onAction: (ScoreEditorUiAction) -> Unit) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.score_editor_title),
			)
		},
		navigationIcon = {
			CloseButton { onAction(ScoreEditorUiAction.BackClicked) }
		},
		actions = {
			TextButton(onClick = { onAction(ScoreEditorUiAction.SaveClicked) }) {
				Text(
					text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		},
	)
}
