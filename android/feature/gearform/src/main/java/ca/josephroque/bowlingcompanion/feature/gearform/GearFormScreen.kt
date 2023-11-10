package ca.josephroque.bowlingcompanion.feature.gearform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearForm
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBar
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiState
import java.util.UUID

@Composable
internal fun GearFormRoute(
	onDismiss: () -> Unit,
	onEditAvatar: (Avatar, NavResultCallback<Avatar>) -> Unit,
	onEditOwner: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GearFormViewModel = hiltViewModel(),
) {
	val gearFormScreenState = viewModel.uiState.collectAsState().value

	when (val event = viewModel.events.collectAsState().value) {
		GearFormScreenEvent.Dismissed -> onDismiss()
		is GearFormScreenEvent.EditAvatar -> {
			viewModel.handleAction(GearFormScreenUiAction.FinishedNavigation)
			onEditAvatar(event.avatar) {
				viewModel.handleAction(GearFormScreenUiAction.UpdatedAvatar(it))
			}
		}
		is GearFormScreenEvent.EditOwner -> {
			viewModel.handleAction(GearFormScreenUiAction.FinishedNavigation)
			onEditOwner(event.owner) { ids ->
				viewModel.handleAction(GearFormScreenUiAction.UpdatedOwner(ids.firstOrNull()))
			}
		}
		null -> Unit
	}

	GearFormScreen(
		state = gearFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun GearFormScreen(
	state: GearFormScreenUiState,
	onAction: (GearFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(GearFormScreenUiAction.LoadGear)
	}

	when (state) {
		GearFormScreenUiState.Loading -> LoadingState()
		is GearFormScreenUiState.Create ->
			GearFormScreen(
				gearFormState = state.form,
				topBarState = state.topBar,
				onAction = { onAction(GearFormScreenUiAction.GearFormAction(it)) },
				modifier = modifier,
			)
		is GearFormScreenUiState.Edit ->
			GearFormScreen(
				gearFormState = state.form,
				topBarState = state.topBar,
				onAction = { onAction(GearFormScreenUiAction.GearFormAction(it)) },
				modifier = modifier,
			)
	}
}

@Composable
private fun GearFormScreen(
	gearFormState: GearFormUiState,
	topBarState: GearFormTopBarUiState,
	onAction: (GearFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			GearFormTopBar(
				state = topBarState,
				onAction = onAction,
			)
		}
	) { padding ->
		GearForm(
			state = gearFormState,
			onAction = onAction,
			modifier = modifier
				.padding(padding),
		)
	}
}