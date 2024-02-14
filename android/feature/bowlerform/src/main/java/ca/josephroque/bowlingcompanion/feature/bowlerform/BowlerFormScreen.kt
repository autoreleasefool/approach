package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBar
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiAction
import kotlinx.coroutines.launch

@Composable
internal fun BowlerFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerFormViewModel = hiltViewModel(),
) {
	val bowlerFormScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						BowlerFormScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	BowlerFormScreen(
		state = bowlerFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BowlerFormScreen(
	state: BowlerFormScreenUiState,
	onAction: (BowlerFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(BowlerFormScreenUiAction.LoadBowler)
	}

	BackHandler {
		onAction(BowlerFormScreenUiAction.BowlerFormAction(BowlerFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			BowlerFormTopBar(
				state = when (state) {
					BowlerFormScreenUiState.Loading -> BowlerFormTopBarUiState()
					is BowlerFormScreenUiState.Create -> state.topBar
					is BowlerFormScreenUiState.Edit -> state.topBar
				},
				onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
	) { padding ->
		when (state) {
			BowlerFormScreenUiState.Loading -> Unit
			is BowlerFormScreenUiState.Create ->
				BowlerForm(
					state = state.form,
					onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
					modifier = modifier.padding(padding),
				)
			is BowlerFormScreenUiState.Edit ->
				BowlerForm(
					state = state.form,
					onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
					modifier = modifier.padding(padding),
				)
		}
	}
}
