package ca.josephroque.bowlingcompanion.feature.datamanagement

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExport
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExportTopBar

@Composable
fun DataExportRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: DataExportViewModel = hiltViewModel(),
) {
	val dataExportScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val context = LocalContext.current
	when (val event = viewModel.events.collectAsState().value) {
		DataExportScreenEvent.Dismissed -> onBackPressed()
		is DataExportScreenEvent.LaunchShareIntent -> {
			viewModel.handleAction(DataExportScreenUiAction.HandledShareAction)
			val fileUri = FileProvider.getUriForFile(
				context,
				"ca.josephroque.bowlingcompanion.fileprovider",
				event.file,
			)

			val intent = Intent(Intent.ACTION_SEND)
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			intent.setDataAndType(fileUri, context.contentResolver.getType(fileUri))
			context.startActivity(intent)
		}
		null -> Unit
	}

	DataExportScreen(
		state = dataExportScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun DataExportScreen(
	state: DataExportScreenUiState,
	onAction: (DataExportScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			DataExportTopBar(
				onAction = { onAction(DataExportScreenUiAction.DataExportAction(it)) },
			)
		},
	) { padding ->
		when (state) {
			DataExportScreenUiState.Loading -> Unit
			is DataExportScreenUiState.Loaded -> DataExport(
				state = state.dataExport,
				onAction = { onAction(DataExportScreenUiAction.DataExportAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}