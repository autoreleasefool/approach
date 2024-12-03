package ca.josephroque.bowlingcompanion.feature.sharing

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharingSheet(
	source: SharingSource,
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

	LaunchedEffect(source) {
		viewModel.handleAction(SharingScreenUiAction.DidStartSharing(source))
	}

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = modifier,
	) {
		SharingScreen(
			state = sharingState,
			onAction = viewModel::handleAction,
		)
	}
}

@Composable
private fun SharingScreen(
	state: SharingScreenUiState,
	onAction: (SharingScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (state) {
		is SharingScreenUiState.Loading -> Unit
		is SharingScreenUiState.SharingSeries -> SeriesSharing(
			state = state.seriesSharing,
			onAction = { onAction(SharingScreenUiAction.SeriesSharingAction(it)) },
			modifier = modifier,
		)
	}
}
