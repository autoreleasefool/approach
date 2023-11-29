package ca.josephroque.bowlingcompanion.feature.alleyslist

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
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysList
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListTopBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun AlleysListRoute(
	onBackPressed: () -> Unit,
	onEditAlley: (UUID) -> Unit,
	onAddAlley: () -> Unit,
	onShowAlleyDetails: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AlleysListViewModel = hiltViewModel(),
) {
	val alleysListScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AlleysListScreenEvent.Dismissed -> onBackPressed()
						AlleysListScreenEvent.NavigateToAddAlley -> onAddAlley()
						is AlleysListScreenEvent.NavigateToEditAlley -> onEditAlley(it.id)
					}
				}
		}
	}

	AlleysListScreen(
		state = alleysListScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun AlleysListScreen(
	state: AlleysListScreenUiState,
	onAction: (AlleysListScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AlleysListTopBar(onAction = { onAction(AlleysListScreenUiAction.AlleysList(it)) })
		}
	) { padding ->
		when (state) {
			AlleysListScreenUiState.Loading -> Unit
			is AlleysListScreenUiState.Loaded -> {
				AlleysList(
					state = state.alleysList,
					onAction = { onAction(AlleysListScreenUiAction.AlleysList(it)) },
					modifier = modifier.padding(padding),
				)
			}
		}
	}
}