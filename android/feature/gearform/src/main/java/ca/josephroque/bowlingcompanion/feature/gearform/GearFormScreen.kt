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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.navigation.AvatarFormResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearForm
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBar
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun GearFormRoute(
	onDismiss: () -> Unit,
	onEditAvatar: (Avatar) -> Unit,
	onEditOwner: (BowlerID?, ResourcePickerResultKey) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GearFormViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
	avatarFormResultViewModel: AvatarFormResultViewModel = hiltViewModel(),
) {
	val gearFormScreenState = viewModel.uiState.collectAsState().value

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(GEAR_FORM_OWNER_PICKER_RESULT_KEY) { BowlerID(it) }
			.onEach { viewModel.handleAction(GearFormScreenUiAction.UpdatedOwner(it.firstOrNull())) }
			.launchIn(this)

		avatarFormResultViewModel.getAvatar()
			.onEach { viewModel.handleAction(GearFormScreenUiAction.UpdatedAvatar(it)) }
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						GearFormScreenEvent.Dismissed -> onDismiss()
						is GearFormScreenEvent.EditAvatar ->
							onEditAvatar(it.avatar)
						is GearFormScreenEvent.EditOwner ->
							onEditOwner(it.owner, GEAR_FORM_OWNER_PICKER_RESULT_KEY)
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
