package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBar
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiState

@Composable
internal fun BowlerFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerFormViewModel = hiltViewModel(),
) {
	val bowlerFormScreenState = viewModel.uiState.collectAsState().value

	when (viewModel.events.collectAsState().value) {
		BowlerFormScreenEvent.Dismissed -> onDismiss()
		else -> Unit
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

	when (state) {
		BowlerFormScreenUiState.Loading -> LoadingState()
		is BowlerFormScreenUiState.Create ->
			BowlerFormScreen(
				bowlerFormState = state.form,
				topBarState = state.topBar,
				onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
				modifier = modifier,
			)
		is BowlerFormScreenUiState.Edit ->
			BowlerFormScreen(
				bowlerFormState = state.form,
				topBarState = state.topBar,
				onAction = { onAction(BowlerFormScreenUiAction.BowlerFormAction(it)) },
				modifier = modifier,
			)
	}
}

@Composable
private fun BowlerFormScreen(
	bowlerFormState: BowlerFormUiState,
	topBarState: BowlerFormTopBarUiState,
	onAction: (BowlerFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			BowlerFormTopBar(
				state = topBarState,
				onAction = onAction,
			)
		},
	) { padding ->
		BowlerForm(
			state = bowlerFormState,
			onAction = onAction,
			modifier = modifier
				.padding(padding)
		)
	}
}