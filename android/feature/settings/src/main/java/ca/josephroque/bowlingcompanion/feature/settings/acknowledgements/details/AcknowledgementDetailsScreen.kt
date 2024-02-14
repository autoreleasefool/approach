package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details

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
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetails
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBarUiState
import kotlinx.coroutines.launch

@Composable
fun AcknowledgementDetailsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AcknowledgementDetailsViewModel = hiltViewModel(),
) {
	val acknowledgementDetailsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AcknowledgementDetailsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	AcknowledgementDetailsScreen(
		state = acknowledgementDetailsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcknowledgementDetailsScreen(
	state: AcknowledgementDetailsScreenUiState,
	onAction: (AcknowledgementDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			AcknowledgementDetailsTopBar(
				state = when (state) {
					AcknowledgementDetailsScreenUiState.Loading -> AcknowledgementDetailsTopBarUiState()
					is AcknowledgementDetailsScreenUiState.Loaded -> state.topBar
				},
				onAction = { onAction(AcknowledgementDetailsScreenUiAction.AcknowledgementDetailsAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			AcknowledgementDetailsScreenUiState.Loading -> Unit
			is AcknowledgementDetailsScreenUiState.Loaded ->
				AcknowledgementDetails(
					state = state.acknowledgementDetails,
					modifier = Modifier.padding(padding),
				)
		}
	}
}
