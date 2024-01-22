package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.SendEmailButton
import ca.josephroque.bowlingcompanion.core.designsystem.theme.ApproachTheme
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.R
import kotlinx.datetime.LocalDate

@Composable
fun DataImport(
	state: DataImportUiState,
	onAction: (DataImportUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	DataImportFilePicker(state = state, onAction = onAction)

	if (state.isShowingRestoreDialog && state.lastImportDate != null) {
		RestoreDialog(
			lastImportDate = state.lastImportDate,
			onAction = onAction,
		)
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.weight(1f),
		) {
			Spacer(modifier = Modifier.weight(1f))

			Text(
				text = stringResource(R.string.data_import_description),
				style = MaterialTheme.typography.bodyMedium,
			)

			DataImportProgressCard(
				progress = state.progress,
				versionCode = state.versionCode,
				versionName = state.versionName,
			)

			Spacer(modifier = Modifier.weight(1f))

			when (state.progress) {
				is DataImportProgress.Failed,
				DataImportProgress.PickingFile,
				DataImportProgress.Importing,
				DataImportProgress.RestoreComplete,
				DataImportProgress.ImportComplete -> Unit
				DataImportProgress.NotStarted -> OverwriteWarningCard()
			}
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			val areActionsEnabled = when (state.progress) {
				DataImportProgress.Importing,
				DataImportProgress.PickingFile,
				DataImportProgress.RestoreComplete,
				DataImportProgress.ImportComplete -> false
				DataImportProgress.NotStarted, is DataImportProgress.Failed -> true
			}

			if (state.isRestoreAvailable) {
				Spacer(modifier = Modifier.weight(1f))

				TextButton(
					onClick = { onAction(DataImportUiAction.RestoreClicked) },
					enabled = areActionsEnabled,
				) {
					Text(text = stringResource(R.string.data_import_restore))
				}
			}

			Button(
				onClick = { onAction(DataImportUiAction.StartImportClicked) },
				enabled = areActionsEnabled,
			) {
				Text(
					text = stringResource(R.string.data_import_import),
					textAlign = TextAlign.Center,
					modifier = if (state.isRestoreAvailable) Modifier else Modifier.fillMaxWidth(),
				)
			}
		}
	}
}

@Composable
private fun RestoreDialog(
	lastImportDate: LocalDate,
	onAction: (DataImportUiAction) -> Unit,
) {
	AlertDialog(
		onDismissRequest = { onAction(DataImportUiAction.CancelRestoreClicked) },
		title = { Text(text = stringResource(R.string.data_import_restore_title)) },
		text = { Text(text = stringResource(R.string.data_import_restore_message, lastImportDate.toString())) },
		confirmButton = {
			TextButton(onClick = { onAction(DataImportUiAction.ConfirmRestoreClicked) }) {
				Text(text = stringResource(R.string.data_import_restore_restore))
			}
		},
		dismissButton = {
			TextButton(onClick = { onAction(DataImportUiAction.CancelRestoreClicked) }) {
				Text(text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel))
			}
		}
	)
}

@Composable
private fun OverwriteWarningCard() {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.warning_container),
		),
		modifier = Modifier.padding(bottom = 16.dp),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(16.dp),
		) {
			Icon(
				Icons.Default.Warning,
				contentDescription = null,
				tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_warning_container),
			)

			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Text(
					text = stringResource(R.string.data_import_this_will_overwrite),
					style = MaterialTheme.typography.titleMedium,
					color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_warning_container),
				)

				Text(
					text = stringResource(R.string.data_import_not_recoverable),
					style = MaterialTheme.typography.bodyMedium,
					color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_warning_container),
				)
			}
		}
	}
}

@Composable
fun DataImportFilePicker(
	state: DataImportUiState,
	onAction: (DataImportUiAction) -> Unit,
) {
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent(),
	) { uri ->
		onAction(DataImportUiAction.FileSelected(uri))
	}

	LaunchedEffect(state.progress) {
		if (state.progress == DataImportProgress.PickingFile) {
			launcher.launch("*/*")
		}
	}
}

@Composable
fun DataImportProgressCard(
	progress: DataImportProgress,
	versionName: String,
	versionCode: String,
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = when (progress) {
				DataImportProgress.NotStarted -> MaterialTheme.colorScheme.secondaryContainer
				DataImportProgress.PickingFile -> MaterialTheme.colorScheme.secondaryContainer
				DataImportProgress.Importing -> MaterialTheme.colorScheme.secondaryContainer
				DataImportProgress.RestoreComplete -> colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.success_container)
				DataImportProgress.ImportComplete -> colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.success_container)
				is DataImportProgress.Failed -> MaterialTheme.colorScheme.errorContainer
			}
		)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.padding(vertical = 16.dp),
		) {
			when (progress) {
				DataImportProgress.NotStarted, DataImportProgress.PickingFile -> {
					Text(
						text = stringResource(R.string.data_import_progress_not_started),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSecondaryContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				}
				DataImportProgress.Importing -> {
					Text(
						text = stringResource(R.string.data_import_progress_importing),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSecondaryContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				}
				DataImportProgress.ImportComplete -> {
					Text(
						text = stringResource(R.string.data_import_progress_import_success),
						style = MaterialTheme.typography.bodyMedium,
						color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_success_container),
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				}
				DataImportProgress.RestoreComplete -> {
					Text(
						text = stringResource(R.string.data_import_progress_restore_success),
						style = MaterialTheme.typography.bodyMedium,
						color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_success_container),
						modifier = Modifier.padding(horizontal = 16.dp),
					)
				}
				is DataImportProgress.Failed -> {
					Text(
						text = stringResource(R.string.data_import_progress_error),
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					Text(
						text = progress.exception.localizedMessage ?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.default_error_unknown_message),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					Divider()

					Text(
						text = stringResource(R.string.data_import_progress_error_report),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					SendEmailButton(
						body = progress.exception.localizedMessage ?: stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.default_error_unknown_message),
						versionName = versionName,
						versionCode = versionCode,
						modifier = Modifier.align(Alignment.End),
						subjectRes = R.string.data_import_error_email_subject,
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun DataImportPreview() {
	ApproachTheme {
		Surface {
			DataImport(
				state = DataImportUiState(
					lastImportDate = LocalDate(2021, 1, 1),
					progress = DataImportProgress.Failed(Exception("Test")),
					versionName = "",
					versionCode = "",
				),
				onAction = {},
			)
		}
	}
}