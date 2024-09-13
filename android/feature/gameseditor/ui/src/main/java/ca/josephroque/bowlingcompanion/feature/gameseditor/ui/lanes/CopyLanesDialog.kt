package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.lanes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
fun CopyLanesDialog(
	state: CopyLanesDialogUiState,
	onAction: (CopyLanesDialogUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	AlertDialog(
		onDismissRequest = { onAction(CopyLanesDialogUiAction.Dismissed) },
		title = {
			Text(
				text = stringResource(R.string.game_editor_lanes_duplicate_title),
			)
		},
		text = {
			Text(
				text = stringResource(R.string.game_editor_lanes_duplicate_description),
			)
		},
		confirmButton = {
			TextButton(onClick = { onAction(CopyLanesDialogUiAction.CopyToAllClicked) }) {
				Text(
					text = stringResource(R.string.game_editor_lanes_duplicate_copy_to_all),
				)
			}
		},
		dismissButton = {
			TextButton(onClick = { onAction(CopyLanesDialogUiAction.Dismissed) }) {
				Text(
					text = stringResource(R.string.game_editor_lanes_duplicate_dismiss),
				)
			}
		},
		modifier = modifier,
	)
}
