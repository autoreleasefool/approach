package ca.josephroque.bowlingcompanion.feature.datamanagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

			Spacer(modifier = Modifier.weight(1f))
		}

		Text(
			text = if (state.lastExportDate != null)
				stringResource(R.string.data_export_last_export, state.lastExportDate.toString())
			else
				stringResource(R.string.data_export_never_exported),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.padding(vertical = 16.dp),
		)

		Button(onClick = { onAction(DataExportUiAction.ExportClicked)}) {
			Text(text = stringResource(R.string.data_export_export))
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