package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.Acknowledgements
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsTopBar

@Composable
fun AcknowledgementsSettingsRoute(
	onBackPressed: () -> Unit,
	onShowAcknowledgementDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AcknowledgementsViewModel = hiltViewModel(),
) {
	val acknowledgementsState by viewModel.uiState.collectAsStateWithLifecycle()

	when (val event = viewModel.events.collectAsState().value) {
		AcknowledgementsSettingsScreenEvent.Dismissed -> onBackPressed()
		is AcknowledgementsSettingsScreenEvent.NavigatedToAcknowledgement -> {
			viewModel.handleAction(AcknowledgementsSettingsScreenUiAction.HandledNavigation)
			onShowAcknowledgementDetails(event.name)
		}
		null -> Unit
	}

	AcknowledgementsSettingsScreen(
		state = acknowledgementsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun AcknowledgementsSettingsScreen(
	state: AcknowledgementsSettingsScreenUiState,
	onAction: (AcknowledgementsSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AcknowledgementsTopBar(
				onAction = { onAction(AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction(it)) },
			)
		}
	) { padding ->
		when (state) {
			AcknowledgementsSettingsScreenUiState.Loading -> Unit
			is AcknowledgementsSettingsScreenUiState.Loaded ->
				Acknowledgements(
					state = state.acknowledgements,
					onAction = { onAction(AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction(it)) },
					modifier = modifier.padding(padding),
				)
		}
	}
}