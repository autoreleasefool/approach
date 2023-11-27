package ca.josephroque.bowlingcompanion.feature.archives

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesList
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListTopBar
import kotlinx.coroutines.launch

@Composable
internal fun ArchivesListRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: ArchivesListViewModel = hiltViewModel(),
) {
	val archivesListState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						ArchivesListScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	ArchivesListScreen(
		state = archivesListState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun ArchivesListScreen(
	state: ArchivesListScreenUiState,
	onAction: (ArchivesListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			ArchivesListTopBar(
				onAction = { onAction(ArchivesListScreenUiAction.ListAction(it)) },
			)
		},
	) { padding ->
		when (state) {
			ArchivesListScreenUiState.Loading -> Unit
			is ArchivesListScreenUiState.Loaded -> ArchivesList(
				state = state.archivesList,
				onAction = { onAction(ArchivesListScreenUiAction.ListAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}