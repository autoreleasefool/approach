package ca.josephroque.bowlingcompanion.feature.gearform

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearForm
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBar
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import kotlinx.coroutines.launch
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

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						GearFormScreenEvent.Dismissed -> onDismiss()
						is GearFormScreenEvent.EditAvatar ->
							onEditAvatar(it.avatar) {
								viewModel.handleAction(GearFormScreenUiAction.UpdatedAvatar(it))
							}
						is GearFormScreenEvent.EditOwner ->
							onEditOwner(it.owner) { ids ->
								viewModel.handleAction(GearFormScreenUiAction.UpdatedOwner(ids.firstOrNull()))
							}
					}
				}
		}
	}

	GearFormScreen(
		state = gearFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GearFormScreen(
	state: GearFormScreenUiState,
	onAction: (GearFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(GearFormScreenUiAction.LoadGear)
	}

	BackHandler {
		onAction(GearFormScreenUiAction.GearFormAction(GearFormUiAction.BackClicked))
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			GearFormTopBar(
				state = when (state) {
					GearFormScreenUiState.Loading -> GearFormTopBarUiState()
					is GearFormScreenUiState.Create -> state.topBar
					is GearFormScreenUiState.Edit -> state.topBar
			  },
				onAction = { onAction(GearFormScreenUiAction.GearFormAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			GearFormScreenUiState.Loading -> Unit
			is GearFormScreenUiState.Create ->
				GearForm(
					state = state.form,
					onAction = { onAction(GearFormScreenUiAction.GearFormAction(it)) },
					modifier = Modifier.padding(padding),
				)
			is GearFormScreenUiState.Edit ->
				GearForm(
					state = state.form,
					onAction = { onAction(GearFormScreenUiAction.GearFormAction(it)) },
					modifier = Modifier.padding(padding),
				)
		}
	}
}