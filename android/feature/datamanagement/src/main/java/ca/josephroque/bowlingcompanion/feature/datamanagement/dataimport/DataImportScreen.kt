package ca.josephroque.bowlingcompanion.feature.datamanagement.dataimport

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImport
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportTopBar
import kotlinx.coroutines.launch

@Composable
fun DataImportRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: DataImportViewModel = hiltViewModel(),
) {
	val dataImportScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						DataImportScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	DataImportScreen(
		state = dataImportScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataImportScreen(
	state: DataImportScreenUiState,
	onAction: (DataImportScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(DataImportScreenUiAction.OnAppear)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			DataImportTopBar(
				onAction = { onAction(DataImportScreenUiAction.DataImport(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			DataImportScreenUiState.Loading -> Unit
			is DataImportScreenUiState.Loaded -> DataImport(
				state = state.dataImport,
				onAction = { onAction(DataImportScreenUiAction.DataImport(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}