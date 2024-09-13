package ca.josephroque.bowlingcompanion.feature.alleyform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyForm
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormTopBar
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiAction
import kotlinx.coroutines.launch

@Composable
internal fun AlleyFormRoute(
	onDismiss: () -> Unit,
	onManageLanes: (List<LaneID>, NavResultCallback<List<LaneID>>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AlleyFormViewModel = hiltViewModel(),
) {
	val alleyFormScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AlleyFormScreenEvent.Dismissed -> onDismiss()
						is AlleyFormScreenEvent.ManageLanes ->
							onManageLanes(it.existingLanes) @JvmSerializableLambda { ids ->
								viewModel.handleAction(AlleyFormScreenUiAction.LanesUpdated(ids))
							}
					}
				}
		}
	}

	AlleyFormScreen(
		state = alleyFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlleyFormScreen(
	state: AlleyFormScreenUiState,
	onAction: (AlleyFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(AlleyFormScreenUiAction.LoadAlley)
	}

	BackHandler {
		onAction(AlleyFormScreenUiAction.AlleyForm(AlleyFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			AlleyFormTopBar(
				state = when (state) {
					AlleyFormScreenUiState.Loading -> AlleyFormTopBarUiState()
					is AlleyFormScreenUiState.Create -> state.topBar
					is AlleyFormScreenUiState.Edit -> state.topBar
				},
				onAction = { onAction(AlleyFormScreenUiAction.AlleyForm(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			AlleyFormScreenUiState.Loading -> Unit
			is AlleyFormScreenUiState.Create ->
				AlleyForm(
					state = state.form,
					onAction = { onAction(AlleyFormScreenUiAction.AlleyForm(it)) },
					modifier = Modifier.padding(padding),
				)
			is AlleyFormScreenUiState.Edit ->
				AlleyForm(
					state = state.form,
					onAction = { onAction(AlleyFormScreenUiAction.AlleyForm(it)) },
					modifier = Modifier.padding(padding),
				)
		}
	}
}
