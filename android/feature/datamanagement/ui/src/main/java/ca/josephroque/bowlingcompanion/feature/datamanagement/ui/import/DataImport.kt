package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.theme.ApproachTheme
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.R

@Composable
fun DataImport(
	state: DataImportUiState,
	onAction: (DataImportUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
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
				is DataImportProgress.Failed, DataImportProgress.PickingFile, DataImportProgress.Importing -> Unit
				DataImportProgress.NotStarted, DataImportProgress.Complete -> OverwriteWarningCard()
			}
		}

		Button(
			onClick = { onAction(DataImportUiAction.StartImportClicked) },
			enabled = when (state.progress) {
				DataImportProgress.Importing, DataImportProgress.PickingFile -> false
				DataImportProgress.Complete, is DataImportProgress.Failed, DataImportProgress.NotStarted -> true
			}
		) {
			Text(text = stringResource(R.string.data_import_import))
		}
	}
}

@Composable
private fun OverwriteWarningCard() {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.errorContainer,
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
				tint = MaterialTheme.colorScheme.onErrorContainer,
			)

			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				Text(
					text = stringResource(R.string.data_import_this_will_overwrite),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onErrorContainer,
				)

				Text(
					text = stringResource(R.string.data_import_not_recoverable),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onErrorContainer,
				)
			}
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
				DataImportProgress.Complete -> MaterialTheme.colorScheme.primaryContainer
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
				DataImportProgress.Complete -> {
					Text(
						text = stringResource(R.string.data_import_progress_success),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer,
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
						text = progress.error.localizedMessage ?: stringResource(R.string.data_import_error_unknown),
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

					val context = LocalContext.current
					OutlinedButton(
						onClick = {
							val recipient = context.resources.getString(R.string.data_import_error_email_recipient)
							val subject = context.resources.getString(
								R.string.data_import_error_email_subject,
								versionName,
								versionCode,
							)
							val body = progress.error.localizedMessage ?: context.resources.getString(R.string.data_import_error_unknown)
							val emailIntent = Intent(Intent.ACTION_SEND).apply {
								setDataAndType(Uri.parse("mailto:"), "message/rfc822")
								putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
								putExtra(Intent.EXTRA_SUBJECT, subject)
								putExtra(Intent.EXTRA_TEXT, body)
							}

							context.startActivity(
								Intent.createChooser(
									emailIntent,
									context.resources.getString(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email)
								)
							)
						},
						modifier = Modifier
							.padding(horizontal = 16.dp)
							.align(Alignment.End),
					) {
						Text(
							text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_send_email),
							style = MaterialTheme.typography.bodyMedium,
						)
					}
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
					progress = DataImportProgress.NotStarted,
					versionName = "",
					versionCode = "",
				),
				onAction = {},
			)
		}
	}
}