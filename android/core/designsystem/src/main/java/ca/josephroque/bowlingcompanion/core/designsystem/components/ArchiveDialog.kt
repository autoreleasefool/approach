package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun ArchiveDialog(itemName: String, onArchive: () -> Unit, onDismiss: () -> Unit) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				text = stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.archive_dialog_title,
					itemName,
				),
			)
		},
		text = {
			Text(
				text = stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.archive_dialog_message,
				),
			)
		},
		confirmButton = {
			TextButton(onClick = onArchive) {
				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_archive,
					),
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel,
					),
				)
			}
		},
	)
}

@Composable
fun DeleteDialog(itemName: String, onDelete: () -> Unit, onDismiss: () -> Unit) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = {
			Text(
				text = stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.delete_dialog_title,
					itemName,
				),
			)
		},
		text = {
			Text(
				text = stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.delete_dialog_message,
				),
			)
		},
		confirmButton = {
			TextButton(onClick = onDelete) {
				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_delete,
					),
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel,
					),
				)
			}
		},
	)
}
