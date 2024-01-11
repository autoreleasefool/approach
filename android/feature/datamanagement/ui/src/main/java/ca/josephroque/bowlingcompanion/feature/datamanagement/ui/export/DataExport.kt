package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.R
import kotlinx.datetime.LocalDate

@Composable
fun DataExport(
	state: DataExportUiState,
	onAction: (DataExportUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
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

			Spacer(modifier = Modifier.weight(1f))
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Spacer(modifier = Modifier.weight(1f))

			TextButton(onClick = { onAction(DataExportUiAction.ShareClicked) }) {
				Text(text = stringResource(R.string.data_export_share))
			}

			Button(onClick = { onAction(DataExportUiAction.SaveClicked)}) {
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
private fun LastExportDateCard(
	lastExportDate: LocalDate?,
	modifier: Modifier = Modifier,
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = if (lastExportDate == null) {
				MaterialTheme.colorScheme.errorContainer
			} else {
				MaterialTheme.colorScheme.primaryContainer
			},
		),
		modifier = modifier,
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
					tint = MaterialTheme.colorScheme.onErrorContainer,
				)

				Text(
					text = stringResource(R.string.data_export_never_exported),
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onErrorContainer
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
			),
			onAction = {},
		)
	}
}