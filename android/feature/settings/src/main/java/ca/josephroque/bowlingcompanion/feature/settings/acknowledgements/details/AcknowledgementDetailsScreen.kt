package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetails
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBar
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBarUiState

@Composable
fun AcknowledgementDetailsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AcknowledgementDetailsViewModel = hiltViewModel(),
) {
	val acknowledgementDetailsState by viewModel.uiState.collectAsStateWithLifecycle()

	when (viewModel.events.collectAsState().value) {
		AcknowledgementDetailsScreenEvent.Dismissed -> onBackPressed()
		null -> Unit
	}

	AcknowledgementDetailsScreen(
		state = acknowledgementDetailsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun AcknowledgementDetailsScreen(
	state: AcknowledgementDetailsScreenUiState,
	onAction: (AcknowledgementDetailsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AcknowledgementDetailsTopBar(
				state = when (state) {
					AcknowledgementDetailsScreenUiState.Loading -> AcknowledgementDetailsTopBarUiState()
					is AcknowledgementDetailsScreenUiState.Loaded -> state.topBar
				},
				onAction = { onAction(AcknowledgementDetailsScreenUiAction.AcknowledgementDetailsAction(it)) },
			)
		}
	) { padding ->
		when (state) {
			AcknowledgementDetailsScreenUiState.Loading -> Unit
			is AcknowledgementDetailsScreenUiState.Loaded ->
				AcknowledgementDetails(
					state = state.acknowledgementDetails,
					modifier = modifier.padding(padding),
				)
		}
	}
}