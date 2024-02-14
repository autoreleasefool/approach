package ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchPlayEditorTopBar(
	gameIndex: Int,
	onAction: (MatchPlayEditorUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.match_play_editor_title, gameIndex + 1),
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(MatchPlayEditorUiAction.BackClicked) })
		},
		actions = {
			TextButton(onClick = { onAction(MatchPlayEditorUiAction.DoneClicked) }) {
				Text(
					text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		},
		scrollBehavior = scrollBehavior,
	)
}
