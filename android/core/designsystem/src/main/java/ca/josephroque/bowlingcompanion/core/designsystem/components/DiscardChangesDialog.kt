package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun DiscardChangesDialog(onDiscardChanges: () -> Unit, onDismiss: () -> Unit) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(text = stringResource(R.string.discard_changes_dialog_title)) },
		text = { Text(text = stringResource(R.string.discard_changes_dialog_message)) },
		confirmButton = {
			TextButton(onClick = onDiscardChanges) {
				Text(text = stringResource(R.string.action_discard))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.action_keep_editing))
			}
		},
	)
}
