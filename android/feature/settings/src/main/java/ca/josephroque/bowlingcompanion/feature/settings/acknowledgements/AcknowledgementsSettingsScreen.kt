package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements

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
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.Acknowledgements
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsTopBar
import kotlinx.coroutines.launch

@Composable
fun AcknowledgementsSettingsRoute(
	onBackPressed: () -> Unit,
	onShowAcknowledgementDetails: (String) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AcknowledgementsViewModel = hiltViewModel(),
) {
	val acknowledgementsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AcknowledgementsSettingsScreenEvent.Dismissed -> onBackPressed()
						is AcknowledgementsSettingsScreenEvent.NavigatedToAcknowledgement ->
							onShowAcknowledgementDetails(it.name)
					}
				}
		}
	}

	AcknowledgementsSettingsScreen(
		state = acknowledgementsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AcknowledgementsSettingsScreen(
	state: AcknowledgementsSettingsScreenUiState,
	onAction: (AcknowledgementsSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	Scaffold(
		topBar = {
			AcknowledgementsTopBar(
				onAction = { onAction(AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			AcknowledgementsSettingsScreenUiState.Loading -> Unit
			is AcknowledgementsSettingsScreenUiState.Loaded ->
				Acknowledgements(
					state = state.acknowledgements,
					onAction = { onAction(AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction(it)) },
					modifier = Modifier.padding(padding),
				)
		}
	}
}
