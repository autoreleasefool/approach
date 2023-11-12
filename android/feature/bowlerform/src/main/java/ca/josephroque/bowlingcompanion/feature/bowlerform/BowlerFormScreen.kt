package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBar
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState

@Composable
internal fun BowlerFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerFormViewModel = hiltViewModel(),
) {
	val bowlerFormScreenState = viewModel.uiState.collectAsState().value

	when (viewModel.events.collectAsState().value) {
		BowlerFormScreenEvent.Dismissed -> onDismiss()
		null -> Unit
	}

	BowlerFormScreen(
		state = bowlerFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun BowlerFormScreen(
	state: BowlerFormScreenUiState,
	onAction: (BowlerFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(BowlerFormScreenUiAction.LoadBowler)
	}

	Scaffold(
		topBar = {
			BowlerFormTopBar(
				state = when (state) {
					BowlerFormScreenUiState.Loading -> BowlerFormTopBarUiState()
					is BowlerFormScreenUiState.Create -> state.topBar
					is BowlerFormScreenUiState.Edit -> state.topBar
			  },
				onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
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