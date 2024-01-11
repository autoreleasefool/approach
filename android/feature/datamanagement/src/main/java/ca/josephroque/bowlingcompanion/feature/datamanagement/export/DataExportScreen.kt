package ca.josephroque.bowlingcompanion.feature.datamanagement.export

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExport
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportTopBar
import kotlinx.coroutines.launch

@Composable
fun DataExportRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: DataExportViewModel = hiltViewModel(),
) {
	val dataExportScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						DataExportScreenEvent.Dismissed -> onBackPressed()
						is DataExportScreenEvent.LaunchShareIntent -> {
							val fileUri = FileProvider.getUriForFile(
								context,
								"ca.josephroque.bowlingcompanion.fileprovider",
								it.file,
							)

							val intent = Intent(Intent.ACTION_SEND)
							intent.putExtra(Intent.EXTRA_STREAM, fileUri)
							intent.setType("application/octet-stream")
							intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
							context.startActivity(intent)
						}
						is DataExportScreenEvent.LaunchCreateDocumentIntent -> {
							val fileUri = FileProvider.getUriForFile(
								context,
								"ca.josephroque.bowlingcompanion.fileprovider",
								it.file,
							)

							val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
							intent.addCategory(Intent.CATEGORY_OPENABLE)
							intent.putExtra(Intent.EXTRA_STREAM, fileUri)
							intent.putExtra(Intent.EXTRA_TITLE, it.file.name)
							intent.setType("application/octet-stream")
							intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
							context.startActivity(intent)
						}
					}
				}
		}
	}

	DataExportScreen(
		state = dataExportScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataExportScreen(
	state: DataExportScreenUiState,
	onAction: (DataExportScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			DataExportTopBar(
				onAction = { onAction(DataExportScreenUiAction.DataExportAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			DataExportScreenUiState.Loading -> Unit
			is DataExportScreenUiState.Loaded -> DataExport(
				state = state.dataExport,
				onAction = { onAction(DataExportScreenUiAction.DataExportAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}