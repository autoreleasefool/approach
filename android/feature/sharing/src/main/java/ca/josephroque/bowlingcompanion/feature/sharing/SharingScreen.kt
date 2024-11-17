package ca.josephroque.bowlingcompanion.feature.sharing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingTopBar
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingTopBarUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharing
import kotlinx.coroutines.launch

@Composable
internal fun SharingRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SharingViewModel = hiltViewModel(),
) {
	val sharingState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SharingScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	SharingScreen(
		state = sharingState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SharingScreen(
	state: SharingScreenUiState,
	onAction: (SharingScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			SharingTopBar(
				state = SharingTopBarUiState,
				onAction = { onAction(SharingScreenUiAction.TopBarAction(it)) },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			is SharingScreenUiState.Loading -> Unit
			is SharingScreenUiState.SharingSeries -> SeriesSharing(
				state = state.seriesSharing,
				onAction = { onAction(SharingScreenUiAction.SeriesSharingAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
