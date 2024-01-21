package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.R
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.components.SendEmailButton
import kotlinx.datetime.LocalDate

@Composable
fun DataExport(
	state: DataExportUiState,
	onAction: (DataExportUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	DataExportFilePicker(
		state = state,
		onAction = onAction,
	)

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp)
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
				text = stringResource(R.string.data_export_description),
				style = MaterialTheme.typography.bodyMedium,
			)

			Text(
				text = stringResource(R.string.data_export_regular_backups),
				style = MaterialTheme.typography.bodyMedium,
			)

			Text(
				text = stringResource(R.string.data_export_your_data),
				style = MaterialTheme.typography.bodyMedium,
			)

			LastExportDateCard(
				lastExportDate = state.lastExportDate,
			)

			DataExportProgressCard(
				progress = state.progress,
				versionCode = state.versionCode,
				versionName = state.versionName,
			)

			Spacer(modifier = Modifier.weight(1f))
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Spacer(modifier = Modifier.weight(1f))

			val isExportEnabled = when (state.progress) {
				DataExportProgress.NotStarted, DataExportProgress.Complete, is DataExportProgress.Failed -> true
				is DataExportProgress.PickingDestination, DataExportProgress.Exporting -> false
			}

			TextButton(
				onClick = { onAction(DataExportUiAction.ShareClicked) },
				enabled = isExportEnabled,
			) {
				Text(text = stringResource(R.string.data_export_share))
			}

			Button(
				onClick = { onAction(DataExportUiAction.SaveClicked) },
				enabled = isExportEnabled,
			) {
				Icon(
					painterResource(R.drawable.ic_download),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onPrimary,
				)

				Text(text = stringResource(R.string.data_export_save))
			}
		}
	}
}

@Composable
private fun DataExportFilePicker(
	state: DataExportUiState,
	onAction: (DataExportUiAction) -> Unit,
) {
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
	) { uri ->
		uri?.let { onAction(DataExportUiAction.DestinationPicked(it)) }
	}

	LaunchedEffect(state.progress) {
		if (state.progress is DataExportProgress.PickingDestination) {
			launcher.launch(state.progress.fileName)
		}
	}
}

@Composable
private fun DataExportProgressCard(
	progress: DataExportProgress,
	versionCode: String,
	versionName: String,
) {
	when (progress) {
		DataExportProgress.NotStarted, is DataExportProgress.PickingDestination, DataExportProgress.Exporting -> Unit
		DataExportProgress.Complete -> {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.success_container),
				),
			) {
				Text(
					text = stringResource(R.string.data_export_progress_success),
					style = MaterialTheme.typography.bodyMedium,
					color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_success_container),
					modifier = Modifier.padding(16.dp),
				)
			}
		}
		is DataExportProgress.Failed -> {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.errorContainer,
				),
			) {
				Column(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.padding(vertical = 16.dp),
				) {
					Text(
						text = stringResource(R.string.data_export_progress_error),
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					Text(
						text = progress.exception.localizedMessage ?: stringResource(R.string.data_error_unknown),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					Divider()

					Text(
						text = stringResource(R.string.data_export_progress_error_report),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onErrorContainer,
						modifier = Modifier.padding(horizontal = 16.dp),
					)

					SendEmailButton(
						errorMessage = progress.exception.localizedMessage,
						versionName = versionName,
						versionCode = versionCode,
						modifier = Modifier.align(Alignment.End),
					)
				}
			}
		}
	}
}

@Composable
private fun LastExportDateCard(
	lastExportDate: LocalDate?,
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = if (lastExportDate == null) {
				colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.warning_container)
			} else {
				MaterialTheme.colorScheme.primaryContainer
			},
		),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			if (lastExportDate == null) {
				Icon(
					Icons.Default.Warning,
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_warning_container),
				)

				Text(
					text = stringResource(R.string.data_export_never_exported),
					style = MaterialTheme.typography.bodyMedium,
					color = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.text_on_warning_container),
				)
			} else {
				Text(
					text = stringResource(R.string.data_export_last_export, lastExportDate.toString()),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer,
				)
			}
		}
	}
}

@Preview
@Composable
private fun DataExportPreview() {
	Surface {
		DataExport(
			state = DataExportUiState(
				lastExportDate = LocalDate(2021, 1, 1),
				progress = DataExportProgress.Complete,
			),
			onAction = {},
		)
	}
}